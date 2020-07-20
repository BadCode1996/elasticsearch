package com.code.index;

import com.code.Application;
import com.code.bean.Item;
import com.code.repository.ItemRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Srd
 * @date 2020/7/21  1:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class QueryIndexTest {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private ItemRepository repository;

    /**
     * 查询所有
     */
    @Test
    public void findAll() {
        Iterable<Item> items = repository.findAll();
        for (Item item : items) {
            System.out.println(item);
        }
    }

    /**
     * 自定义查询
     */
    @Test
    public void findByPriceBetween() {
        List<Item> items = repository.findByPriceBetween(1000.00, 4000.00);
        for (Item item : items) {
            System.out.println(item);
        }
    }

    /**
     * 词条查询
     */
    @Test
    public void search() {
//        利用构造器NativeSearchQueryBuilder构建NativeSearchQuery
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "华为手机"))
                .build();
        /*
            ElasticsearchRestTemplate.search()
                参数1：本机查询的构造
                参数2：index的类
                参数3：再次声明库名（可选）
         */
        SearchHits<Item> search = restTemplate.search(queryBuilder, Item.class);
        for (SearchHit<Item> itemSearchHit : search) {
            System.out.println(itemSearchHit.getContent());
        }
    }

    /**
     * 分页查询
     */
    @Test
    public void searchByPage() {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "华为手机"))
                /*
                    添加分页
                    Pageable的实现类PageRequest的of方法
                        参数1：当前页
                        参数2：要返回的页面大小，必须大于0
                        参数3：排序参数（可选）,Sort.Direction.DESC降序 Sort.Direction.ASC升序
                 */
                .withPageable(PageRequest.of(1, 2))
                .build();

        SearchHits<Item> search = restTemplate.search(queryBuilder, Item.class);
        System.out.println("总条目数：" + search.getTotalHits());
        for (SearchHit<Item> itemSearchHit : search) {
            System.out.println(itemSearchHit.getContent());
        }
    }

    /**
     * 排序查询
     */
    @Test
    public void searchBySort() {
        NativeSearchQuery queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "华为手机"))
                .withPageable(PageRequest.of(0, 4))
                /*
                    排序
                    fieldSort(String field) 根据字段排序
                 */
                .withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC))
                .build();
        SearchHits<Item> search = restTemplate.search(queryBuilder, Item.class);
        for (SearchHit<Item> itemSearchHit : search) {
            System.out.println(itemSearchHit.getContent());
        }
    }

    /**
     * 聚合查询
     */
    @Test
    public void testAggs() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    /*
        聚合可以用多个，所以add
            terms：词条聚合,传入词条名称
            field：聚合字段
     */
        NativeSearchQuery query = queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"))
                /*
                    结果集过滤，这里设置不需要结果集，不添加包含与不包含，会自动生成length为0的数组
                 */
                .withSourceFilter(new FetchSourceFilterBuilder().build())
                .build();
        SearchHits<Item> items = restTemplate.search(query, Item.class);
//        获取聚合结果集
        Aggregations aggregations = items.getAggregations();
        assert aggregations != null;
//        因为结果为字符串类型，所以使用ParsedStringTerms接收
        ParsedStringTerms brands = aggregations.get("brands");
//        获取桶
        brands.getBuckets().forEach(bucket -> {
//            getKeyAsString()：获取桶中的key
//            getDocCount()：获取记录数
            System.out.println(bucket.getKeyAsString() + "\t" + bucket.getDocCount());
        });
    }

    /**
     * 嵌套聚合查询
     */
    @Test
    public void testSubAggs() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        NativeSearchQuery query = queryBuilder
                .addAggregation(AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("avg_price").field("price")))
                .withSourceFilter(new FetchSourceFilterBuilder().build())
                .build();
        SearchHits<Item> itemSearchHits = restTemplate.search(query, Item.class);
        Aggregations aggregations = itemSearchHits.getAggregations();
        assert aggregations != null;
        ParsedStringTerms brands = aggregations.get("brands");
        brands.getBuckets().forEach(bucket -> {
            System.out.print("品牌名：" + bucket.getKeyAsString() + "\t数量：" + bucket.getDocCount());
            ParsedAvg avg_price = bucket.getAggregations().get("avg_price");
            System.out.println("\t平均价格：" + avg_price.getValue());
        });
    }
}

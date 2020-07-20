package com.code.index;

import com.code.Application;
import com.code.bean.Item;
import com.code.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Srd
 * @date 2020/7/16  17:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CreateIndexTest {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private ItemRepository repository;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() {
//        设置索引信息（绑定实体类），返回IndexOperations
        IndexOperations indexOperations = restTemplate.indexOps(Item.class);
//        创建索引
        indexOperations.create();
//        创建索引映射
        Document mapping = indexOperations.createMapping();
//        写入索引
        indexOperations.putMapping(mapping);
    }

    /**
     * 删除索引
     */
    @Test
    public void deleteIndex() {
//    设置索引信息（绑定实体类），返回IndexOperations
        IndexOperations indexOperations = restTemplate.indexOps(Item.class);
//    删除索引
        indexOperations.delete();
    }

    /**
     * 新增文档
     */
    @Test
    public void save() {
        Item item = new Item(1L, "华为手机", "手机", "华为", 4999.00, "http://image.badcode.icu/13123.jpg");
        repository.save(item);
    }

    /**
     * 批量新增文档
     */
    @Test
    public void saveAll() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机R1", " 手机", "锤子", 3999.00, "http://image.badcode.icu/13123.jpg"));
        list.add(new Item(3L, "小米手机", " 手机", "小米", 1999.00, "http://image.badcode.icu/13123.jpg"));
        list.add(new Item(4L, "iphone手机", " 手机", "Apple", 8999.00, "http://image.badcode.icu/13123.jpg"));
        list.add(new Item(5L, "华为V30", "手机", "华为", 3999.00, "http://image.badcode.icu/13123.jpg"));
        list.add(new Item(6L, "华为P10", " 手机", "华为", 2999.00, "http://image.badcode.icu/13123.jpg"));
        list.add(new Item(7L, "小米X5", " 手机", "小米", 1999.00, "http://image.badcode.icu/13123.jpg"));
        list.add(new Item(8L, "iphone11", " 手机", "Apple", 5999.00, "http://image.badcode.icu/13123.jpg"));
        repository.saveAll(list);
    }

    /**
     * 修改文档
     */
    @Test
    public void update() {
        Item item = new Item(4L, "苹果手机", " 手机", "Apple", 8999.00, "http://image.badcode.icu/13123.jpg");
        repository.save(item);
    }
}

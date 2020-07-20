package com.code.index;

import com.code.Application;
import com.code.repository.ItemRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Srd
 * @date 2020/7/21  2:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestQuery {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private ItemRepository repository;

}

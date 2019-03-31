package com.chengxuunion.test.demo.dao;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author youpanpan
 * @date: 2019-03-15 10:00
 * @since v1.0
 */
@Component
public class DemoDao {

    public List<String> findAll() {
        return Arrays.asList("1", "2", "3", "4", "5");
    }
}

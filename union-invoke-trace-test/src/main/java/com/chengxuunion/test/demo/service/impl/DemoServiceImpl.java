package com.chengxuunion.test.demo.service.impl;

import com.chengxuunion.test.demo.dao.DemoDao;
import com.chengxuunion.test.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author youpanpan
 * @date: 2019-03-13 15:28
 * @since v1.0
 */
@Service
public class DemoServiceImpl extends ArrayList implements DemoService {

    @Autowired
    private DemoDao demoDao;

    @Autowired
    private OtherServiceImpl otherService;

    @Override
    public List<String> findAll(String str, Integer i) {
        test();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        otherService.find();
        return demoDao.findAll();
    }

    public void test() {
        System.out.println("123");
    }
}

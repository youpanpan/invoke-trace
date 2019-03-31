package com.chengxuunion.test.demo.service.impl;

import org.springframework.stereotype.Service;

/**
 * @author youpanpan
 * @date: 2019-03-18 13:46
 * @since v1.0
 */
@Service
public class OtherServiceImpl {

    public void find() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

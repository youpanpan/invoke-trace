package com.chengxuunion.util.string.template.impl;

import java.util.Map;

/**
 * 实现基本属性的获取
 * TODO  待实现复杂属性内部属性的获取
 * @title:      使用Map对象填充模板
 * @fileName:   MapStringWriter.java
 * @description:
 * @author:     游盼盼
 * @date:       2018年4月26日
 * @version:    V1.0
 */
public class MapStringWriter extends AbstractStringWriter<Map<String, Object>> {

    /**
     * 模板数据，例{item: 氨氮, value: 2.4, standardName: 一级标准}
     */
    private Map<String, Object> dataMap = null;

    /**
     * 构造函数
     *
     * @param dataObj   模板数据
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public MapStringWriter(Map<String, Object> dataObj) {
        this.dataMap = dataObj;
    }

    /**
     * 构造函数
     *
     * @param dataObj   模板数据
     * @param notFindOut    没有找到值时，是否输出原配置，为true则输出，false则输出空字符
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public MapStringWriter(Map<String, Object> dataObj, boolean notFindOut) {
        this.dataMap = dataObj;
        this.notFindOut = notFindOut;
    }

    @Override
    public String writeInternal(String template, String variable) {
        variable = variable.trim();

        // 没有该属性
        if (!dataMap.containsKey(variable)) {
            return notFindOut ? template : "";
        }

        // 找到该属性
        Object value = dataMap.get(variable);

        // 属性的值不为null
        if (value != null) {
            return value.toString();
        } else {

            // 属性值为null，则判断是否输出原配置
            return notFindOut ? template : "";
        }
    }

    /**
     * @see com.szboanda.emdc.common.util.template.IStringWriter#setTempalteData(java.lang.Object)
     */
    @Override
    public void setTempalteData(Map<String, Object> data) {
        this.dataMap = data;

    }
}

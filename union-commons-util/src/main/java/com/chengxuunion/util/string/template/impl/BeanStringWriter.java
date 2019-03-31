package com.chengxuunion.util.string.template.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Locale;

/**
 *
 * 实现基本类型的数据获取
 * TODO  待实现复杂类型字段的数据获取
 *
 * @title:      使用实体对象填充模板
 * @fileName:   BeanStringWriter.java
 * @description:
 * @author:     游盼盼
 * @date:       2018年4月26日
 * @version:    V1.0
 */
public class BeanStringWriter extends AbstractStringWriter<Object> {

    /**
     * 实体对象,例{item: 氨氮, value: 2.4, standardName: 一级标准}
     */
    private Object dataObj;

    /**
     * 构造函数
     *
     * @param dataObj   实体对象(模板数据)
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public BeanStringWriter(Object dataObj) {
        this.dataObj = dataObj;
    }

    /**
     * 构造函数
     * @param dataObj   实体对象(模板数据)
     * @param notFindOut    没有找到值时，是否输出原配置，为true则输出，false则输出空字符
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public BeanStringWriter(Object dataObj, boolean notFindOut) {
        this.dataObj = dataObj;
        this.notFindOut = notFindOut;
    }

    /**
     * @see com.szboanda.emdc.common.util.template.impl.AbstractStringWriter#writeInternal(java.lang.String, java.lang.String)
     */
    @Override
    public String writeInternal(String template, String variable) {

        Field field = null;
        try {
            field = dataObj.getClass().getDeclaredField(variable);
        } catch (Exception e) {
            return notFindOut ? template : "";
        }

        field.setAccessible(true);
        String getMethodName = getGetMethodName(field, variable);
        try {
            Method method = dataObj.getClass().getDeclaredMethod(getMethodName, new Class[] {});
            Object value = method.invoke(dataObj, new Object[] {});
            if (value == null) {
                return notFindOut ? template : "";
            } else {
                return value.toString();
            }
        } catch (Exception e) {
            return notFindOut ? template : "";
        }

    }

    @SuppressWarnings("rawtypes")
    private String getGetMethodName(Field field, String fieldName) {
        String newName = fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
        Type fieldType = field.getGenericType();
        if (fieldType instanceof Class) {
            Class fieldClass = (Class) fieldType;
            if (fieldClass == boolean.class || fieldClass == Boolean.class) {
                return "is" + newName;
            } else {
                return "get" + newName;
            }
        }

        return "get" + newName;
    }

    /**
     * @see com.szboanda.emdc.common.util.template.IStringWriter#setTempalteData(java.lang.Object)
     */
    @Override
    public void setTempalteData(Object data) {
        this.dataObj = data;

    }
}

package com.chengxuunion.util.string.template;

/**
 * @title:      字符串写入接口
 * @fileName:   IStringWrite.java
 * @description:
 * @author:     游盼盼
 * @date:       2018年4月26日
 * @version:    V1.0
 */
public interface IStringWriter<T> {

    /**
     * 使用数据填充模板中的变量并返回填充后的结果
     *
     * @param template  模板字符串,例  如果${item}的浓度大于${value},则执行${standardName}
     * @return  填充模板数据后的内容， 例 如果氨氮的浓度大于2.4,则执行一级标准
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    String write(String template);

    /**
     * 设置装饰对象
     *
     * @param delegate  装饰对象
     * @return  装饰对象
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    @SuppressWarnings("rawtypes")
    IStringWriter setDelegate(IStringWriter delegate);

    /**
     * 设置模板数据
     *
     * @param data
     *
     * @author  游盼盼
     * @since   2018年5月3日
     * @version V1.0
     */
    void setTempalteData(T data);

}

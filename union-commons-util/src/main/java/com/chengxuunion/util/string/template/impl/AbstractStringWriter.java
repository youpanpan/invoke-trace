package com.chengxuunion.util.string.template.impl;

import com.chengxuunion.util.string.template.IStringWriter;
import com.chengxuunion.util.string.template.constants.TemplateConstants;

/**
 * @title:      字符串写入抽象接口
 * @fileName:   AbstractStringWrite.java
 * @description:
 * @author:     游盼盼
 * @date:       2018年4月26日
 * @version:    V1.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractStringWriter<T> implements IStringWriter<T> {

    /**
     * 字符串写入装饰对象
     */
    protected IStringWriter delegate;

    /**
     * 没有找到值时，是否输出原配置，为true则输出，false则输出空字符
     * 默认为true
     */
    protected boolean notFindOut = true;

    /**
     * 构造函数
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public AbstractStringWriter() {

    }

    /**
     * 传入装饰对象构造函数
     *
     * @param delegate  装饰对象
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public AbstractStringWriter(IStringWriter delegate) {
        this.delegate = delegate;
    }

    @Override
    public String write(String template) {
        if (delegate != null) {
            template = delegate.write(template);
        }

        int offset = 0;
        int start = template.indexOf(TemplateConstants.OPEN_TOKEN, offset);
        if (start == -1) {
            return template;
        }

        StringBuilder fillBuilder = new StringBuilder();

        while (start > -1) {

            fillBuilder.append(template.substring(offset, start));

            // 如果当前找到的是转义字符，那么不处理
            if (start > 0 && template.charAt(start - 1) == '\\') {
                start = template.indexOf(TemplateConstants.OPEN_TOKEN, start + TemplateConstants.OPEN_TOKEN.length());
                offset = start + TemplateConstants.OPEN_TOKEN.length();
                continue;
            }

            offset = start + TemplateConstants.OPEN_TOKEN.length();
            int end = template.indexOf(TemplateConstants.CLOSE_TOKEN, offset);
            if (end > -1) {
                String variable = template.substring(offset, end);

                // 填充数据
                String childTemplate = template.substring(offset - TemplateConstants.OPEN_TOKEN.length(), end
                        + TemplateConstants.CLOSE_TOKEN.length());
                fillBuilder.append(writeInternal(childTemplate, variable));

                offset = end + TemplateConstants.CLOSE_TOKEN.length();

                start = template.indexOf(TemplateConstants.OPEN_TOKEN, offset);
            } else {
                break;
            }

        }

        // 拼接剩余的字符串
        fillBuilder.append(template.substring(offset, template.length()));

        return fillBuilder.toString();
    }

    /**
     * @param delegate the delegate to set
     */
    @Override
    public IStringWriter setDelegate(IStringWriter delegate) {
        this.delegate = delegate;

        return this.delegate;
    }

    /**
     * @return the notFindOut
     */
    public boolean isNotFindOut() {
        return notFindOut;
    }

    /**
     * 获取配置变量的值（每个子类必须实现自己的填充功能）
     *
     * @param template  模板，例${userName}
     * @param variable  变量，例userName
     * @return
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public abstract String writeInternal(String template, String variable);

}

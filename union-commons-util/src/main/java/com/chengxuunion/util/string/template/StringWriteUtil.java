package com.chengxuunion.util.string.template;

import java.util.Map;

import com.chengxuunion.util.string.template.constants.TemplateConstants;


/**
 * @title:      字符串写入工具类
 * @fileName:   StringWriteUtil.java
 * @description:
 * @author:     游盼盼
 * @date:       2018年4月26日
 * @version:    V1.0
 */
public class StringWriteUtil {

    /**
     * 将数据写入到模板中,并输出填充数据后的结果
     *
     * @param template  模板，例  如果${item}的浓度大于${value},则执行${standardName}
     * @param templateData  模板数据，例{item: 氨氮, value: 2.4, standardName: 一级标准}
     * @return  填充模板数据后的内容， 例 如果氨氮的浓度大于2.4,则执行一级标准
     *
     * @author  游盼盼
     * @since   2018年4月26日
     * @version V1.0
     */
    public static String writeByMap(String template, Map<String, Object> templateData) {
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
                Object value = templateData.get(variable.trim());
                if (value != null) {
                    fillBuilder.append(value.toString());
                } else {

                    // 不过没有找到该变量，则直接写入原字符串
                    fillBuilder.append(template.substring(offset - TemplateConstants.OPEN_TOKEN.length(), end
                            + TemplateConstants.CLOSE_TOKEN.length()));
                }

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

}

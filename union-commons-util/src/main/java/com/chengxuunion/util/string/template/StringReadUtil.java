package com.chengxuunion.util.string.template;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.chengxuunion.util.string.template.constants.TemplateConstants;


/**
 * @title:      字符串读取工具类
 * @fileName:   StringReadUtil.java
 * @description:
 * @author:     游盼盼
 * @date:       2018年4月26日
 * @version:    V1.0
 */
public class StringReadUtil {

    /**
     * 根据模板解析模板中的每个字段的值并存储到map中，map的key: 模板变量名, value: 从src中解析出的模板值
     *
     * @param src   源字符串,例 04-25 08
     * @param template  模板字符串,例 ${month}-${day} ${hour}
     * @return  模板中定义变量值的集合，例{month:04, day: 25, hour: 08}
     *
     * @author  游盼盼
     * @since   2018年4月25日
     * @version V1.0
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> readByTemplate(String src, String template) {
        int offset = 0;
        int start = template.indexOf(TemplateConstants.OPEN_TOKEN, offset);
        if (start == -1) {
            return Collections.EMPTY_MAP;
        }

        int srcOffset = 0;
        Map<String, String> variableMap = new HashMap<String, String>();

        while (start > -1) {

            // 如果当前找到的是转义字符，那么不处理
            if (start > 0 && template.charAt(start - 1) == '\\') {
                start = template.indexOf(TemplateConstants.OPEN_TOKEN, start + TemplateConstants.OPEN_TOKEN.length());
                if (start > -1) {
                    srcOffset = start - offset;
                }
                continue;
            }
            offset = start + TemplateConstants.OPEN_TOKEN.length();
            int end = template.indexOf(TemplateConstants.CLOSE_TOKEN, offset);
            if (end > -1) {
                String variable = template.substring(offset, end);

                offset = end + TemplateConstants.CLOSE_TOKEN.length();

                start = template.indexOf(TemplateConstants.OPEN_TOKEN, offset);

                String value = "";
                if (start > -1) {
                    // 分隔的字符串
                    String splitStr = template.substring(offset, start);
                    int srcEnd = src.indexOf(splitStr, srcOffset);
                    value = src.substring(srcOffset, srcEnd);
                    srcOffset = srcEnd + splitStr.length();
                } else {
                    value = src.substring(srcOffset, src.length());
                }

                variableMap.put(variable, value);
            } else {
                break;
            }

        }

        return variableMap;
    }

}

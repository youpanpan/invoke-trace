package com.chengxuunion.invoketrace.business.tracemethod.model.request;

import com.chengxuunion.invoketrace.common.model.PageParam;

/**
 * @author youpanpan
 * @date: 2019-03-18 09:32
 * @since v1.0
 */
public class TraceMethodPageParam  extends PageParam{

    private String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}

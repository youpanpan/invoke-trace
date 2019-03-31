package com.chengxuunion.invoketrace.storage;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;

/**
 * 调用跟踪记录存储
 *
 * @author youpanpan
 * @date: 2019-03-13 17:19
 * @since v1.0
 */
public interface InvokeTraceStorage {

    /**
     * 保存方法调用跟踪记录
     *
     * @param traceMethod   方法调用跟踪对象
     */
    void saveInvokeTrace(TraceMethod traceMethod);
}

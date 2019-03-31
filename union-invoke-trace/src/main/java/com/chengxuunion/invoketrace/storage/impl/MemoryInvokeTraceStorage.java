package com.chengxuunion.invoketrace.storage.impl;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.storage.InvokeTraceStorage;
import com.chengxuunion.invoketrace.storage.MemoryStorageContainer;
import org.springframework.stereotype.Component;

/**
 * 内存存储实现类
 *
 * @author youpanpan
 * @date: 2019-03-13 17:23
 * @since v1.0
 */
@Component
public class MemoryInvokeTraceStorage implements InvokeTraceStorage {

    @Override
    public void saveInvokeTrace(TraceMethod traceMethod) {
        MemoryStorageContainer.getInstance().putTraceMethod(traceMethod);
    }
}

package com.chengxuunion.invoketrace.storage;

import com.chengxuunion.invoketrace.common.util.ApplicationContextHelper;
import com.chengxuunion.invoketrace.storage.impl.DataBaseInvokeTraceStorage;
import com.chengxuunion.invoketrace.storage.impl.MemoryInvokeTraceStorage;

/**
 * 方法调用存储工厂类
 *
 * @author youpanpan
 * @date: 2019-03-14 10:04
 * @since v1.0
 */
public class InvokeTraceStorageFactory {

    /**
     * 根据存储实现全类名获取存储对象
     *
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static InvokeTraceStorage getInvokeTraceStorage(String className) throws ClassNotFoundException{
        Class<?> clazz = Class.forName(className);
        if (InvokeTraceStorage.class.isAssignableFrom(clazz)) {
            return (InvokeTraceStorage) ApplicationContextHelper.getBean(clazz);
        }

        return null;
    }

    /**
     * 根据存储类型获取存储对象
     *
     * @param invokeTraceStorageType    存储类型
     * @return
     */
    public static InvokeTraceStorage getInvokeTraceStorage(InvokeTraceStorageType invokeTraceStorageType) {
        InvokeTraceStorage invokeTraceStorage = null;
        if (invokeTraceStorageType == InvokeTraceStorageType.MEMORY) {
            invokeTraceStorage = ApplicationContextHelper.getBean(MemoryInvokeTraceStorage.class);
        } else if (invokeTraceStorageType == InvokeTraceStorageType.DATABASE) {
            invokeTraceStorage = ApplicationContextHelper.getBean(DataBaseInvokeTraceStorage.class);
        }

        return invokeTraceStorage;
    }
}

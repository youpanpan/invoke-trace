package com.chengxuunion.invoketrace.storage;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 内存存储容器
 *
 * @author youpanpan
 * @date: 2019-03-14 09:17
 * @since v1.0
 */
public class MemoryStorageContainer {

    /**
     * key: 方法签名，value：方法对象
     */
    private Map<String, LinkedList<TraceMethod>> traceMethodMap = new ConcurrentHashMap<>();

    /**
     * 单例对象
     */
    private static MemoryStorageContainer instance;

    /**
     * 只保留最近的多少次记录
     */
    private static final int MAX_SIZE = 20;

    private MemoryStorageContainer() {

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static MemoryStorageContainer getInstance() {
        if (instance == null) {
            synchronized (MemoryStorageContainer.class) {
                if (instance == null) {
                    instance = new MemoryStorageContainer();
                }
            }
        }

        return instance;
    }

    /**
     * put方法跟踪对象
     *
     * @param traceMethod
     */
    public void putTraceMethod(TraceMethod traceMethod) {
        String methodSign = traceMethod.getFullName();
        if (!traceMethodMap.containsKey(methodSign)) {
            traceMethodMap.put(methodSign, new LinkedList<>());
        }

        // 每个方法的跟踪记录数超过设置的值，则丢弃第一个值
        if (traceMethodMap.get(methodSign).size() >= MAX_SIZE) {
            traceMethodMap.get(methodSign).removeFirst();
        }
        traceMethodMap.get(methodSign).addLast(traceMethod);
    }

    /**
     * 获取内存中已有的跟踪记录列表
     * 这里之所有这么做，因为在遍历的时候不允许操作，所以这里需要通过迭代的方式去获取数据
     * @return
     */
    public Map<String, LinkedList<TraceMethod>> getTraceMethodMap() {
        Map<String, LinkedList<TraceMethod>> map = new HashMap<>();
        for (Map.Entry<String, LinkedList<TraceMethod>> entry : traceMethodMap.entrySet()) {
            Iterator<TraceMethod> iterator =  entry.getValue().iterator();
            LinkedList<TraceMethod> traceMethodList = new LinkedList<>();
            while(iterator.hasNext()) {
                TraceMethod traceMethod = iterator.next();
                traceMethodList.add(traceMethod);
            }
            map.put(entry.getKey(), traceMethodList);
        }
        return map;
    }
}

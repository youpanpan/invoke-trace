package com.chengxuunion.invoketrace.processor;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.common.util.AspectMethodUtil;
import com.chengxuunion.invoketrace.common.util.InvokeTracePropertiesUtils;
import com.chengxuunion.invoketrace.storage.InvokeTraceStorage;
import com.chengxuunion.invoketrace.storage.InvokeTraceStorageFactory;
import com.chengxuunion.invoketrace.storage.InvokeTraceStorageType;
import com.chengxuunion.invoketrace.storage.impl.MemoryInvokeTraceStorage;
import com.chengxuunion.util.collection.CollectionUtils;
import com.chengxuunion.util.string.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 调用切点处理
 *
 * @author youpanpan
 * @date: 2019-03-13 17:53
 * @since v1.0
 */
public class EnableInvokePoint {

    private static final Map<Long, Stack<TraceMethod>> traceMethodMap = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(EnableInvokePoint.class);
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private EnableInvokePoint() {

    }

    /**
     * 方法执行之前
     *
     * @param pjp
     * @param identify
     */
    public static void before(ProceedingJoinPoint pjp, long identify) {
        long threadId = Thread.currentThread().getId();
        executor.submit(() -> {
            TraceMethod traceMethod = AspectMethodUtil.getTraceMethod(pjp, identify, threadId);
            pushMap(traceMethod);
        });
    }

    /**
     * 方法执行之后
     *
     * @param pjp
     * @param identify
     * @param obj
     */
    public static void after(ProceedingJoinPoint pjp, long identify, Object obj) {
        long threadId = Thread.currentThread().getId();
        String returnType = obj != null ? obj.getClass().getName() : null;
        executor.submit(() -> {
            popMap(identify, threadId, returnType);
        });
    }

    /**
     * 方法入栈
     *
     * @param traceMethod
     */
    private static void pushMap(TraceMethod traceMethod) {
        Stack<TraceMethod> traceMethodStack = traceMethodMap.get(traceMethod.getThreadId());
        if (traceMethodStack == null) {
            traceMethodStack = new Stack<>();
        }
        traceMethodStack.add(traceMethod);
        traceMethodMap.put(traceMethod.getThreadId(), traceMethodStack);
    }

    /**
     * 方法出栈
     *
     * @param identify
     * @param threadId
     * @param returnType
     */
    private static void popMap(long identify, long threadId, String returnType) {
        Stack<TraceMethod> traceMethodStack = traceMethodMap.get(threadId);
        logger.info(traceMethodMap.toString());
        if (traceMethodStack == null || traceMethodStack.isEmpty()) {
            return;
        }

        TraceMethod traceMethod = traceMethodStack.pop();
        traceMethod.setReturnType(returnType);
        traceMethod.setEndTimestamp(System.currentTimeMillis());

        if (!traceMethodStack.isEmpty()) {
            TraceMethod method = traceMethodStack.pop();
            List<TraceMethod> list = method.getTraceMethodList();
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(traceMethod);
            traceMethod.setParentId(method.getId());
            method.setTraceMethodList(list);

            setTraceMethodIdentify(traceMethod, method.getIdentify());
            traceMethodStack.push(method);
        } else {
            traceMethod.setParentId(0L);

            String className = MemoryInvokeTraceStorage.class.getName();

            if (InvokeTracePropertiesUtils.getInstance() != null) {
                className = InvokeTracePropertiesUtils.getInstance().getValue("storage.class");
                if (StringUtils.isEmpty(className)) {
                    className = MemoryInvokeTraceStorage.class.getName();
                }
            }
            logger.info(className);
            try {
                InvokeTraceStorage invokeTraceStorage = InvokeTraceStorageFactory.getInvokeTraceStorage(className);
                if (invokeTraceStorage != null) {
                    invokeTraceStorage.saveInvokeTrace(traceMethod);
                } else {
                    logger.error("调用记录存储实现类必须实现InvokeTraceStorage接口");
                }
            } catch (ClassNotFoundException e) {
                logger.error(className + "不存在", e);
            }
        }
    }

    /**
     * 递归设置前方法及子方法的identify
     *
     * @param traceMethod
     * @param identify
     */
    private static void setTraceMethodIdentify(TraceMethod traceMethod, Long identify) {
        traceMethod.setIdentify(identify);

        if (CollectionUtils.isNotEmpty(traceMethod.getTraceMethodList())) {
            for (TraceMethod method : traceMethod.getTraceMethodList()) {
                setTraceMethodIdentify(method, identify);
            }
        }
    }
}

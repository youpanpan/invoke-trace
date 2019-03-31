package com.chengxuunion.invoketrace.business.tracemethod.service;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.business.tracemethod.model.request.TraceMethodPageParam;
import com.chengxuunion.invoketrace.common.model.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author youpanpan
 * @date: 2019-03-18 09:30
 * @since v1.0
 */
public interface TraceMethodService {

    /**
     * 分页查询方法跟踪记录
     *
     * @param traceMethodPageParam
     * @return
     */
    PageResult<TraceMethod> listTraceMethod(TraceMethodPageParam traceMethodPageParam);

    /**
     * 获取方法调用时间统计
     *
     * @param fullName
     * @param number
     * @return
     */
    Map<String, Object> getTraceMethodStatistics(String fullName, Integer number);

    /**
     * 根据方法全名称获取方法信息
     *
     * @param fullName
     * @return
     */
    TraceMethod getTraceMethod(String fullName);

}

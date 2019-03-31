package com.chengxuunion.invoketrace.storage.impl;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.common.util.JdbcUtils;
import com.chengxuunion.invoketrace.storage.InvokeTraceStorage;
import com.chengxuunion.util.collection.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据库存储实现类
 *
 * @author youpanpan
 * @date: 2019-03-13 17:43
 * @since v1.0
 */
@Component
public class DataBaseInvokeTraceStorage implements InvokeTraceStorage {

    private Logger logger = LoggerFactory.getLogger(DataBaseInvokeTraceStorage.class);

    @Override
    public void saveInvokeTrace(TraceMethod traceMethod) {
        List<TraceMethod> traceMethodList = getTraceMethod(traceMethod);
        int size = traceMethodList.size();
        JdbcUtils jdbcUtils = new JdbcUtils();
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("INSERT INTO trace_method ( ");
            sqlBuilder.append("id, class_name, method_name, full_name, super_class_name,");
            sqlBuilder.append("interfaces, parameter_types, modifier, return_type, request_url,");
            sqlBuilder.append("parent_id, create_date, start_timestamp, end_timestamp, identify");
            sqlBuilder.append(" ) VALUES  ");
            for (int i = 0; i < size; i++) {
                sqlBuilder.append(" ( ");
                sqlBuilder.append("?, ?, ?, ?, ?,");
                sqlBuilder.append("?, ?, ?, ?, ?,");
                sqlBuilder.append("?, ?, ?, ?, ?");
                sqlBuilder.append(" ) ");
                if (i != size - 1) {
                    sqlBuilder.append(",");
                }
            }

            List<Object> paramList = new LinkedList<>();
            for (TraceMethod method : traceMethodList) {
                paramList.add(method.getId());
                paramList.add(method.getClassName());
                paramList.add(method.getMethodName());
                paramList.add(method.getFullName());
                paramList.add(method.getSuperClassName());
                paramList.add(method.getInterfaces());
                paramList.add(method.getParameterTypes());
                paramList.add(method.getModifier());
                paramList.add(method.getReturnType());
                paramList.add(method.getRequestUrl());
                paramList.add(method.getParentId());
                paramList.add(method.getCreateDate());
                paramList.add(method.getStartTimestamp());
                paramList.add(method.getEndTimestamp());
                paramList.add(method.getIdentify());
            }

            logger.info(sqlBuilder.toString());
            jdbcUtils.execute(sqlBuilder.toString(), paramList);
        } catch (Exception e) {
            logger.error("保存方法调用信息出现异常", e);
        } finally {
            jdbcUtils.close();
        }
    }

    /**
     * 递归获取指定方法及其下的所有的方法调用信息
     *
     * @param traceMethod
     * @return
     */
    private List<TraceMethod> getTraceMethod(TraceMethod traceMethod) {
        List<TraceMethod> traceMethodList = new ArrayList<>();
        traceMethodList.add(traceMethod);

        if (CollectionUtils.isNotEmpty(traceMethod.getTraceMethodList())) {
            for (TraceMethod childTraceMethod : traceMethod.getTraceMethodList()) {
                traceMethodList.addAll(getTraceMethod(childTraceMethod));
            }
        }

        return traceMethodList;
    }
}

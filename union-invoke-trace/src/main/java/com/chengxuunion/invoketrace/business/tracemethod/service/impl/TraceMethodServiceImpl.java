package com.chengxuunion.invoketrace.business.tracemethod.service.impl;

import com.chengxuunion.invoketrace.business.tracemethod.dao.TraceMethodDao;
import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.business.tracemethod.model.request.TraceMethodPageParam;
import com.chengxuunion.invoketrace.business.tracemethod.service.TraceMethodService;
import com.chengxuunion.invoketrace.common.model.PageResult;
import com.chengxuunion.invoketrace.common.util.InvokeTracePropertiesUtils;
import com.chengxuunion.invoketrace.storage.MemoryStorageContainer;
import com.chengxuunion.invoketrace.storage.impl.MemoryInvokeTraceStorage;
import com.chengxuunion.util.collection.CollectionUtils;
import com.chengxuunion.util.date.DateUtils;
import com.chengxuunion.util.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author youpanpan
 * @date: 2019-03-18 09:31
 * @since v1.0
 */
@Service
public class TraceMethodServiceImpl implements TraceMethodService{

    @Autowired
    private TraceMethodDao traceMethodDao;

    @Override
    public PageResult<TraceMethod> listTraceMethod(TraceMethodPageParam traceMethodPageParam) {

        if (isMemory()) {
            // 内存存储
            List<TraceMethod> traceMethodList = new ArrayList<>();
            Map<String, LinkedList<TraceMethod>> traceMethodMap = MemoryStorageContainer.getInstance().getTraceMethodMap();
            int index = 0;
            int startIndex = (traceMethodPageParam.getPageNum() -  1) * traceMethodPageParam.getPageSize();
            int endIndex = startIndex + traceMethodPageParam.getPageSize();

            for (Map.Entry<String, LinkedList<TraceMethod>> entry : traceMethodMap.entrySet()) {
                if (CollectionUtils.isNotEmpty(entry.getValue())) {
                    if (startIndex <= index && endIndex >= index
                            && (StringUtils.isEmpty(traceMethodPageParam.getMethodName())
                                || entry.getKey().contains(traceMethodPageParam.getMethodName()))) {
                        traceMethodList.add(entry.getValue().getLast());
                        index++;
                    }
                }
            }
            long totalCount = Long.parseLong(String.valueOf(index));
            return new PageResult<>(traceMethodPageParam, totalCount, traceMethodList);
        } else {
            return traceMethodDao.listTraceMethod(traceMethodPageParam);
        }

    }

    @Override
    public Map<String, Object> getTraceMethodStatistics(String fullName, Integer number) {
        Map<String, Object> statisticsMap = new LinkedHashMap<>();
        statisticsMap.put("xAxis", new LinkedList());
        statisticsMap.put("yAxis", new LinkedList());
        List<TraceMethod> traceMethodList = null;
        if (isMemory()) {
            traceMethodList = MemoryStorageContainer.getInstance().getTraceMethodMap().get(fullName);
        } else {
            // 数据库查询
            traceMethodList = traceMethodDao.listTraceMethodByFullName(fullName, number);
        }

        if (CollectionUtils.isNotEmpty(traceMethodList)) {
            for (TraceMethod traceMethod : traceMethodList) {
                ((LinkedList)(statisticsMap.get("xAxis"))).add(DateUtils.format(traceMethod.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
                ((LinkedList)(statisticsMap.get("yAxis"))).add(traceMethod.getEndTimestamp() - traceMethod.getStartTimestamp());
            }
        }
        return statisticsMap;
    }

    @Override
    public TraceMethod getTraceMethod(String fullName) {
        if (isMemory()) {
            LinkedList<TraceMethod> traceMethodLinkedList = MemoryStorageContainer.getInstance().getTraceMethodMap().get(fullName);
            if (CollectionUtils.isNotEmpty(traceMethodLinkedList)) {
                return traceMethodLinkedList.getLast();
            }
        } else {
            // 数据库查询
            List<TraceMethod> traceMethodList = traceMethodDao.listTraceMethodByFullName(fullName, 1);
            if (CollectionUtils.isNotEmpty(traceMethodList)) {
                return traceMethodList.get(0);
            }
            return null;
        }

        return null;
    }

    /**
     * 判断存储方式是否为内存存储，如果为内存则为true
     *
     * @return
     */
    private boolean isMemory() {
        if (InvokeTracePropertiesUtils.getInstance() == null
                || StringUtils.isEquals(InvokeTracePropertiesUtils.getInstance().getValue("storage.class"), MemoryInvokeTraceStorage.class.getName())) {
            return true;
        }

        return false;
    }
}

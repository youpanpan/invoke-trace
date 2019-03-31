package com.chengxuunion.invoketrace.business.tracemethod.dao;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import com.chengxuunion.invoketrace.business.tracemethod.model.request.TraceMethodPageParam;
import com.chengxuunion.invoketrace.common.model.PageResult;
import com.chengxuunion.invoketrace.common.util.JdbcUtils;
import com.chengxuunion.util.collection.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author youpanpan
 * @date: 2019-03-18 09:31
 * @since v1.0
 */
@Repository
public class TraceMethodDao {

    private Logger logger = LoggerFactory.getLogger(TraceMethodDao.class);

    public PageResult<TraceMethod> listTraceMethod(TraceMethodPageParam traceMethodPageParam) {
        StringBuilder sqlBuilder = new StringBuilder();
        long startIndex = (traceMethodPageParam.getPageNum() - 1 ) * traceMethodPageParam.getPageSize();
        long endIndex = startIndex + traceMethodPageParam.getPageSize();
        sqlBuilder.append("SELECT * FROM trace_method WHERE parent_id = 0 GROUP BY full_name LIMIT ?,? ");
        List<Object> paramList = new ArrayList<>();
        paramList.add(startIndex);
        paramList.add(endIndex);
        JdbcUtils jdbcUtils = new JdbcUtils();
        List<TraceMethod> traceMethodList = new ArrayList<>();
        try {
            List<TraceMethodDB> traceMethodDBS = jdbcUtils.selectList(sqlBuilder.toString(), paramList, TraceMethodDB.class);
            traceMethodList = convertTraceMethodList(traceMethodDBS);
        } catch (Exception e) {
            logger.error("查询方法跟踪记录出现异常", e);
        } finally {
            jdbcUtils.close();
        }

        long totalCount = Long.parseLong(String.valueOf(traceMethodList.size()));
        return new PageResult<>(totalCount, traceMethodList);
    }

    public List<TraceMethod> listTraceMethodByFullName(String fullName, Integer number) {
        StringBuilder sqlBuilder = new StringBuilder();
        long startIndex = 0;
        long endIndex = number;
        sqlBuilder.append("SELECT t.* FROM ( ");
        sqlBuilder.append("SELECT id,identify FROM trace_method WHERE ");
        sqlBuilder.append("full_name = ? ");
        sqlBuilder.append("ORDER BY create_date DESC LIMIT ?, ?) ");
        sqlBuilder.append("temp ");
        sqlBuilder.append("LEFT JOIN trace_method t ON (t.id = temp.id OR t.identify = temp.identify) ORDER BY t.create_date ASC ");
        List<Object> paramList = new ArrayList<>();
        paramList.add(fullName);
        paramList.add(startIndex);
        paramList.add(endIndex);
        JdbcUtils jdbcUtils = new JdbcUtils();
        List<TraceMethod> traceMethodList = new ArrayList<>();
        try {
            List<TraceMethodDB> traceMethodDBS = jdbcUtils.selectList(sqlBuilder.toString(), paramList, TraceMethodDB.class);
            traceMethodList = convertTraceMethodList(traceMethodDBS);
        } catch (Exception e) {
            logger.error("查询方法跟踪记录出现异常", e);
        } finally {
            jdbcUtils.close();
        }

        Map<Long, List<TraceMethod>> traceMethodParentMap = new HashMap<>();
        Map<Long, TraceMethod> traceMethodMap = new HashMap<>();
        for (TraceMethod traceMethod : traceMethodList) {
            if (!traceMethodParentMap.containsKey(traceMethod.getParentId())) {
                traceMethodParentMap.put(traceMethod.getParentId(), new ArrayList<>());
            }
            traceMethodParentMap.get(traceMethod.getParentId()).add(traceMethod);
            traceMethodMap.put(traceMethod.getId(), traceMethod);
        }
        List<TraceMethod> traceMethodRestList = new ArrayList<>();
        for (Map.Entry<Long, List<TraceMethod>> entry : traceMethodParentMap.entrySet()) {
            if (entry.getKey().equals(0L)) {
                traceMethodRestList.addAll(entry.getValue());
            } else {
                List<TraceMethod> childTraceMethodList = traceMethodMap.get(entry.getKey()).getTraceMethodList();
                if (childTraceMethodList == null) {
                    childTraceMethodList = new LinkedList<>();
                    traceMethodMap.get(entry.getKey()).setTraceMethodList(childTraceMethodList);
                }
                childTraceMethodList.addAll(entry.getValue());

            }
        }

        return traceMethodRestList;
    }

    private List<TraceMethod> convertTraceMethodList(List<TraceMethodDB> traceMethodDBS) {
        List<TraceMethod> traceMethodList = new ArrayList<>();
        for (TraceMethodDB traceMethodDB : traceMethodDBS) {
            TraceMethod traceMethod = new TraceMethod();
            traceMethod.setId(traceMethodDB.getId());
            traceMethod.setClassName(traceMethodDB.getClass_name());
            traceMethod.setMethodName(traceMethodDB.getMethod_name());
            traceMethod.setFullName(traceMethodDB.getFull_name());
            traceMethod.setSuperClassName(traceMethodDB.getSuper_class_name());
            traceMethod.setInterfaces(traceMethodDB.getInterfaces());
            traceMethod.setParameterTypes(traceMethodDB.getParameter_types());
            traceMethod.setModifier(traceMethodDB.getModifier());
            traceMethod.setReturnType(traceMethodDB.getReturn_type());
            traceMethod.setRequestUrl(traceMethodDB.getRequest_url());
            traceMethod.setParentId(traceMethodDB.getParent_id());
            traceMethod.setCreateDate(traceMethodDB.getCreate_date());
            traceMethod.setStartTimestamp(traceMethodDB.getStart_timestamp());
            traceMethod.setEndTimestamp(traceMethodDB.getEnd_timestamp());
            traceMethod.setIdentify(traceMethod.getIdentify());

            traceMethodList.add(traceMethod);
        }

        return traceMethodList;
    }

    public static class TraceMethodDB {
        private Long id;
        private String class_name;
        private String method_name;
        private String full_name;
        private String super_class_name;
        private String interfaces;
        private String parameter_types;
        private String modifier;
        private String return_type;
        private String request_url;
        private Long parent_id;
        private Date create_date;
        private Long start_timestamp;
        private Long end_timestamp;
        private Long identify;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }

        public String getMethod_name() {
            return method_name;
        }

        public void setMethod_name(String method_name) {
            this.method_name = method_name;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getSuper_class_name() {
            return super_class_name;
        }

        public void setSuper_class_name(String super_class_name) {
            this.super_class_name = super_class_name;
        }

        public String getInterfaces() {
            return interfaces;
        }

        public void setInterfaces(String interfaces) {
            this.interfaces = interfaces;
        }

        public String getParameter_types() {
            return parameter_types;
        }

        public void setParameter_types(String parameter_types) {
            this.parameter_types = parameter_types;
        }

        public String getModifier() {
            return modifier;
        }

        public void setModifier(String modifier) {
            this.modifier = modifier;
        }

        public String getReturn_type() {
            return return_type;
        }

        public void setReturn_type(String return_type) {
            this.return_type = return_type;
        }

        public String getRequest_url() {
            return request_url;
        }

        public void setRequest_url(String request_url) {
            this.request_url = request_url;
        }

        public Long getParent_id() {
            return parent_id;
        }

        public void setParent_id(Long parent_id) {
            this.parent_id = parent_id;
        }

        public Date getCreate_date() {
            return create_date;
        }

        public void setCreate_date(Date create_date) {
            this.create_date = create_date;
        }

        public Long getStart_timestamp() {
            return start_timestamp;
        }

        public void setStart_timestamp(Long start_timestamp) {
            this.start_timestamp = start_timestamp;
        }

        public Long getEnd_timestamp() {
            return end_timestamp;
        }

        public void setEnd_timestamp(Long end_timestamp) {
            this.end_timestamp = end_timestamp;
        }

        public Long getIdentify() {
            return identify;
        }

        public void setIdentify(Long identify) {
            this.identify = identify;
        }
    }
}

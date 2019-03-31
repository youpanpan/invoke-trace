package com.chengxuunion.invoketrace.business.tracemethod.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 跟踪方法实体
 *
 * @author youpanpan
 * @date: 2019-03-13 17:08
 * @since v1.0
 */
public class TraceMethod implements Serializable{

    /**
     * 序号，主键
     */
    private Long id;

    /**
     * 全类名
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法全名称,全类名.方法名称(参数列表)
     */
    private String fullName;

    /**
     * 父类全类名
     */
    private String superClassName;

    /**
     * 实现的接口名称，多个用,隔开
     */
    private String interfaces;

    /**
     * 参数类型，多个用,隔开
     */
    private String parameterTypes;

    /**
     * 方法修饰符
     */
    private String modifier;

    /**
     * 方法类型全类名
     */
    private String returnType;

    /**
     * 请求URL，针对控制器方法才有值
     */
    private String requestUrl;

    /**
     * 上级方法ID，即调用方ID，如果为0，表示该方法为最上层入口
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 线程ID
     */
    private Long threadId;

    /**
     * 方法调用开始时间戳
     */
    private Long startTimestamp;

    /**
     * 方法调用结束时间戳
     */
    private Long endTimestamp;

    /**
     * 一次调用过程唯一标识
     */
    private Long identify;

    /**
     * 被调用方方法集合
     */
    private List<TraceMethod> traceMethodList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String interfaces) {
        this.interfaces = interfaces;
    }

    public String getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Long getIdentify() {
        return identify;
    }

    public void setIdentify(Long identify) {
        this.identify = identify;
    }

    public List<TraceMethod> getTraceMethodList() {
        return traceMethodList;
    }

    public void setTraceMethodList(List<TraceMethod> traceMethodList) {
        this.traceMethodList = traceMethodList;
    }
}

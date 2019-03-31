package com.chengxuunion.invoketrace.common.util;

import com.chengxuunion.invoketrace.business.tracemethod.model.TraceMethod;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 切面工具
 *
 * @author youpanpan
 * @date: 2019-03-13 18:00
 * @since v1.0
 */
public class AspectMethodUtil {

    /**
     * 分隔符
     */
    private static final String SPLIT_STR = ",";

    private static Logger logger = LoggerFactory.getLogger(AspectMethodUtil.class);

    /**
     * 根据切面对象获取方法信息
     *
     * @param pjp
     * @param identify
     * @param threadId
     * @return
     */
    public static TraceMethod getTraceMethod(ProceedingJoinPoint pjp, long identify, long threadId) {
        TraceMethod traceMethod = new TraceMethod();
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        String parameters = getParameters(pjp);
        String superclass = getSuperclass(pjp);
        String interfaces = getInterfaces(pjp);
        Date startTime = new Date();
        String modifier = getModifier(pjp);
        String requestUrl = getRequestUrl(pjp);

        traceMethod.setId(identify);
        traceMethod.setClassName(className);
        traceMethod.setMethodName(methodName);
        traceMethod.setFullName(getFullName(className, methodName, parameters));
        traceMethod.setParameterTypes(parameters);
        traceMethod.setInterfaces(interfaces);
        traceMethod.setSuperClassName(superclass);
        traceMethod.setModifier(modifier);
        traceMethod.setThreadId(threadId);
        traceMethod.setCreateDate(startTime);
        traceMethod.setStartTimestamp(System.currentTimeMillis());
        traceMethod.setIdentify(identify);
        traceMethod.setRequestUrl(requestUrl);

        return traceMethod;
    }

    /**
     * 获取方法修饰符
     *
     * @param pjp
     * @return
     */
    public static String getModifier(ProceedingJoinPoint pjp) {
        int i = pjp.getSignature().getModifiers();
        return Modifier.toString(i);
    }

    /**
     * 获取方法实现的接口
     *
     * @param pjp
     * @return
     */
    public static String getInterfaces(ProceedingJoinPoint pjp) {
        Class[] interfaces = pjp.getSignature().getDeclaringType().getInterfaces();
        List<String> interfacesStr = new ArrayList<>();
        if (interfaces != null) {
            Arrays.stream(interfaces).forEach(s -> interfacesStr.add(s.getName()));
        }
        return StringUtils.join(interfacesStr.toArray(new String[0]), SPLIT_STR);
    }

    /**
     * 获取方法实现的父类
     *
     * @param pjp
     * @return
     */
    public static String getSuperclass(ProceedingJoinPoint pjp) {
        Class superclass = pjp.getSignature().getDeclaringType().getSuperclass();
        return superclass != null ? superclass.getName() : " ";
    }

    /**
     * 获取方法参数
     *
     * @param pjp
     * @return
     */
    public static String getParameters(ProceedingJoinPoint pjp) {
        List<String> parameterTypes = new ArrayList<>();
        for (Object object : pjp.getArgs()) {
            parameterTypes.add(Optional.ofNullable(object).map(s -> s.getClass().getName()).orElse("null"));
        }

        return StringUtils.join(parameterTypes.toArray(new String[0]), SPLIT_STR);
    }

    /**
     * 获取方法请求的url，针对Controller的请求方法才有值
     *
     * @param pjp
     * @return
     */
    public static String getRequestUrl(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature)signature;
            Method targetMethod = methodSignature.getMethod();
            try {
                Method realMethod = pjp.getTarget().getClass().getDeclaredMethod(signature.getName(), targetMethod.getParameterTypes());

                // 获取Controller类上的路径
                String urlPrefix = "";
                if (pjp.getTarget().getClass().isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = pjp.getTarget().getClass().getAnnotation(RequestMapping.class);
                    urlPrefix = StringUtils.join(requestMapping.value(), SPLIT_STR);
                }

                // 支持的注解类型
                Class[] clazzs = new Class[] {RequestMapping.class, GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class};
                for (Class clazz : clazzs) {
                    if (realMethod.isAnnotationPresent(clazz)) {
                        Annotation annotation = realMethod.getAnnotation(clazz);
                        Method valueMethod = annotation.getClass().getMethod("value", null);
                        Object value = valueMethod.invoke(annotation, null);

                        if (value != null) {
                            if (value.getClass() == String[].class) {
                                String[] values = (String[])value;
                                return urlPrefix + StringUtils.join(values, SPLIT_STR);
                            } else {
                                return urlPrefix + String.valueOf(value);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return "";
            }
        }

        return "";
    }

    /**
     * 获取方法全名称 = 类名.方法名(参数类型列表)
     *
     * @param className
     * @param methodName
     * @param parameterType
     * @return
     */
    private static String getFullName(String className, String methodName, String parameterType) {
        StringBuilder builder = new StringBuilder();
        builder.append(className);
        builder.append(".");
        builder.append(methodName);
        builder.append("(");
        String[] parameterTypes = parameterType.split(SPLIT_STR);
        for (int i=0;i<parameterTypes.length;i++){
            if (i>0){
                builder.append(",");
            }
            builder.append(parameterTypes[i]);
        }
        builder.append(")");
        return builder.toString();
    }


}

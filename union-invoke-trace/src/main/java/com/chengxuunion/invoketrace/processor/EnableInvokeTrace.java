package com.chengxuunion.invoketrace.processor;

import java.lang.annotation.*;

/**
 * 启用方法调用跟踪的注解
 *
 * @author youpanpan
 * @date: 2019-03-13 17:53
 * @since v1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableInvokeTrace {

    /**
     * pointcut表达式
     *
     * @return
     */
    String value() default "";
}

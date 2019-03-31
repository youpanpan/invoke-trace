package com.chengxuunion;

import com.chengxuunion.invoketrace.processor.EnableInvokeTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author youpanpan
 * @date: 2019-03-13 15:18
 * @since v1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.chengxuunion"})
@EnableInvokeTrace("execution(* com.chengxuunion.test.demo..*(..))")
public class InvokeTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvokeTraceApplication.class, args);
    }
}

package com.chengxuunion.invoketrace.common.util;

import com.chengxuunion.util.properties.PropertiesReader;
import org.aspectj.weaver.Dump;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * union-invoke-trace.properties文件读取工具类
 *
 * @author youpanpan
 * @date 2018年8月31日
 * @version V1.0
 */
public class InvokeTracePropertiesUtils {

	/**
	 * properties文件读取器
	 */
	private PropertiesReader reader;

	private static InvokeTracePropertiesUtils instance = null;


	private InvokeTracePropertiesUtils() {
		try {
			reader = new PropertiesReader("/union-invoke-trace.properties");
		} catch (IOException e) {
			LoggerFactory.getLogger(this.getClass()).error("初始化union-invoke-trace.properties文件读取器出现异常", e);
		}
	}

	/**
	 * 获取PropertiesReader对象
	 * 
	 * @return
	 */
	public static PropertiesReader getInstance() {
		if (instance == null) {
			synchronized (InvokeTracePropertiesUtils.class) {
				if (instance == null) {
					instance = new InvokeTracePropertiesUtils();
				}
			}
		}
		return instance.reader;
	}
}
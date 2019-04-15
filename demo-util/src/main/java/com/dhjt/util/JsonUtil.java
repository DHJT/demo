package com.dhjt.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.dhjt.json.DateJsonValueProcessor;
import com.dhjt.json.HibernateJsonBeanProcessor;
import com.dhjt.json.HibernateJsonBeanProcessorMatcher;
import com.dhjt.json.NumberJsonValueProcessor;

import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 转json通用函数
 *
 * @author 马骏良
 *
 */
public class JsonUtil {
	static Log logger = LogFactory.getLog(JsonUtil.class);

	/**
	 * 转换为json字符串时，过滤集合类型
	 */
	public static JsonConfig COLLECTION_FILTER = new JsonConfig();

	/**
	 * 转换为json字符串时，过滤volumns，files，documents属性
	 */
	private static JsonConfig FILTER = new JsonConfig();

	static {
		COLLECTION_FILTER.registerJsonBeanProcessor(org.hibernate.proxy.HibernateProxy.class,
				new HibernateJsonBeanProcessor());
		COLLECTION_FILTER.setJsonBeanProcessorMatcher(new HibernateJsonBeanProcessorMatcher());
		COLLECTION_FILTER.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				if (value instanceof Collection) {
					return true;
				}
				return false;
			}
		});
		COLLECTION_FILTER.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		COLLECTION_FILTER.registerJsonValueProcessor(Long.class, new NumberJsonValueProcessor());
		COLLECTION_FILTER.registerJsonValueProcessor(Integer.class, new NumberJsonValueProcessor());
		COLLECTION_FILTER.registerJsonValueProcessor(Boolean.class, new NumberJsonValueProcessor());
		COLLECTION_FILTER.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		FILTER.registerJsonBeanProcessor(org.hibernate.proxy.HibernateProxy.class, new HibernateJsonBeanProcessor());
		FILTER.setJsonBeanProcessorMatcher(new HibernateJsonBeanProcessorMatcher());
		FILTER.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		FILTER.registerJsonValueProcessor(Long.class, new NumberJsonValueProcessor());
		FILTER.registerJsonValueProcessor(Integer.class, new NumberJsonValueProcessor());
		FILTER.registerJsonValueProcessor(Boolean.class, new NumberJsonValueProcessor());
		FILTER.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		FILTER.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				// if("volumns".equals(name)||"files".equals(name)||"documents".equals(name)){
				// return true;
				// }
				if (value == null && value instanceof Integer) {
					return true;
				}
				return false;
			}
		});

	}

	/**
	 * 将对象obj转化为json字符串
	 *
	 * @param obj
	 * @param jsonConfig
	 * @return
	 */
	public synchronized static String toString(Object obj) {
		if (obj instanceof List)
			return JSONArray.fromObject(obj).toString();
		else
			return JSONObject.fromObject(obj).toString();
	}

	/**
	 * 将对象obj转化为json字符串，允许指定一个JsonConfig参数
	 *
	 * @see ces.gdda.App#COLLECTION_FILTER
	 * @see ces.gdda.App#ARCHIVE_FILTER
	 * @param obj
	 * @param jsonConfig
	 * @return
	 */
	public synchronized static String toString(Object obj, JsonConfig jsonConfig) {
		if (obj instanceof Collection)
			return JSONArray.fromObject(obj, jsonConfig).toString();
		else
			return JSONObject.fromObject(obj, jsonConfig).toString();
	}

	/**
	 * 将对象obj转化为json字符串，允许指定一个JsonConfig参数
	 *
	 * @see ces.gdda.App#COLLECTION_FILTER
	 * @see ces.gdda.App#ARCHIVE_FILTER
	 * @param obj
	 * @param jsonConfig
	 * @return
	 */
	public synchronized static String toString(Object obj, String[] filters) {
		FILTER.setExcludes(filters);
		if (obj instanceof Collection) {
			System.out.println("2018年12月19日 下午9:02:33");
			return JSONArray.fromObject(obj, FILTER).toString();
		} else{
			return JSONObject.fromObject(obj, FILTER).toString();
		}
	}

	/**
	 * 将异常e转化为json字符串
	 *
	 * @param e
	 * @return
	 */
	public synchronized static String toString(Throwable e) {
		Throwable now = e;
		Throwable next = now.getCause();
		while (next != null) {
			now = next;
			next = now.getCause();
		}
		Map map = new HashMap();
		map.put("error", now.toString());
		map.put("message", now.getMessage() == null ? "" : now.getMessage());
		return JSONObject.fromObject(map).toString();
	}
}

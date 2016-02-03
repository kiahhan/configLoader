package io.kiah.common.config.register;

import io.kiah.common.config.listener.ChangedListener;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册配置项
 * 
 */
@XmlRootElement(name = "config")
public class Config {
	/**
	 * 配置项的类名（含包名）
	 */
	private String className;

	/**
	 * 配置项的xml配置文件所在Path
	 * （如果xml文件存放在包路径里，需要加上包名，如io/kiah/common/config/ServiceConfigRegister
	 * .xml）
	 */
	private String xmlPath;

	/**
	 * 默认自动热加载
	 */
	private boolean autoHotLoad = true;

	/**
	 * 默认扫描间隔5000毫秒（配置时以毫秒为单位）
	 */
	private long scanInterval = 5000;

	/**
	 * 配置文件的最后修改时间
	 */
	private Map<String, String> lastModifyTimeMap = new HashMap<String, String>();

	/**
	 * 配置文件改动监听类(可选配置)
	 */
	private String listenerClassName;

	/** 解析后的结果对象引用 **/
	private Map<String, Object> valueMap = new ConcurrentHashMap<String, Object>();

	/** 接听接口对象引用 **/
	private ChangedListener listener;

	public String getClassName() {
		return className;
	}

	@XmlAttribute(name = "className")
	public void setClassName(String className) {
		this.className = className;
	}

	@XmlAttribute
	public String getListenerClassName() {
		return listenerClassName;
	}

	public void setListenerClassName(String listenerClassName) {
		this.listenerClassName = listenerClassName;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	@XmlAttribute(name = "xmlPath")
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public boolean isAutoHotLoad() {
		return autoHotLoad;
	}

	@XmlAttribute
	public void setAutoHotLoad(boolean autoHotLoad) {
		this.autoHotLoad = autoHotLoad;
	}

	public long getScanInterval() {
		return scanInterval;
	}

	@XmlAttribute
	public void setScanInterval(long scanInterval) {
		this.scanInterval = scanInterval;
	}

	public String getLastModifyTime(String fileName) {
		return lastModifyTimeMap.get(fileName);
	}

	public void setLastModifyTime(String fileName, String time) {
		this.lastModifyTimeMap.put(fileName, time);
	}

	public Map<String, String> getLastModfiyTimes() {
		return lastModifyTimeMap;
	}

	@Override
	public String toString() {
		return "Config [ className=" + className + ", lastModifyTime=" + ", scanInterval=" + scanInterval + ", xmlPath="
				+ xmlPath + "]";
	}

	public Object getValue(String fileName) {
		return valueMap.get(fileName);
	}

	public void setValue(String fileName, Object object) {
		this.valueMap.put(fileName, object);
	}

	public Map<String, Object> getValues() {
		return valueMap;
	}

	public ChangedListener getListenerInstacne() {
		return listener;
	}

	public void setListenerInstance(ChangedListener listener) {
		this.listener = listener;
	}

}

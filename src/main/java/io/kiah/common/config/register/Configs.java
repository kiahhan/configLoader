package io.kiah.common.config.register;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 注册配置项集合
 * 
 */
@XmlRootElement(name = "configs")
public class Configs {
	/**
	 * 所有配置文件集合
	 */
	private List<Config> configList;

	/**
	 * 返回配置文件集合
	 * 
	 * @return 配置文件集合
	 */
	public List<Config> getConfigList() {
		return configList;
	}

	@XmlElementRef
	public void setConfigList(List<Config> configList) {
		this.configList = configList;
	}

}

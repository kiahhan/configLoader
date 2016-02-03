package io.kiah.common.config;

import io.kiah.common.config.listener.ConfigScan;
import io.kiah.common.config.listener.IConfigChangedListener;
import io.kiah.common.config.register.Config;
import io.kiah.common.config.register.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置实例生成器。
 * 
 */
public class ConfigInstanceGenerator {

	private static final Logger log = LoggerFactory.getLogger(ConfigInstanceGenerator.class);

	private static final String CONFIG_SERVICE_CLASS_NAME = "io.kiah.common.config.register.Configs";
	private static final String CONFIG_SERVICE_XML_PATH = "ServiceConfigRegister.xml";

	/**
	 * 配置实例Map；用于存放所有注册的配置项实例，这些配置实例的key为其类全名。
	 */
	private final static Map<String, Object> configInstanceMaps;
	private static volatile boolean hasInited = false;

	static {
		configInstanceMaps = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * 初始化配置
	 */
	private static void init() {

		if (hasInited)
			return;

		hasInited = true;

		// 读取配置服务的xml文件，并生成相应实例
		Configs configs = (Configs) JAXBUtil.xml2java(CONFIG_SERVICE_CLASS_NAME, CONFIG_SERVICE_XML_PATH);

		if (configs == null) {
			log.error("can not create Configs instance,for configServiceClassName:" + CONFIG_SERVICE_CLASS_NAME
					+ " configServiceXmlPath:" + CONFIG_SERVICE_XML_PATH + ", the configuration service force end!");
			return;
		}

		List<Config> configList = configs.getConfigList();

		if (configList == null || configList.size() == 0) {
			log.error("No registered configuration, the configuration service does not start!");
			return;
		}

		log.info("Registered  configuration items are: " + configList);

		// 初始化每个配置项
		for (Config config : configList) {

			try {
				URL url = ConfigInstanceGenerator.class.getClassLoader().getResource(config.getXmlPath());
				File file = new File(url.toURI());
				// 为注册的配置项加上初始修改时间
				config.setLastModifyTime(config.getXmlPath(), file.lastModified() + "");
				// 初始生成所有配置实例，并存入configInstanceMaps
				Object item = JAXBUtil.xml2java(config.getClassName(), config.getXmlPath());
				// 对结果赋值
				config.setValue(config.getXmlPath(), item);
				configInstanceMaps.put(config.getXmlPath(), item);
				// 构造接口实现类
				String listenerClassName = config.getListenerClassName();
				if (listenerClassName != null && !listenerClassName.isEmpty()) {
					IConfigChangedListener listener = (IConfigChangedListener) Class.forName(listenerClassName)
							.newInstance();
					// 对实现类进行存储，防止多次构造
					config.setListenerInstance(listener);
				}
			} catch (Exception e) {
				log.error("Failed to init \"" + config.getXmlPath() + "\". The file had been ignored.", e);
				continue;// 不再处理该配置文件
			}
		}

		ConfigScan.scan(configs);
	}

	/**
	 * 通过配置项的全类名得到配置实例
	 * 
	 * @param xmlPath 配置项的全类名
	 * @return 配置实例
	 */
	public static Object getConfigInstance(String xmlPath) {
		init();
		return configInstanceMaps.get(xmlPath);
	}

	/**
	 * 得到所有已注册的配置实例Map{key:className value:配置实例}
	 * 
	 * @return 实例集合
	 */
	public final static Collection<Entry<String, Object>> getConfigInstanceSet() {
		init();
		return configInstanceMaps.entrySet();
	}

	/**
	 * 
	 * @param fileName
	 * @param obj
	 */
	public final static void cache(String fileName, Object obj) {
		configInstanceMaps.put(fileName, obj);
	}
}

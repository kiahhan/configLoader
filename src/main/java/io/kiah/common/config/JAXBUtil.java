package io.kiah.common.config;

import io.kiah.common.config.listener.ConfigScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;

/**
 * JAXB工具类
 * 
 */
public class JAXBUtil {

	private static final Logger log = LoggerFactory.getLogger(JAXBUtil.class);

	/**
	 * 通过类名和xml配置文件生成相应的配置实例
	 * 
	 * @param className
	 *            类全名（含包名）
	 * @param xmlPath
	 *            配置项的xml配置文件所在Path（如果xml文件存放在包路径里，需要加上包名，如io/kiah/common/config/
	 *            ServiceConfigRegister.xml）
	 * @return 生成的配置实例
	 */
	public static Object xml2java(String className, String xmlPath) {

		Object obj = null;

		try {
			JAXBContext context = JAXBContext.newInstance(Class.forName(className));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			URL url = ConfigScan.class.getClassLoader().getResource(xmlPath);

			if (url == null) {
				log.info("The file \"" + xmlPath + "\" could not found, return null object.");
				return null;
			}

			obj = unmarshaller.unmarshal(url);

		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return obj;
	}
}
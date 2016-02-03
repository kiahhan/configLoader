package io.kiah.common.config.listener;

import io.kiah.common.config.ConfigInstanceGenerator;
import io.kiah.common.config.JAXBUtil;
import io.kiah.common.config.register.Config;
import io.kiah.common.config.register.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 配置文件扫描,实现热加载
 * 
 */
public class ConfigScan {

	private static final Logger log = LoggerFactory.getLogger(ConfigScan.class);
	private static final Logger configRelodLogger = LoggerFactory.getLogger("configReloadLogger");

	private static ScheduledExecutorService executor = null;

	/**
	 * 扫描已注册的配置文件集合，若配置项的autoHotLoad为true，且文件有改动，则重新生成实例。
	 */
	public static void scan(Configs configs) {

		// 创建定时执行器（线程池中的线程为守护线程）
		executor = Executors.newScheduledThreadPool(configs.getConfigList().size(), new DaemonThreadFactory());

		for (Config config : configs.getConfigList()) {
			if (config.isAutoHotLoad()) {
				// 向定时器添加配置文件的扫描任务
				executor.scheduleAtFixedRate(new ConfigScanTask(config), 1000, config.getScanInterval(),
						TimeUnit.MILLISECONDS);
			}
		}

	}

	/**
	 * shutdown scan
	 */
	public static void shutdown() {
		log.info("Config File Scan shutdown!");

		if (executor != null) {
			executor.shutdownNow();
			executor = null;
		}
	}

	/**
	 * 配置文件扫描任务类
	 */
	private static class ConfigScanTask implements Runnable {
		private Config config;

		/**
		 * 构造方法
		 * 
		 * @param config
		 */
		public ConfigScanTask(Config config) {
			this.config = config;
		}

		/**
		 * 用于扫描文件是否变更并回调已注册的监听方法
		 */
		public void run() {

			/**
			 * 不try catch部分，能够使其在web容器环境关闭情况能抛出异常自动关闭扫描线程
			 */
			log.debug(config.getXmlPath() + " scanning ...");

			URL url = ConfigScan.class.getClassLoader().getResource(config.getXmlPath());
			if (url == null) {
				log.info("could not find config xml :" + config.getXmlPath() + ", force return this time scanning!");
				return;
			}
			try {

				File file = new File(url.toURI());
				long nowModifyTime = file.lastModified();

				if (!config.getLastModifyTime(config.getXmlPath()).equals(nowModifyTime + "")) {
					log.info(config.getXmlPath() + " find change, reload it.");
					Object item = JAXBUtil.xml2java(config.getClassName(), config.getXmlPath());
					config.setValue(config.getXmlPath(), item);
					// 保存最新修改时间
					config.setLastModifyTime(config.getXmlPath(), String.valueOf(nowModifyTime));
					// 获取监听接口实现类，并进行回调void configChanged(Config confi)方法
					configRelodLogger.info(config.getXmlPath() + " reload  success!");
					ConfigInstanceGenerator.cache(config.getXmlPath(), item);
					IConfigChangedListener listener = (IConfigChangedListener) config.getListenerInstacne();
					if (listener != null) {
						listener.configChanged(config);
					}
				}
			} catch (Exception e) {
				log.error(config.getXmlPath() + " : scanning find error : ", e);
			}
		}
	}
}
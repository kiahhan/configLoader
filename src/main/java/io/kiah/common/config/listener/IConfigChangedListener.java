package io.kiah.common.config.listener;

import io.kiah.common.config.register.Config;

/**
 * 配置文件更改监听接口
 *
 */
public interface IConfigChangedListener extends ChangedListener {
	/**
	 * 通知实现类配置文件已更新
	 */
	public void configChanged(Config config);
}

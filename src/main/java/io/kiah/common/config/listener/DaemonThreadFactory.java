package io.kiah.common.config.listener;

import java.util.concurrent.ThreadFactory;

/**
 * 实现了java并发包的ThreadFactory，创建守护线程。
 * （使用守护线程的好处是，当应用程序特别是web应用程序退出时，其不会阻碍jvm退出，而且当所有非守护线程结束后，守护线程会自动退出。）
 * 如配置文件扫描定时器，就需要这样的功能。
 *
 */
public class DaemonThreadFactory implements ThreadFactory {
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setDaemon(true);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}

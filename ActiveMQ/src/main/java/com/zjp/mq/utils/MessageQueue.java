/**
 * @author lsp 
 * 
 * copyright all reserved 系统内部消息队列
 */
package com.zjp.mq.utils;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {

	private static LinkedBlockingQueue queue;

	/**
	 * 创建建队列
	 */
	private static void initQueue() {
		if (queue == null) {
			synchronized (MessageQueue.class) {
				if (queue == null) {
					queue = new LinkedBlockingQueue(1000);
				}
			}
		}
	}

	/**
	 * 向队列中添加数据
	 *
	 * @param e
	 * @param <T>
	 * @throws InterruptedException
	 */
	public static <T> void put(T e) throws InterruptedException {
		if (queue == null) {
			initQueue();
		}
		queue.put(e);
	}

	/**
	 * 获取数据
	 *
	 * @param <T>
	 * @return
	 * @throws InterruptedException
	 */
	public static <T> T take() throws InterruptedException {
		if (queue == null) {
			initQueue();
		}
		return (T) queue.take();
	}

}

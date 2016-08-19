package com.zjp.mq.aop;

import com.zjp.mq.disruptor.impl.DisruptorQueue;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.utils.MessageHolder;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zjprevenge time: 2016/6/27 copyright all reserved
 * <p/>
 * 消息处理逻辑流程： 1.向broker中发送消息 2.消费者消费消息后给出响应消息 3.生产者获取响应消息，删除本地数据库中的消息
 */
@Component
@Aspect
@Slf4j
public class MqOperationAspect implements Ordered {

	@Resource(name = "QMessageService")
	private QMessageService qMessageService;

	@Resource
	private DisruptorQueue disruptorQueue;

	// 任意公共方法
	@Pointcut("execution( public * *(..))")
	public void aopPoint() {
	}

	/**
	 * 方法正常执行完成时，向broker中发送消息 任何标注AMQ注解的公共方法执行完成之后，发送消息
	 */
	@AfterReturning(value = "aopPoint()&& @annotation(com.zjp.mq.annotation.AMQ)")
	public void sendMessageToBroker(JoinPoint pj) {
		log.info("send message to activeMQ broker start... ");
		try {
			List<String> list = MessageHolder.get();
			log.info("get the message size:{}", list.size());
			for (String messageId : list) {
				// 获取消息内容
				QMessage message = qMessageService.getMessage(messageId);
				// 获取消息对应的生产者
				if (message == null) {
					continue;
				}
				log.info("Messages Enqueued  is :  " + message);
				disruptorQueue.publish(message);
			}
			log.info("send message to activeMQ broker end...");
		} catch (Exception e) {
			log.error("send message error: {}", e);
		} finally {
			// 清除数据，减小内存占用
			log.info("清空  messageHolder ······");
			MessageHolder.remove();
		}
	}

	public int getOrder() {
		return Integer.MAX_VALUE;
	}
}

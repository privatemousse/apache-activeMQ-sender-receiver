package com.zjp.mq.producer;

import com.google.common.base.Preconditions;
import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.config.ProducerCfg;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.utils.MessageHolder;
import com.zjp.mq.utils.QMessageUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author lsp
 * 
 *         copyright all reserved
 */
@Slf4j
public class MessageSender extends ProducerCfg implements InitializingBean {

	@Resource(name = "QMessageService")
	private QMessageService qMessageService;

	@Resource
	private ProducerCache producerCache;

	@Resource
	private BrokerConfig brokerConfig;

	/**
	 * 
	 *
	 * @param message
	 *            消息内容
	 * 
	 *            发送消息 只是数据存储到本地数据库中
	 * 
	 */
	public void sendMessage(Map<String, String> message) {

		Preconditions.checkArgument(message != null && message.size() != 0, "message must not be empty...");

		// 时间戳
		Date date = new Date();
		// 生成消息Id
		String messageId = QMessageUtils.createMessageId(date);
		// 创建消息
		QMessage qMessage = null;
		/**
		 * 业务逻辑标记是否为空区分 n1 n2
		 */
		// 是否是n2级别的消息
		if (n2) {
			log.info("");
			// businessMark不允许为空
			Preconditions.checkArgument(StringUtils.isNotBlank(message.get("businessMark")),
					"This queueName:" + destName + "is n2,businessMark must not be empty...");
			// 创建消息
			qMessage = QMessage.builder().messageId(messageId).businessMark(message.get("businessMark"))
					.destination(destName).status(0).destType(0).messageContent(message.get("message")).retry(0)
					.timeStamp(date.getTime()).build();
			log.info("n2 级别的消息：    " + qMessage);
		} else {
			// 只需要满足n1级别
			qMessage = QMessage.builder().messageId(messageId).destination(destName).status(0).destType(0)
					.messageContent(message.get("message")).retry(0).timeStamp(date.getTime()).build();
			log.info("n1 级别的消息：    " + qMessage);
		}
		// 将消息保存到数据库中
		int result = qMessageService.addQMessage(qMessage);
		log.info("消息保存的结果是：   " + result + "非 0 代表成功");
		// 将消息Id共享出去
		if (result != 0) {
			MessageHolder.set(messageId);
			log.info("消息Id   :" + messageId + "共享成功");
		}
	}

	/**
	 * 参数校验
	 *
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		// 消息队列名称不允许以ack. or ACK.开始
		if (destName.startsWith("ack.") || destName.startsWith("ACK.")) {
			throw new RuntimeException("destName must not start with ack. or ACK.");
		}
		// 启动时初始化生产者
		ActiveMqMessageProducer producer = producerCache.get(destName);
		if (producer == null) {
			producer = ActiveMqMessageProducer.builder().brokerUrl(brokerConfig.getBrokerUrl())
					.userName(brokerConfig.getUserName()).password(brokerConfig.getPassword()).destName(destName).n2(n2)
					.qMessageService(qMessageService).build();
			// 加入缓存中
			producerCache.set(destName, producer);
		}
	}
}

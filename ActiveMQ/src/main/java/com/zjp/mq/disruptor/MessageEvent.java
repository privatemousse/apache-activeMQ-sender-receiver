package com.zjp.mq.disruptor;

import com.zjp.mq.entity.QMessage;

/**
 * @author lsp 
 * copyright all reserved
 */
public class MessageEvent {

	private QMessage qMessage;

	public QMessage getqMessage() {
		return qMessage;
	}

	public void setqMessage(QMessage qMessage) {
		this.qMessage = qMessage;
	}
}

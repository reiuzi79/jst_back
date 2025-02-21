package com.su.JobTracker.service;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.su.JobTracker.mapper.*;
import com.su.JobTracker.model.*;

@Service
public class MessageService {
	@Autowired
	private messageMapper messageMapper;
	
	public int insertMessage(int sender_id, int receiver_id, String content) {
		message message = new message();
		message.setSenderId(sender_id);
		message.setReceiverId(receiver_id);
		message.setReadStatus(false);
		message.setTimestamp(new Date());
		return messageMapper.insert(message);
	}
	
	
	
	public List<message> findMessageByApplicationId(int receiver_id){
		messageExample example = new messageExample();
		var cri = example.createCriteria();
		cri.andReceiverIdEqualTo(receiver_id);
		return messageMapper
				.selectByExample(example)
				.stream()
				.sorted(Comparator.comparing(message::getTimestamp).reversed())
				.collect(Collectors.toList());
	}
	
	public void setRead(int message_id) {
		var message = messageMapper.selectByPrimaryKey(message_id);
		message.setReadStatus(true);
	}
}

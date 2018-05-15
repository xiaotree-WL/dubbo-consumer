package com.dubbo.nlp.dubboconsumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.techwolf.oceanus.chatbot.api.notice.ChatMessage;
import com.techwolf.oceanus.chatbot.api.notice.ChatNoticeService;
import com.techwolf.oceanus.chatbot.api.notice.Notice;
import io.gsf.core.annotation.MicroService;
import io.gsf.core.app.MicroServiceApp;
import org.springframework.boot.CommandLineRunner;

import java.io.*;
import java.util.List;

@MicroService(serviceId = "30000100002")
public class DubboconsumerApplication implements CommandLineRunner {

	@Reference(version = "1.0.1")
	ChatNoticeService chatNoticeService;

	public static void main(String[] args) {
		MicroServiceApp.start(DubboconsumerApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		testDialogue();
	}

	public void testDialogue() throws Exception {
		String filepath = "/home/wanglin/data/data/chat-eg-13.txt";
		//filepath = "/home/wangsihong/workspace/work/corpus/chat/chat-eg-17.txt";
		File file = new File(filepath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			int count=0;
			long start=System.currentTimeMillis();
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0)
					continue;

				String[] pair = line.trim().split("\t");
				long fromId = Long.parseLong(pair[2]);
				long toId = Long.parseLong(pair[3]);
				long timestamp = Long.parseLong(pair[4]);
				String content = pair[5];
				int type = Integer.parseInt(pair[7]);
				if (type != 1)
					continue;
				int fromIdentity = Integer.parseInt(pair[8]);
				int messageType =  1; //消息类型
				int actionType = 0; //消息类型不是0才有用，默认设置为0
				int chatStatus = 3; //单聊还是双聊，双聊是3

				ChatMessage chatMessage = new ChatMessage(0, fromId, toId, fromIdentity, messageType, timestamp, content, actionType, chatStatus);
				//ForkJoinPool.commonPool().execute(()->chatNoticeService.chatNotice(chatMessage);
				List<Notice> notices = chatNoticeService.chatNotice(chatMessage);
				count=count+1;
				System.out.println("================ "+count+"->"+ fromId + "-" + toId + " : " + content);
				if (null != notices && notices.size() > 0) {
					System.out.println("+++++++++++++++++++++++++++++++++" + notices.get(0).getContent());
				}
			}
			System.out.println("================exe time "+(System.currentTimeMillis()-start));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

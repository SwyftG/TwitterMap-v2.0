package twittMap;

import java.io.Serializable;
import org.w3c.dom.Document;

import com.alchemyapi.api.*;

public class ThreadPoolTask implements Runnable, Serializable {
	private static final long serialVersionUID = 0;
	// ������������Ҫ������
	private Object threadPoolTaskData;
	private Document doc;

	ThreadPoolTask(Object tasks) {
		this.threadPoolTaskData = tasks;
	}

	public void run() {
		// ����һ����������Ĵ���ʽ̫���ˣ�������һ����ӡ���
		System.out.println("start .." + threadPoolTaskData);

		AlchemyAPI alchemyObj = AlchemyAPI
				.GetInstanceFromString("PUTyourKEYHere");

		while (true) {
			try {
				String msg = SimpleQueueService.PickUpAmsg();
				doc = alchemyObj.TextGetTextSentiment(msg.substring(20,
						msg.length()));
				// publish this evaluation result to SNS: sid + type + score.
				SNS.publishMsg(msg.substring(0, 20) + " "
						+ getStringFromDocument(doc));
			} catch (Exception e) {
				e.printStackTrace();
				threadPoolTaskData = null;
			}
		}
	}

	public Object getTask() {
		return this.threadPoolTaskData;
	}

	// utility method return: sid + type + score.
	private static String getStringFromDocument(Document doc) {
		return doc.getElementsByTagName("type").item(0).getTextContent() + " "
				+ doc.getElementsByTagName("score").item(0).getTextContent();
	}
}
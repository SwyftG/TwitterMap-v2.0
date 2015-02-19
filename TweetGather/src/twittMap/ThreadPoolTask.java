package twittMap;

import java.io.Serializable;
import org.w3c.dom.Document;

import com.alchemyapi.api.*;

public class ThreadPoolTask implements Runnable, Serializable {
	private static final long serialVersionUID = 0;
	// 保存任务所需要的数据
	private Object threadPoolTaskData;
	private Document doc;

	ThreadPoolTask(Object tasks) {
		this.threadPoolTaskData = tasks;
	}

	public void run() {
		// 处理一个任务，这里的处理方式太简单了，仅仅是一个打印语句
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
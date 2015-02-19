package analysis;

import aws.SQSService;

import com.alchemyapi.api.*;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import org.xml.sax.SAXException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class SentimentAnalyse{
	private static final SentimentAnalyse sentimentAnalyse = new SentimentAnalyse();
	AlchemyAPI alchemyObj = null;
	static Boolean flag = true;
	ThreadPool threadPool = ThreadPool.getInstance();
	
	private SentimentAnalyse() {
		//System.out.println("classLoader="+this.getClass().getClassLoader()); 
		try {
			alchemyObj = AlchemyAPI
					.GetInstanceFromString("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SentimentAnalyse getInstance() {
		System.out.println(sentimentAnalyse);
		System.out.println("flag: " + flag);
		return sentimentAnalyse;
	}
    
	public void setFlag(boolean b) {
		flag = b;
		System.out.println(flag);
	}
	
	public void startThreadPool2() {
		ExecutorService pool = threadPool.startPool();
		setFlag(true);
		while(flag) {
			Runnable run = new Runnable() {
				@Override
				public void run() {
					System.out.println("running");
				}
			};
			pool.execute(run);
			//System.out.println(flag);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startThreadPool() {
		final SQSService sQSService = SQSService.getInstance();
		final AmazonSNSClient snsClient = iniSNS();
		final String topicArn = "";
		ExecutorService pool = threadPool.startPool();
		System.out.println("isShunt: " + pool.isShutdown());
		setFlag(true);
		while (flag) {
			List<String> queues = sQSService.myListQueue();
			//final SQSService tsQSService = sQSService;
			System.out.println("begin: "+queues.size());
			if(queues.size() == 0) {
				System.out.println("break");
				break;
			}
			for (String myQueueUrl : queues) {
				final String fmyQueueUrl = myQueueUrl;
				Runnable run = new Runnable() {
					@Override
					public void run() {
						List<String> messages = null;
						messages = sQSService.myReceiveMessage(fmyQueueUrl);
						JSONObject obj;
						String[] sentimentOut = new String[2];
						//System.out.println("running");
						for (String message : messages) {
							try {
								obj = (JSONObject) new JSONParser().parse(message);
								//System.out.println((String)obj.get("sText"));
								sentimentOut = analyse((String) obj.get("sText"));
								if(sentimentOut == null) continue;
								JSONObject sentiOut = new JSONObject();
								sentiOut.put("sId", obj.get("sId"));
								sentiOut.put("type", sentimentOut[0]);
								sentiOut.put("score", sentimentOut[1]);
								sentiOut.put("sLatitude", obj.get("sLatitude"));
								sentiOut.put("sLongitude", obj.get("sLongitude"));
								sentiOut.put("sTime", obj.get("sTime"));
								System.out.println(sentiOut.toString());
								String msg = sentiOut.toString();
								myPublish(msg, topicArn, snsClient);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				};
				pool.execute(run);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sQSService.myDeleteQueue(myQueueUrl);
			}
			try {
				System.out.println("Sleep 60s");
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public AmazonSNSClient iniSNS() {
		AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider("././AwsCredentials.properties"));		                           
		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
		return snsClient;
	}
	
	public void myPublish(String msg, String topicArn, AmazonSNSClient snsClient) {
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		System.out.println("MessageId - " + publishResult.getMessageId());
	}
	
	public void closeThreadPool() {
		setFlag(false);
		threadPool.shutPool();
	}

	public String[] analyse(String text) {
		Document doc = null;
		try {
			doc = alchemyObj.TextGetTextSentiment(text);
		} catch (XPathExpressionException | IOException | SAXException
				| ParserConfigurationException e) {
			System.out.println("TextGetTextSentiment erro");
			return null;
		}
		return getStringFromDocument(doc);
	}

	// get type and score
	private static String[] getStringFromDocument(Document doc) {
		//System.out.println(doc.toString());
		String[] out = new String[2];
		try {
			out[0] = doc.getElementsByTagName("type").item(0).getTextContent();
			out[1] = doc.getElementsByTagName("score").item(0).getTextContent();
		} catch(Exception e) {
			System.out.println("dom operation erro");
		}
		return out;
	}
	
//	public static void main(String args[]) {
//		SentimentAnalyse fac = new SentimentAnalyse();
//		AmazonSNSClient snsClient = fac.iniSNS(); 
//		fac.myPublish("test123", "", snsClient);
//	}
}

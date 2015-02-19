package aws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSService {

	//ini sqs
	AmazonSQS sqs = null;
	private final static SQSService sQSService = new SQSService();
	private SQSService() {
//		AWSCredentials credentials = null;
//		try {
//			credentials = new PropertiesCredentials(
//					SimpleQueueServiceSample.class
//							.getResourceAsStream("././AwsCredentials.properties"));	                           
//		} catch (Exception e) {
//			throw new AmazonClientException(
//					"Cannot load the credentials from the credential profiles file. "
//							+ "Please make sure that your credentials file is at the correct "
//							+ e);
//		}
		sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider("././AwsCredentials.properties"));
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		sqs.setRegion(usWest2);
	}
	
	public static SQSService getInstance() {
		return sQSService;
	}
	//create sqs
	public String myCreateQueue(String name) {
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(name);
		return sqs.createQueue(createQueueRequest).getQueueUrl();
	}
	
	//List queues
	public List<String> myListQueue() {
		List<String> out = new ArrayList<String>();
        for (String queueUrl : sqs.listQueues().getQueueUrls()) {
        	try {
        		out.add(queueUrl);
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        	}
        }
        return out;
	}
	
	//send to sqs
	public void mySendMessage(String message, String myQueueUrl) {
		sqs.sendMessage(new SendMessageRequest(myQueueUrl, message));
	}
	
	//receive from sqs
	public List<String> myReceiveMessage(String myQueueUrl) {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		List<Message> messages = null;
		try {
			messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		} catch(Exception e) {
			e.printStackTrace();
		}
        
        List<String> out = new ArrayList<String>();
        for (Message message : messages) {
        	out.add(message.getBody());
        }
        return out;
	}
	
	//delete queue
	public void myDeleteQueue(String myQueueUrl) {
		if(myQueueUrl.isEmpty()) return;
		try{
			sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
		}
		catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	//delete all
	public void deleteAll() {
		List<String> urlList = myListQueue();
		for(String url : urlList) {
			myDeleteQueue(url);
		}
		
	}
	
	public static void main(String[] args) {
		SQSService fac = new SQSService();
		while(true) {
			
			System.out.println(fac.myListQueue().size());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

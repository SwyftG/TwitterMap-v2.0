package twittMap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

public class SNS {
	private static AWSCredentials credentials;
	private static AmazonSNSClient snsClient;
	private static String topicArn;
	private static String endpoint="http://snsservlet-env.elasticbeanstalk.com/";
	
	public static void init(){
		try {
			credentials = new ProfileCredentialsProvider("default")
					.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (/Users/daniel/.aws/credentials), and is in valid format.",
					e);
		}
		
		snsClient = new AmazonSNSClient(credentials);
		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
		
		//create a new SNS topic
		CreateTopicRequest createTopicRequest = new CreateTopicRequest("SentimentEval");
		CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
		
		//save TopicArn
		System.out.println(createTopicResult);
		topicArn = createTopicResult.getTopicArn();
		
		//get request id for CreateTopicRequest from SNS metadata		
		System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
		
		
		//subscribe to an SNS topic
		//TODO: get the endpoint of this server.
		subscribe();
	}

	
	public static void subscribe(){
		
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "http", endpoint);
		snsClient.subscribe(subRequest);
		//get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
	}
	
	public static void publishMsg(String msg){
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		//print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}
	
	public static void deleteTopic(){
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
		//get request id for DeleteTopicRequest from SNS metadata
		System.out.println("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
	}
	
	public static void main(String[] args){
		System.out.println("init");
		SNS.init();
		System.out.println("publish message");
		SNS.publishMsg("test message");
		System.out.println("done");

	}
}

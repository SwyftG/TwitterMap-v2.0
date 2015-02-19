package twittMap;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * This sample demonstrates how to make basic requests to Amazon SQS using the
 * AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services developer
 * account, and be signed up to use Amazon SQS. For more information on Amazon
 * SQS, see http://aws.amazon.com/sqs.
 * <p>
 * WANRNING:</b> To avoid accidental leakage of your credentials, DO NOT keep
 * the credentials file in your source directory.
 */
public final class SimpleQueueService {
	private static AWSCredentials credentials;
	private static AmazonSQS sqs;
	private static String myQueueUrl;
	private static ThreadPoolExecutor threadPool;
	private static int produceTaskMaxNumber = 10;

	public static void IniateSimpleQueueService() {
		try {
			// credentials = new ProfileCredentialsProvider("default")
			// .getCredentials();
			credentials = new BasicAWSCredentials("PUT YOUR KEYID HERE",
					"PUT YOUR KEY HERE");
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (/Users/daniel/.aws/credentials), and is in valid format.",
					e);
		}
		sqs = new AmazonSQSClient(credentials);
		sqs.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
		try {
			// Create a queue
			System.out.println("Creating a new SQS queue called MyQueue.\n");
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(
					"MyQueue");
			myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with SQS, such as not "
							+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		// 构造一个线程池
		threadPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(3),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		for (int i = 1; i <= produceTaskMaxNumber; i++) {
			try {
				// 产生一个任务，并将其加入到线程池
				String task = "task@ " + i;
				System.out.println("put " + task);
				threadPool.execute(new ThreadPoolTask(task));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void SendMsg(long Id, String msg) {
		// Send a message
		System.out.println("Sending a message to MyQueue:"
				+ String.format("%1$020d", Id) + msg);
		try {
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, String.format(
					"%1$020d", Id) + msg));
		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with SQS, such as not "
							+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public static String PickUpAmsg() {
		String msg = null;
		try {
			// Receive messages
			System.out.println("Receiving messages from MyQueue.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
					myQueueUrl).withMaxNumberOfMessages(1);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
					.getMessages();
			if (!messages.isEmpty()) {
				for (Message message : messages) {
					System.out.println("  Message");
					System.out.println("    MessageId:     "
							+ message.getMessageId());
					System.out.println("    ReceiptHandle: "
							+ message.getReceiptHandle());
					System.out.println("    MD5OfBody:     "
							+ message.getMD5OfBody());
					System.out.println("    Body:          "
							+ message.getBody());
					for (Entry<String, String> entry : message.getAttributes()
							.entrySet()) {
						System.out.println("  Attribute");
						System.out.println("    Name:  " + entry.getKey());
						System.out.println("    Value: " + entry.getValue());
					}
				}
				msg = messages.get(0).getBody().toString();
				// Delete a message
				System.out.println("Deleting a message.\n");
				String messageRecieptHandle = messages.get(0)
						.getReceiptHandle();
				sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl,
						messageRecieptHandle));
				System.out.println(msg);
			}

		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with SQS, such as not "
							+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return msg;
	}

	public final static void DeleteSimpleQueueService() throws Exception {
		try {
			// Delete a queue
			System.out.println("Deleting the test queue.\n");
			sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
			/*
			 * The ProfileCredentialsProvider will return your [default]
			 * credential profile by reading from the credentials file located
			 * at ().
			 */
		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with SQS, such as not "
							+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		// 关闭线程池
		threadPool.shutdown();
	}
}

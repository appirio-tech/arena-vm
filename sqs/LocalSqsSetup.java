import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class LocalSqsSetup {

	public static void main(String[] args) throws Exception {
		String endpoint = args[0];
		String prefix = args[1];
		
		System.out.println("Creating queues at endpoint " + endpoint + " with prefix " + prefix);
		
		AmazonSQS client = new AmazonSQSClient(new BasicAWSCredentials("x", "x"));
		client.setEndpoint(endpoint);
		for (int i=2; i < args.length; i++) {
			String q = prefix + args[i];
			client.createQueue(q);
			System.out.println("Created queue " + q);
		}
	
	}


	
}


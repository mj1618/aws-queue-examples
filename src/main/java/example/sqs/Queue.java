package example.sqs;

import example.MessageSubscriber;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import javax.jms.JMSException;
import java.util.List;

/**
 * Created by matt on 9/6/17.
 */
public class Queue {

    final String name;
    private final String queueUrl;
    final AmazonSQS sqs;


    public Queue(String name) throws JMSException {
        this.name=name;
        sqs = AmazonSQSClientBuilder.defaultClient();
        try {
            sqs.createQueue(name);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        queueUrl = sqs.getQueueUrl(name).getQueueUrl();

    }

    public void send(String body){

        SendMessageRequest sendRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(body);
        sqs.sendMessage(sendRequest);
    }

    public void subscribe(MessageSubscriber subscriber){
        while(true){
            List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
            for (Message message : messages) {
                subscriber.onMessage(message.getBody());
                sqs.deleteMessage(queueUrl, message.getReceiptHandle());
            }
        }
    }
}

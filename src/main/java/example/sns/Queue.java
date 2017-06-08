package example.sns;

import example.MessageSubscriber;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static spark.Spark.post;

/**
 * Created by matt on 9/6/17.
 */
public class Queue {
    String name;
    AmazonSNSClient snsClient;
    String topicArn;
    public Queue(String name){
        this.name = name;
        snsClient = new AmazonSNSClient();

        snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(name);
        CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
        topicArn=createTopicResult.getTopicArn();
    }

    public void send(String body){
        PublishRequest publishRequest = new PublishRequest(topicArn, body);
        PublishResult publishResult = snsClient.publish(publishRequest);
    }

    public void subscribe(MessageSubscriber subscriber){
        post("/message", (req, res) -> {
            Map<String, String> messageMap = new ObjectMapper().readValue(req.bodyAsBytes(), Map.class);
            String token = messageMap.get("Token");
            if (token != null) {
                System.out.println("confirming subscription: "+token);
                ConfirmSubscriptionRequest confirmReq = new ConfirmSubscriptionRequest()
                        .withTopicArn(topicArn)
                        .withToken(token);
                snsClient.confirmSubscription(confirmReq);
            } else {
                subscriber.onMessage(messageMap.get("Message"));
            }
            return "ok";
        });
        SubscribeRequest subscribeReq = new SubscribeRequest()
                .withTopicArn(topicArn)
                .withProtocol("http")
                .withEndpoint("http://8b96f8ad.ngrok.io/message"); // get using ./ngrok http 4567
        snsClient.subscribe(subscribeReq);

    }

}

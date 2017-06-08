package example.sqs;

import javax.jms.JMSException;

/**
 * Created by matt on 1/6/17.
 */
public class Subscriber {

    public static void main(String args[]) throws JMSException {
        Queue queue = new Queue("sqsexample");
        queue.subscribe(message->{
            System.out.println("Received SQS message: "+message);
        });
    }

}

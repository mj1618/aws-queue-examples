package example.sns;

import javax.jms.JMSException;

/**
 * Created by matt on 1/6/17.
 */
public class Subscriber {

    public static void main(String args[]) throws JMSException {
        Queue queue = new Queue("snsexample");
        queue.subscribe(message->{
            System.out.println("Received SNS message: "+message);
        });
    }

}

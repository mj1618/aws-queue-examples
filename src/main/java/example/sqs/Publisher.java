package example.sqs;

import javax.jms.JMSException;
import java.util.stream.IntStream;

/**
 * Created by matt on 9/6/17.
 */
public class Publisher {

    public static void main(String args[]) throws JMSException {
        Queue queue = new Queue("sqsexample");

        IntStream.range(1, 10).forEach(i->{
            System.out.println("Sending SQS message: "+i);
            queue.send(""+i);
        });

    }
}

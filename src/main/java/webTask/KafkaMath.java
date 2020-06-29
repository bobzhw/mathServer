package webTask;

import net.didion.jwnl.data.Exc;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class KafkaMath implements Runnable{

//    private static ExecutorService executorService = new ThreadPoolExecutor(3, 5,
//                3000, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>());
    private static ExecutorService ex = Executors.newCachedThreadPool();
    private final Semaphore semp = new Semaphore(4);
    private static KafkaConsumer<String, String> consumer;
    private final static String TOPIC = "test";
    public KafkaMath(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        //每个消费者分配独立的组号
        props.put("group.id", "test2");
        //如果value合法，则自动提V交偏移量
        props.put("enable.auto.commit", "true");
        //设置多久一次更新被消费消息的偏移量
        props.put("auto.commit.interval.ms", "1000");
        //设置会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
        props.put("session.timeout.ms", "30000");
        //自动重置offset
        props.put("auto.offset.reset","earliest");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<String, String>(props);
    }
    @Override
    public void run(){
        consumer.subscribe(Arrays.asList(TOPIC));
        for(;;) {
            ConsumerRecords<String, String> records = consumer.poll(3000);
            List<Task> tasks = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
//                executorService.execute(new Task(record.value(),record.key()));
                ex.execute(new Task(record.value(),record.key()));
//                new Task(record.value(),record.key()).run();
//                tasks.add(new Task(record.value(), record.key()));
            }
//            for (Task task : tasks) {
//                try {
//                    task.run();
//                } catch (Exception e) {
//                    e.printStackTrace();

//                }
//            }
        }
    }

    public static void main(String[] args) {
        KafkaMath kafkaMath = new KafkaMath();
        kafkaMath.run();;
    }
}

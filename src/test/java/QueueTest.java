import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: mongoToExcle
 * @description:
 * @author: chenzhennan
 * @create: 2020-02-25 17:46
 **/
public class QueueTest {

    public static void main(String[] args) throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(50);
        queue.put("a");
        queue.put("b");
        queue.put("c");
        //queue.poll();//只删除头元素
        //queue.peek();//只取出头元素，不做任何处理
        //queue.clear();//清空所有元素
        for (String str : queue) {
            System.out.println(str);
        }
        queue.poll();
        queue.poll();
        System.out.println(queue.isEmpty());
        queue.poll();
        System.out.println(queue.isEmpty());
        //System.out.println(queue.remove());//取出头元素，并删除其他元素
        System.out.println(queue.size());
        /*System.out.println(queue.poll());
        System.out.println(queue.peek());
        System.out.println(queue.size());*/
    }
}

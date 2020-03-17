/**
 * @program: mongoToExcle
 * @description: 多线程售票服务
 * @author: chenzhennan
 * @create: 2020-03-06 15:04
 **/
public class Station extends Thread{

     private static Integer tick = 100;
     private static Object object = new Object();//注意为保持加锁时对象是同一个，必须声明为静态的

    public Station(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (tick > 0){

            synchronized (object){
                if(tick > 0){
                    System.out.println(getName() + "卖出了第" + tick + "张票");
                    tick--;
                }
                else {
                    System.out.println("余票不足,票已售完!");
                }
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

import java.util.Random;
import java.util.concurrent.Callable;

public class SimpleThread implements Runnable, Callable {
    @Override
    public void run() {
        System.out.println("当前线程名称：" + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object call() throws Exception {
        System.out.println("当前线程名称：" + Thread.currentThread().getName());
        try {
            return new Random().nextInt(100000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

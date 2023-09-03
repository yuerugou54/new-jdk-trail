import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 本例子演示在JDK8的场景下，针对并发任务只需要执行其中任一一个的情况
 */
public class ExecuteAnyJDK8 {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(new RespTemperatureTask("a"));
        tasks.add(new RespTemperatureTask("b"));
        tasks.add(new RespTemperatureTask("c"));
        List<Future<String>> futures = null;
        try {
            // 等待任一个任务完成并获取结果
            String result = executorService.invokeAny(tasks);
            // 执行任务并返回 Future 对象列表
//            futures = executorService.invokeAll(tasks);
            System.out.println("获取执行结果为:" + result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (futures != null) {
                for (Future<String> future : futures) {
                    if (!future.isCancelled()) {
                        System.out.println("关闭线程");
                        future.cancel(true);
                    }
                }
            }
        }


    }

    static class RespTemperatureTask implements Callable<String> {
        private String name;

        public RespTemperatureTask(String name) {
            this.name = name;
        }

        @Override
        public String call() throws InterruptedException, ExecutionException {
            int res = new Random().nextInt(1000, 2000);
//            Thread.sleep(new Random().nextInt(1000, 5000));
            System.out.println("name:" + name + " 当前执行任务线程为:" + Thread.currentThread().getName() + ",res:" + res);
            return name + "-" + res;
        }

    }
}

import java.util.concurrent.*;

/**
 * 参考文档：https://mp.weixin.qq.com/s/MzCyWqcH1TCytpnHI8dVjA
 * 如果需要Run起来，需要在IDEA中的Java Compiler将本项目的Target bytecode version手动设置为21
 */
public class VirtualThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        start_way5();
        Thread.sleep(2000);
    }

    /**
     * 虚拟线程启动方式_1
     */
    public static void start_way1() {
        Thread.ofPlatform().name("thread-test").start(new SimpleThread());
    }

    /**
     * 虚拟线程启动方式2
     */
    public static void start_way2() {
        Thread.ofVirtual()
                .name("thread-test")
                .start(new SimpleThread());
    }

    /**
     * 虚拟线程启动方式_3
     * 使用 ofVirtual()，builder 方式启动虚拟线程，可以设置线程名称、优先级、异常处理等配置
     */
    public static void start_way3() {
        Thread thread = Thread.ofVirtual()
                .name("thread-test")
                .uncaughtExceptionHandler((t, e) -> {
                    System.out.println(t.getName() + e.getMessage());
                })
                .unstarted(new SimpleThread());
        thread.start();
    }

    /**
     * 使用 Factory 创建线程
     */
    public static void start_way4() {
        ThreadFactory factory = Thread.ofVirtual().factory();
        Thread thread = factory.newThread(new SimpleThread());
        thread.setName("thread-test");
        thread.start();
    }

    /**
     * 使用 Executors 方式
     */
    public static void start_way5() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        Future<?> submit = executorService.submit((Callable<Object>) new SimpleThread());
        Object o;
        try {
            o = submit.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("输出结果为:" + o);
    }
}

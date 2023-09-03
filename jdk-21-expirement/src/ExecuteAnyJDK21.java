import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 运行时在每个启动类上添加--enable-preview即可
 * 参考： https://mp.weixin.qq.com/s/MzCyWqcH1TCytpnHI8dVjA
 * <p>
 * ShutdownOnSuccess捕获第一个结果并关闭任务范围以中断未完成的线程并唤醒调用线程。
 * 适用于任意子任务的结果都可以直接使用，并且无需等待其他未完成任务的结果的情况。
 * 它定义了获取第一个结果或在所有子任务失败时抛出异常的方法
 */
public class ExecuteAnyJDK21 {
    public static void main(String[] args) throws IOException {
        //test_ShutdownOnSuccess();
        test_ShutdownOnFailure();
    }


    public static void test_ShutdownOnSuccess() {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            StructuredTaskScope.Subtask<String> res1 = scope.fork(() -> runTask(1));
            StructuredTaskScope.Subtask<String> res2 = scope.fork(() -> runTask(2));
            StructuredTaskScope.Subtask<String> res3 = scope.fork(() -> runTask(3));
            scope.join();
            System.out.println("scope:" + scope.result());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String runTask(int i) throws InterruptedException {
        Thread.sleep(1000);
        long l = new Random().nextLong();
        String s = String.valueOf(l);
        System.out.println("第" + i + "个任务：" + s + " 线程名:" + Thread.currentThread().getName());
        return s;
    }


    /**
     * ShutdownOnFailure
     * <p>
     * 执行多个任务，只要有一个失败（出现异常或其他主动抛出异常情况），就停止其他未执行完的任务，使用scope.throwIfFailed捕捉并抛出异常。
     * 如果所有任务均正常，则使用 Feture.get() 或*Feture.resultNow() 获取结果
     */
    public static void test_ShutdownOnFailure() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Supplier<String> res1 = scope.fork(() -> runTaskWithException(1));
            Supplier<String> res2 = scope.fork(() -> runTaskWithException(2));
            Supplier<String> res3 = scope.fork(() -> runTaskWithException(3));
            scope.join();
            scope.throwIfFailed(Exception::new);

            String s = res1.get(); //或 res1.get()
            System.out.println(s);
            String result = Stream.of(res1, res2, res3)
                    .map(Supplier::get)
                    .collect(Collectors.joining());
            System.out.println("直接结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }

    // 有一定几率发生异常
    public static String runTaskWithException(int i) throws InterruptedException {
        Thread.sleep(1000);
        long l = new Random().nextLong(3);
        if (l == 0) {
            throw new InterruptedException();
        }
        String s = String.valueOf(l);
        System.out.println("第" + i + "个任务：" + s);
        return s;
    }

}

/**
 * 作用域值是JDK 20中的一项功能，允许开发人员创建作用域限定的值，这些值限定于特定的线程或任务。
 * 作用域值类似于线程本地变量，但是设计为与虚拟线程和结构化并发配合使用。
 * 它们允许开发人员以结构化的方式在任务和虚拟线程之间传递值，无需复杂的同步或锁定机制。
 * 作用域值可用于在应用程序的不同部分之间传递上下文信息，例如用户身份验证或请求特定数据。
 */
public class ScopedValueExample {
    final static ScopedValue<String> LoginUser = ScopedValue.newInstance();

    /**
     * 我们肯定都用过 ThreadLocal，它是线程本地变量，只要这个线程没销毁，可以随时获取 ThreadLocal 中的变量值。
     * Scoped Values 也可以在线程内部随时获取变量，只不过它有个作用域的概念，超出作用域就会销毁。
     * 也就是说使用了虚拟线程，可以共享主线程中的作用域变量
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ScopedValue.where(LoginUser, "张三")
                .run(() -> {
                    new Service().login();
                });

        Thread.sleep(2000);
    }

    static class Service {
        void login() {
            System.out.println("当前登录用户是：" + LoginUser.get());
        }
    }
}

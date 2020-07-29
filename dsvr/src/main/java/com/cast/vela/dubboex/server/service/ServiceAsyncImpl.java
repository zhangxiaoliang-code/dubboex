package com.cast.vela.dubboex.server.service;

import com.cast.vela.dubboex.api.ServiceAsync;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 异步调用的例子，服务主要用这个
public class ServiceAsyncImpl implements ServiceAsync {
    private static final Logger log = LoggerFactory.getLogger(ServiceAsyncImpl.class);

    // 1.创建业务自定义线程池
    private final ThreadPoolExecutor bizThreadpool = new ThreadPoolExecutor(2, 128, 1, TimeUnit.MINUTES,
            new SynchronousQueue(), new NamedThreadFactory("biz-thread-pool"),
            new ThreadPoolExecutor.CallerRunsPolicy()); // TODO： 需要自行处理线程被拒绝的场景（最后一个参数）

    // 2.创建服务处理接口，返回值为CompletableFuture
    @Override
    public CompletableFuture<String> callAsync(String name) {
        // 2.1 为supplyAsync提供自定义线程池bizThreadpool，避免使用JDK公用线程池(ForkJoinPool.commonPool())
        // 使用CompletableFuture.supplyAsync让服务处理异步化进行处理
        log.info("Remote task {} submitted.", name);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
                doBusiness(name), bizThreadpool
        );
        return future;
    }

    private static Random random = new Random();

    // 具体的业务方法 这里模拟一种耗时的操作
    private String doBusiness(String name) {
        try {
            int anInt = random.nextInt(1000);
            TimeUnit.MILLISECONDS.sleep(anInt);
            log.info("Remote task {} completed. Used {}ms.", name, anInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello " + name;
    }
}

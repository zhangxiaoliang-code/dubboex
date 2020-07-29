package com.cast.vela.dubboex.client;

import com.cast.vela.dubboex.api.ServiceAsync;
import com.cast.vela.dubboex.api.SimpleService;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

// 通过API方式进行服务消费的示例
public class ApiClientPortal {
    private static final Logger log = LoggerFactory.getLogger(ApiClientPortal.class);

    public static void main(String[] args) {
        runWithBootstrap();
    }

    private static void runWithBootstrap() {
        log.info("Client with Bootstrap.");
        ReferenceConfig<SimpleService> simpleServiceReferenceConfig = new ReferenceConfig<>();
        simpleServiceReferenceConfig.setInterface(SimpleService.class);
        simpleServiceReferenceConfig.setTimeout(5000);

        ReferenceConfig<ServiceAsync> serviceAsyncReferenceConfig = new ReferenceConfig<>();
        serviceAsyncReferenceConfig.setInterface(ServiceAsync.class);
        serviceAsyncReferenceConfig.setTimeout(5000);

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        ApplicationConfig applicationConfig = new ApplicationConfig("dubbo-demo-api-consumer");
        applicationConfig.setQosEnable(true);
        applicationConfig.setQosPort(3333);     // 否则会跟Provider的端口冲突
        applicationConfig.setQosAcceptForeignIp(false);
        bootstrap.application(applicationConfig)
//                .registry(new RegistryConfig("zookeeper://192.168.71.7:2181"))
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(serviceAsyncReferenceConfig)
                .reference(simpleServiceReferenceConfig)
                .start();

        // 测试简单远程服务
        SimpleService remoteService = ReferenceConfigCache.getCache().get(simpleServiceReferenceConfig);
        int count = 0;
        while (count++ < 3) {
            String message = remoteService.sayHello("Client" + count);
            log.info("Got remote simple message: {}", message);
        }
        // 测试异步调用的远程服务
        ServiceAsync remoteAsyncService = ReferenceConfigCache.getCache().get(serviceAsyncReferenceConfig);
        count = 0;
        while (count++ < 10) {
            CompletableFuture<String> completableFuture = remoteAsyncService.callAsync("AsyncClient" + count);
            completableFuture.whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            log.error("Execute error. {}", throwable.getLocalizedMessage());
                        } else {
                            log.info("Got remote async message: {}", result);
                        }
                    }
            );
        }
        try {
            Thread.currentThread().join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

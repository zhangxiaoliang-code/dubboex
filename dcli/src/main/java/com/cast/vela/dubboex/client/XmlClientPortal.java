package com.cast.vela.dubboex.client;

import com.cast.vela.dubboex.api.ServiceAsync;
import com.cast.vela.dubboex.api.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CompletableFuture;

public class XmlClientPortal {
    private static final Logger log = LoggerFactory.getLogger(XmlClientPortal.class);

    public static void main(String[] args) {
        //        String url = "http://10.20.160.198/wiki/display/dubbo/client.xml";  // 支持URL部署
        String url = "client.xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{url});
        context.start();

        // 测试简单远程服务
        SimpleService remoteService = context.getBean(SimpleService.class);
        int count = 0;
        while (count++ < 3) {
            String message = remoteService.sayHello("Client" + count);
            log.info("Got remote message: {}", message);
        }
        // 测试异步调用的远程服务
        ServiceAsync remoteAsyncService = context.getBean(ServiceAsync.class);
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

        boolean flag = remoteService.isExists();
        log.info("Got remote flag: {}", flag);
        try {
            Thread.currentThread().join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

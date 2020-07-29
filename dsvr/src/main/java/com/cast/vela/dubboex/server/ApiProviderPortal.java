package com.cast.vela.dubboex.server;

import com.cast.vela.dubboex.api.ServiceAsync;
import com.cast.vela.dubboex.api.SimpleService;
import com.cast.vela.dubboex.server.service.ServiceAsyncImpl;
import com.cast.vela.dubboex.server.service.SimpleServiceImpl;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 通过API方式进行服务发布的示例
public class ApiProviderPortal {
    private static final Logger log = LoggerFactory.getLogger(ApiProviderPortal.class);

    public static void main(String[] args) throws Exception {
        startWithBootstrap();
    }

    private static void startWithBootstrap() {
        log.info("Start with Bootstrap.");
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        // 准备服务 1
        SimpleServiceImpl simpleService = new SimpleServiceImpl();
        ServiceConfig<SimpleServiceImpl> simpleServiceConfig = new ServiceConfig<>();
        simpleServiceConfig.setInterface(SimpleService.class);
        simpleServiceConfig.setRef(simpleService);
//        simpleServiceConfig.setLoadbalance("roundrobin");

        // 准备服务 2
        ServiceAsyncImpl serviceAsync = new ServiceAsyncImpl();
        ServiceConfig<ServiceAsyncImpl> asyncServiceConfig = new ServiceConfig<ServiceAsyncImpl>();
        asyncServiceConfig.setInterface(ServiceAsync.class);
        asyncServiceConfig.setRef(serviceAsync);

//        simpleServiceConfig.setLoadbalance("roundrobin");

//        RegistryConfig registryConfig = new RegistryConfig("zookeeper://192.168.71.7:2181");

        RegistryConfig registryConfig = new RegistryConfig("zookeeper://127.0.0.1:2181");
        ProtocolConfig protocolConfig = new ProtocolConfig("dubbo");
        protocolConfig.setPayload(11557050*2);  //  设置协议数据量大小限制
        bootstrap.application(new ApplicationConfig("dubbo-demo-api-provider"))
                .registry(registryConfig)
                .service(simpleServiceConfig)
                .service(asyncServiceConfig)
                .protocol(protocolConfig)
                .start()
                .await();
    }
}

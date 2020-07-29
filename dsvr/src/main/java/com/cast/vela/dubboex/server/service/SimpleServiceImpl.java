package com.cast.vela.dubboex.server.service;

import com.cast.vela.dubboex.api.SimpleService;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SimpleServiceImpl implements SimpleService {
    private static final Logger log = LoggerFactory.getLogger(SimpleServiceImpl.class);
    /**
     * 服务调用计数
     */
    private static int executiveCounter = 0;

    /**
     * @param name 输入参数
     *             example="OneName"
     * @return 返回值
     */
    @Override
    public String sayHello(String name) {
        executiveCounter++;
        log.info("Say Hello executing. Count {}", executiveCounter);
        // 下面代码用于获取附加信息，仅根据情况使用
        rpcContext();
        return "Hello " + name;
    }

    /**
     * 运行时上下文信息和参数的隐式传递
     */
    private void rpcContext() {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext == null) {
            return;
        }
        // 本端是否为提供端，这里会返回true
        boolean isProviderSide = RpcContext.getContext().isProviderSide();
        log.info("Server? {}", isProviderSide);
        // 获取调用方IP地址
        String clientIP = RpcContext.getContext().getRemoteHost();
        log.info("Client IP {}",clientIP);
        // 通过 RpcContext 上的 setAttachment 和 getAttachment 在服务消费方和提供方之间进行参数的隐式传递
        String attachment = rpcContext.getAttachment("att");
        log.info("Attachment Info {}", attachment);
    }

    @Override
    public boolean isExists(){
        log.info("文件是否存在 {}", exists());
        return exists();
    }


    private boolean exists(){
        File file = new File("D:\\shared\\TTCR _UPDATE _SCC_20200723170000.rst");
        return file.exists();
    }

    public static void main(String[] args) {
        File file = new File("D:\\shared\\TTCR _UPDATE _SCC_20200723170000.rst");
        System.out.println(file.exists());
    }

}    
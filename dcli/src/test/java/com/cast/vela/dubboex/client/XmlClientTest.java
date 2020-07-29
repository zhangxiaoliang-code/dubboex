package com.cast.vela.dubboex.client;

import com.cast.vela.dubboex.api.SimpleService;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.EchoService;
import org.junit.jupiter.api.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

public class XmlClientTest {
    static ClassPathXmlApplicationContext context;

    /**
     * 静态准备工作
     */
    @BeforeAll
    static void prepareSystem() {
        String url = "client.xml";
        context = new ClassPathXmlApplicationContext(new String[]{url});
        context.start();
    }


    @BeforeEach
    void prepareContext() {
    }

    /**
     * 回声测试用于检测服务是否可用，回声测试按照正常请求流程执行，能够测试整个调用是否通畅，可用于监控。
     * 所有服务自动实现 EchoService 接口，只需将任意服务引用强制转型为 EchoService，即可使用。
     */
    @Test
    @DisplayName("回声测试")
    void echoService() {
        // 测试简单远程服务
        SimpleService remoteService = context.getBean(SimpleService.class);
        EchoService echoService = (EchoService) remoteService;
        String param = "OK";
        Object echo = echoService.$echo(param);
        assert (echo.equals(param));
    }

    /**
     * 上下文中存放的是当前调用过程中所需的环境信息。所有配置信息都将转换为 URL 的参数，参见 schema 配置参考手册 中的对应URL参数一列。
     * RpcContext 是一个 ThreadLocal 的临时状态记录器，当接收到 RPC 请求，或发起 RPC 请求时，RpcContext 的状态都会变化。
     * 通过 RpcContext 上的 setAttachment 和 getAttachment 在服务消费方和提供方之间进行参数的隐式传递
     */
    @Test
    @DisplayName("上下文环境信息&参数的隐式传递")
    void serviceContextAttachment() {
        SimpleService remoteService = context.getBean(SimpleService.class);
        RpcContext context = RpcContext.getContext();
        assertNotNull(context);
        context.setAttachment("att", "att-value");
        remoteService.sayHello("ok");
        // 本端是否为消费端，这里会返回true
        boolean isConsumerSide = context.isConsumerSide();
        // 获取最后一次调用的提供方IP地址
        String serverIP = context.getRemoteHost();
        // 获取当前服务配置信息，所有配置信息都将转换为URL的参数
        String application = context.getUrl().getParameter("application");
        assert (application.equals("dubbo-demo-api-consumer"));
//        assert (serverIP.equals("192.168.71.185"));
        assert (serverIP.equals("127.0.0.1"));
        assert (isConsumerSide);
    }

}

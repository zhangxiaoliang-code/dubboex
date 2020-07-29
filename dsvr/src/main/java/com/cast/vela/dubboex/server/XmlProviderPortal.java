package com.cast.vela.dubboex.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class XmlProviderPortal {
    private static final Logger log = LoggerFactory.getLogger(XmlProviderPortal.class);

    public static void main(String[] args) {
//        String url = "http://10.20.160.198/wiki/display/dubbo/provider.xml";  // 支持URL部署
        String url = "service.xml";
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {url});
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(url);
        context.start();
        try {
            System.in.read(); // 按任意键退出
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.nie.netty;

import com.nie.netty.config.ESConfig;
import com.nie.netty.server.WebServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.annotation.PostConstruct;

@SpringBootApplication
public class NettyApplication {
    private final WebServer webServer = new WebServer();

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);

    }

    @PostConstruct
    public void run() throws Exception {
        new Thread(() -> {
            try {
                webServer.startServer();
                System.out.println("Netty  starting......");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }
}

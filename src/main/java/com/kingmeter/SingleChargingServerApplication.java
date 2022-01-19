package com.kingmeter;

import com.kingmeter.socket.framework.role.server.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication
public class SingleChargingServerApplication implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;


    public static void main(String[] args) {
        SpringApplication.run(SingleChargingServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        nettyServer.bind();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nettyServer.destroy()));
    }
}

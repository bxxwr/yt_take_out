package com.dy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableTransactionManagement
@SpringBootApplication
public class YtTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtTakeOutApplication.class, args);
    }


}

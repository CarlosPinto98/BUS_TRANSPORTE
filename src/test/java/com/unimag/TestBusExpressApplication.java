package com.unimag;

import org.springframework.boot.SpringApplication;

public class TestBusExpressApplication {

    public static void main(String[] args) {
        SpringApplication.from(BusExpressApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

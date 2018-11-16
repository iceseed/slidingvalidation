package com.ice.seed.slidingvalidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SlidingvalidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlidingvalidationApplication.class, args);
    }
}

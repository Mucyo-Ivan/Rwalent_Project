package com.Ivan.Rwalent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RwalentApplication {
	public static void main(String[] args) {
		SpringApplication.run(RwalentApplication.class, args);
	}
}
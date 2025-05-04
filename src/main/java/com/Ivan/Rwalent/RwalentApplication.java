package com.Ivan.Rwalent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RwalentApplication {

	public static void main(String[] args) {
		SpringApplication.run(RwalentApplication.class, args);
	}

	@RestController
	public class HomeController {
		@GetMapping("/")
		public String home() {
			return "Hello World";
		}
	}

}

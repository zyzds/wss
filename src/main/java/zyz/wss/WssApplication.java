package zyz.wss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WssApplication {

	public static void main(String[] args) {
		SpringApplication.run(WssApplication.class, args);
	}

}

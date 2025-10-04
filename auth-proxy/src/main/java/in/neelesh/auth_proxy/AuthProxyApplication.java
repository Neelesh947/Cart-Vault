package in.neelesh.auth_proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuthProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthProxyApplication.class, args);
	}

}

package ir.softwareEng.A1;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class A1Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(A1Application.class, args);
		Controller.manager = context.getBean(Accounting.class);
	}

}

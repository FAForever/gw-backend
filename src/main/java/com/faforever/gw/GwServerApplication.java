package com.faforever.gw;

import com.faforever.gw.config.GwServerProperties;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableProcessApplication("FAF Galactic War backend")
@EnableConfigurationProperties({GwServerProperties.class})
public class GwServerApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(GwServerApplication.class, args);
		context.getBean(DemoDataInitializer.class).run();
	}
}

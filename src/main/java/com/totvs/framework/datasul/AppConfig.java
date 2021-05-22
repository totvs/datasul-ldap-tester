package com.totvs.framework.datasul;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
	public Console console() {
		return new Console();
	}

}

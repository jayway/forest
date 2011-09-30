package com.jayway.restfuljersey.samples.bank.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.di.spring.SpringDependencyInjectionImpl;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

@Configuration
public class ApplicationConfig {
	@Bean
	public DependencyInjectionSPI di() {
		return new SpringDependencyInjectionImpl();
	}

	@Bean
	public AccountRepository accountRepository() {
		return new AccountRepository();
	}

	@Bean
	public AccountManager accountManager() {
		return new AccountManager();
	}
}

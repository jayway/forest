package com.jayway.forest.samples.bank.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jayway.forest.legacy.di.DependencyInjectionSPI;
import com.jayway.forest.legacy.di.spring.SpringDependencyInjectionImpl;
import com.jayway.forest.samples.bank.model.AccountManager;
import com.jayway.forest.samples.bank.repository.AccountRepository;

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

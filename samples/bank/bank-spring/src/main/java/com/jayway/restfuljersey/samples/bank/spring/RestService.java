package com.jayway.restfuljersey.samples.bank.spring;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jayway.forest.DependencyInjectionSPI;
import com.jayway.forest.di.spring.SpringDependencyInjectionImpl;
import com.jayway.forest.grove.RoleManager;
import com.jayway.jersey.rest.RestfulServlet;
import com.jayway.jersey.rest.resource.ExceptionMapper;
import com.jayway.jersey.rest.resource.Resource;
import com.jayway.jersey.rest.resource.ResourceUtil;
import com.jayway.jersey.rest.resource.Response;
import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;
import com.jayway.restfuljersey.samples.bank.spring.resources.RootResource;

public class RestService extends RestfulServlet {
	
	public RestService() {
		super(getDI());
	}
	
	private static ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
	
    private static DependencyInjectionSPI getDI() {
		return applicationContext.getBean(DependencyInjectionSPI.class);
	}

	@Override
    protected Resource root() {
        return new RootResource();
    }

    @Override
    protected void setupContext() {
    }

    @Override
    protected ExceptionMapper exceptionMapper() {
        return new ExceptionMapper() {
            public Response map(Exception e) {
                if ( e instanceof CannotDepositException) {
                    return new Response(HttpServletResponse.SC_CONFLICT, "Deposit not allowed" );
                } else if ( e instanceof OverdrawException) {
                    return new Response(HttpServletResponse.SC_CONFLICT, "Account cannot be overdrawn");
                }
                return null;
            }
        };
    }
}

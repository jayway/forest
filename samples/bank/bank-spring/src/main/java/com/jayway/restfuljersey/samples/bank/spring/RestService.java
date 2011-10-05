package com.jayway.restfuljersey.samples.bank.spring;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.jayway.forest.reflection.Transformer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.jayway.forest.core.Application;
import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.roles.Resource;
import com.jayway.forest.servlet.ExceptionMapper;
import com.jayway.forest.servlet.Response;
import com.jayway.forest.servlet.RestfulServlet;
import com.jayway.restfuljersey.samples.bank.exceptions.CannotDepositException;
import com.jayway.restfuljersey.samples.bank.exceptions.OverdrawException;
import com.jayway.restfuljersey.samples.bank.spring.resources.RootResource;

import java.util.Map;

public class RestService extends RestfulServlet implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void init() throws ServletException {
        super.init();
        Application application = new Application() {
            @Override
            public void setupRequestContext() {
            }

            @Override
            public Resource root() {
                return new RootResource();
            }

            @Override
            public Map<Class, Transformer> transformers() {
                return null;
            }
        };
        initForest(application, getDI());
    }

    private DependencyInjectionSPI getDI() {
		return applicationContext.getBean(DependencyInjectionSPI.class);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

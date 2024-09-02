package com.honda;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.honda.freemarker.PaginationTemplateDirectiveModelBean;

@Configuration
public class SpringBootConfiguration {
    @Autowired
    private freemarker.template.Configuration configuration;

    @PostConstruct
    public void init() {
    	configuration.setSharedVariable("p", new PaginationTemplateDirectiveModelBean());
    	System.out.println("==================SpringBootConfiguration=========SINGLE");
    }
}
package com.example.demo.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.client.QuoteClient;
import com.example.demo.model.Greeting;
import com.example.demo.model.Quote;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
	private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);
	
	@Autowired
	QuoteClient qc;

    //http://localhost:8080/greeting?name=James
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	Greeting greeting = null;
    	try {
        	MDC.put("uuid", "counter_" + counter);
        	Quote quote = qc.getQuote();
        	greeting = new Greeting(counter.incrementAndGet(), String.format(template, name), quote);
        	logger.info(greeting.toString());
        	logger.info("Greeting starts...");
    	} finally {
        	MDC.remove("uuid");
    	}
    	return greeting;
    }
    
    //https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
    @RequestMapping("/cors")
    public ResponseEntity<Greeting> cors() {
    	Quote quote = qc.getQuote();
    	Greeting greeting = new Greeting(counter.incrementAndGet(), "CORS", quote);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<Greeting>(greeting, responseHeaders, HttpStatus.OK);
    }
}

//https://api.swaggerhub.com/apis/jamesyangwang
//http://petstore.swagger.io/?url=https://api.swaggerhub.com/apis/jamesyangwang/CORS/0.1
//http://petstore.swagger.io/?url=https://api.swaggerhub.com/apis/jamesyangwang/Greeting/0.1

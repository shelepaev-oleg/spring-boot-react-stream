package com.example.springbootreactstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.TimeZone;

@SpringBootApplication
public class SpringBootReactStream {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactStream.class, args);
    }

    @PostConstruct
    public void init(){
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() throws IOException {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(-1);
        // Установим папку, с которой будет работать java для временного хранения файлов
        // На проде лучше так не делать (оставить по умолчанию)
        multipartResolver.setUploadTempDir(new FileSystemResource(Paths.get("C:/temp1")));
        return multipartResolver;
    }

    // #1 https://coderoad.ru/31469136/Spring-data-jpa-%D1%85%D1%80%D0%B0%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5-blob
//    @Bean // Need to expose SessionFactory to be able to work with BLOBs
//    public SessionFactory sessionFactory(HibernateEntityManagerFactory hemf) {
//        return hemf.getSessionFactory();
//    }
}

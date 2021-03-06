package com.poc.livraria.livraria;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LivrariaApplication {

    @Bean
    public ModelMapper modelmapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(LivrariaApplication.class, args);
    }

}

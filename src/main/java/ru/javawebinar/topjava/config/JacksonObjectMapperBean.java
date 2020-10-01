package ru.javawebinar.topjava.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javawebinar.topjava.web.json.JacksonObjectMapper;

@Configuration
public class JacksonObjectMapperBean {

    @Bean
    public ObjectMapper objectMapper(){
        return JacksonObjectMapper.getMapper();
    }
}

package com.mycompany.appointmentservice.configuration;

import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setPropertyCondition(context -> !(context.getSource() instanceof PersistentCollection))
                .setSkipNullEnabled(true);
        return modelMapper;
    }
}

package com.mycompany.appointmentservice.configuration;

import com.cloudinary.Cloudinary;
import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Value("${CLOUDINARY_CLOUD_NAME}")
    private String cloudinaryCloudName;

    @Value("${CLOUDINARY_API_KEY}")
    private String cloudinaryApiKey;

    @Value("${CLOUDINARY_API_SECRET}")
    private String cloudinaryApiSecret;

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setPropertyCondition(context -> !(context.getSource() instanceof PersistentCollection))
                .setSkipNullEnabled(true);
        return modelMapper;
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", cloudinaryCloudName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinaryApiSecret));
    }
}

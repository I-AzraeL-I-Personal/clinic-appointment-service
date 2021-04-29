package com.mycompany.appointmentservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void save(MultipartFile file, String directory, String name) throws Exception;

    Resource load(String directory, String name) throws Exception;
}

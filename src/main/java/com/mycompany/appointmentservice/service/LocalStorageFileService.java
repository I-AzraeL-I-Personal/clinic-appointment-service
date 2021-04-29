package com.mycompany.appointmentservice.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class LocalStorageFileService implements FileService {

    private final String uploadFolder = "uploads";

    public void save(MultipartFile file, String directory, String name) throws Exception {
        var path = Paths.get(uploadFolder, directory, name);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
    }

    public Resource load(String directory, String name) throws Exception {
        var resource = new FileSystemResource(Paths.get(uploadFolder, directory, name));
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException(name + " not found");
        }
        return resource;
    }
}

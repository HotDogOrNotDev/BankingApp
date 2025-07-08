package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.domain.response.SimpleMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    SimpleMessage uploadImage(MultipartFile multipartFile);

    ByteArrayResource downloadImage();

}

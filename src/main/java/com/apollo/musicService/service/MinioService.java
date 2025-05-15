package com.apollo.musicService.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

	String uploadFile(MultipartFile file);

	void deleteFile(String fileUrl);

	Resource getFile(String fileName);
}

package com.example.springbootreactstream.service;

import com.example.springbootreactstream.model.TestFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {

    void uploadFileStream(MultipartFile file) throws Exception;

    void uploadFileStream(InputStream inputStream, String fileName, long length);

    void downloadFileOutputStream(Long testFileId, OutputStream outputStream);

    TestFile getTestFile(Long testFileId);
}

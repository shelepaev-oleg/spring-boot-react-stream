package com.example.springbootreactstream.controller;

import com.example.springbootreactstream.model.TestFile;
import com.example.springbootreactstream.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Objects;

@RestController
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/api/rest/upload-multi-part-file")
    public void uploadMultipartFile(@RequestParam(value = "file") final MultipartFile file) throws Exception {
        // Потоковое сохранение
        fileService.uploadFileStream(file);
    }

    /**
     * Можно загрузить файл через Postman
     * @param fileName
     * @param length
     * @param inputStream
     */
    @PostMapping(path = "/api/rest/upload-file")
    public void uploadFile(@RequestParam(value = "fileName", required = false) final String fileName,
                           @RequestHeader(HttpHeaders.CONTENT_LENGTH) final long length,
                           final InputStream inputStream) {
        fileService.uploadFileStream(inputStream, fileName, length);
    }

    /**
     * https://medium.com/swlh/streaming-data-with-spring-boot-restful-web-service-87522511c071
     */
    // http://localhost:8080/api/rest/download
    @GetMapping (value = "/api/rest/download/{testFileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> download(final HttpServletResponse response,
                                                          @PathVariable final Long testFileId) {
        TestFile testFile = fileService.getTestFile(testFileId);

        //StreamSaver.js:49 Resource interpreted as Document but transferred with MIME type application/octet-stream: "https://jimmywarting.github.io/StreamSaver.js/localhost:3000/759937/apache-maven-3.6.3-bin.zip".
        response.setContentType("application/octet-stream");
//        response.setContentType("application/zip");
        response.setHeader(
                "Content-Disposition",
                String.format("attachment;filename=%s", Objects.toString(testFile.getName(), "file")));
        StreamingResponseBody stream = out -> {
            fileService.downloadFileOutputStream(testFileId, response.getOutputStream());
        };
        return new ResponseEntity(stream, HttpStatus.OK);
    }
}

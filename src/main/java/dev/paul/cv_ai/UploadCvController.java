package dev.paul.cv_ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController

public class UploadCvController {
    private static final Log logger = LogFactory.getLog(UploadCvController.class);

    @PostMapping(path = "/uploadcv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> handleCvUpload(@RequestParam("cv") MultipartFile cv) {
        String cvName = cv.getOriginalFilename();
        try {
            cv.transferTo(new File("/Users/paulioanalbu/Downloads/testcv/" + cvName));

        } catch (Exception e) {
            logger.error("Could not upload CV. Exception is: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("File uploaded successfully");
    }


}

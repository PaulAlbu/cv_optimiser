package dev.paul.cv_ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController

public class UploadCvController {
    private static final Log logger = LogFactory.getLog(UploadCvController.class);

    @PostMapping(path = "/uploadcv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> handleCvUpload(@RequestParam("cv") MultipartFile cv) throws IOException {
        String cvName = cv.getOriginalFilename();
        String htmlFIleName = String.format("/Users/paulioanalbu/Downloads/testcv/%s.html", cvName);

        try(PDDocument pdf = PDDocument.load(cv.getInputStream());
            Writer output = new PrintWriter(htmlFIleName, "utf-8")) {

            new PDFDomTree().writeText(pdf, output);
            cv.transferTo(new File("/Users/paulioanalbu/Downloads/testcv/" + cvName));
            //TODO:
            // check how can we transform the HTML file to a string and then feed it to
//            try (PDDocument pdf = PDDocument.load(cv.getInputStream());
//                 StringWriter output = new StringWriter()) {
//
//                new PDFDomTree().writeText(pdf, output);
//                String htmlContent = output.toString();
//
//                // If you still want to save the uploaded file
//                cv.transferTo(new File("/Users/paulioanalbu/Downloads/testcv/" + cvName));
//
//                // Optionally save HTML to disk:
//                try (PrintWriter fileWriter = new PrintWriter("/Users/paulioanalbu/Downloads/testcv/" + cvName + ".html", "UTF-8")) {
//                    fileWriter.write(htmlContent);
//                }

        } catch (Exception e) {
            logger.error("Error in manipulating the CV. Exception is: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("File uploaded successfully");
    }


}

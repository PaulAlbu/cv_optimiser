package dev.paul.cv_ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.paul.cv_ai.service.UtilityService.convertDocxToMarkdown;


@RestController

public class UploadCvController {
    private final ChatClient chatClient;
    private static final Log logger = LogFactory.getLog(UploadCvController.class);
    public UploadCvController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping(path = "/uploadcv", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> handleCvUpload(@RequestParam("cv") MultipartFile cv) throws IOException {
        String cvName = cv.getOriginalFilename();
        String htmlFIleName = String.format("/Users/paulioanalbu/Downloads/testcv/%s", cvName);

        Path outMd = Paths.get("/Users/paulioanalbu/Downloads/markdownFile",
                cv.getOriginalFilename() + ".md");

        try {

            String md = convertDocxToMarkdown(cv.getInputStream(), outMd);

            String messageToAi = "This is part of the an HR JD: " +
                    "Update this CV experience with the above key areas from the JD \n" ;
            String systemMessage = """
                    You are an AI that edits CV HTML. You MUST NOT:
                    - Add any extra text (no explanations, greetings, apologies).
                    - Change formatting or annotations—only modify text content.
                    - Ask any fllow-up questions
                    Output EXACTLY one HTML block (<html>…</html>) with the edited CV.
                    Respond ONLY with the HTML and nothing else, wrapped in a single ```html ... ``` code block.
                    """;
            var response = chatClient.prompt()
                    .system(systemMessage)
                    .user(messageToAi)
                    .call()
                    .content();


            System.out.println(response);

            cv.transferTo(new File("/Users/paulioanalbu/Downloads/testcv/" + cvName));


        } catch (Exception e) {
            logger.error("Error in manipulating the CV. Exception is: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("File uploaded successfully");
    }


}

package dev.paul.cv_ai.service;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.apache.coyote.BadRequestException;
import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;


@Service

public class UtilityService {
    public static void generateDocxFromString(String htmlString, String outputPath) throws Exception {
        // 1) Create empty docx
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        // 2) Import XHTML into it
        XHTMLImporterImpl importer = new XHTMLImporterImpl(wordMLPackage);
        wordMLPackage.getMainDocumentPart()
                .getContent()
                .addAll(importer.convert(htmlString, null));
        // 3) Save to .docx
        wordMLPackage.save(new File(outputPath));
    }


    public static String convertDocxToMarkdown(InputStream docxStream, Path outputMdPath) throws Exception {
        // Save the input stream to a temporary file
        Path tempDocx = Files.createTempFile("temp_cv", ".docx");
        Files.copy(docxStream, tempDocx, StandardCopyOption.REPLACE_EXISTING);

        try {
            // Build pandoc command
            ProcessBuilder pb = new ProcessBuilder(
                    "pandoc",
                    "-f", "docx",
                    "-t", "gfm",  // GitHub Flavored Markdown
                    "--wrap=none",
                    "--atx-headers",
                    "--extract-media=" + outputMdPath.getParent().resolve("images"),
                    tempDocx.toString(),
                    "-o", outputMdPath.toString()
            );

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Pandoc conversion failed with exit code: " + exitCode);
            }

            // Read the generated markdown
            return Files.readString(outputMdPath, StandardCharsets.UTF_8);

        } finally {
            // Clean up temp file
            Files.deleteIfExists(tempDocx);
        }
    }




}

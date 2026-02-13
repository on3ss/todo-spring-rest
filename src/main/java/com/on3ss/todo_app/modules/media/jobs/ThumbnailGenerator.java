package com.on3ss.todo_app.modules.media.jobs;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ThumbnailGenerator {

    private static final String THUMB_PREFIX = "thumb_";

    public String generate(String originalPath) throws IOException {
        Path source = Paths.get(originalPath);
        File inputFile = source.toFile();
        
        // Define output path: e.g., uploads/thumb_image.jpg
        String fileName = source.getFileName().toString();
        String folder = source.getParent().toString();
        Path targetPath = Paths.get(folder, THUMB_PREFIX + fileName);

        // Actual Resizing Logic
        Thumbnails.of(inputFile)
                .size(200, 200) // Max width/height while maintaining aspect ratio
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toFile(targetPath.toFile());

        return targetPath.toString();
    }
}
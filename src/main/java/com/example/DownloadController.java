package com.example;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Controller("download")
public class DownloadController
{
    private static final int SIZE = 1024 * 1024 * 8; // 8 Mb

    @Get(value = "{filename}", produces = {MediaType.MULTIPART_FORM_DATA})
    public StreamedFile download(@QueryValue String filename) throws IOException
    {
        File tempFile = generateRandomFile(filename);

        return new StreamedFile(new FileInputStream(tempFile),
            MediaType.forFilename(filename),
            Instant.now().toEpochMilli(),
            tempFile.length())
                   .attach(filename);
    }

    public static File generateRandomFile(String filename) throws IOException
    {
        final var file = Files.createTempFile(null, filename).toFile();
        file.createNewFile();
        try (FileOutputStream out = new FileOutputStream(file))
        {
            byte[] bytes = new byte[(int) ThreadLocalRandom.current().nextLong(SIZE)];
            ThreadLocalRandom.current().nextBytes(bytes);
            out.write(bytes);
        }
        return file;
    }
}

package bsa.java.concurrency.service;

import bsa.java.concurrency.fs.FileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FileSystemService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    @Value("${cache.path}")
    private String cachePath;

    @Async
    public CompletableFuture<String> saveFile(String fileName, byte[] file) {
        ensureThatCacheExists();
        //CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
        String pathToImage = cachePath + "/" + fileName + ".jpg";
        File image = new File(pathToImage);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(image);
            fileOutputStream.write(file);
        } catch (FileNotFoundException ex) {
            logger.info("File not found!", ex);
        } catch (IOException ex) {
            logger.info("An error occured while writing file content", ex);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ex) {
                logger.info("Error while closing stream", ex);
            }
           // return pathToImage;
        }
       // });
        return CompletableFuture.completedFuture(pathToImage);
    }

    private void ensureThatCacheExists() {
        File cache = new File(cachePath);
        if (!cache.exists()) {
            cache.mkdir();
        }
    }

    public void deleteImage(String imageId) {
        try {
            FileSystemUtils.deleteRecursively(Path.of(cachePath + "/" + imageId + ".jpg"));
        } catch (IOException ex) {
            logger.info("Error while deleting file form cache", ex);
        }
    }

    public void clearDiskStorage() {
        try {
            FileSystemUtils.deleteRecursively(Path.of(cachePath));
        } catch (IOException ex) {
            logger.info("Error while deleting cache directory", ex);
        }
    }

}

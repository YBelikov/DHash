package bsa.java.concurrency.service;

import bsa.java.concurrency.image.Image;
import bsa.java.concurrency.image.dto.ImageSearchResultDTO;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);
    private List<Image> images = new ArrayList<>();

    private HashingService hashingService;
    private FileSystemService fileSystemService;

    @Autowired
    public BatchService(HashingService hashingService, FileSystemService fileSystemService) {
        this.hashingService = hashingService;
        this.fileSystemService = fileSystemService;
    }

    public void saveImage(MultipartFile file) {

        UUID id = UUID.randomUUID();
        try {
            //var filePathFuture = saveImageToDisk(id, file);
            //long hash = computeHash(file);
            //addRecordToPersistentStorage(id, filePathFuture, hash);
            long hash = hashingService.computeHashOfImage(file);
            String filePath = fileSystemService.saveFile(id.toString(), file.getBytes()).get();
            Image image = new Image(id, filePath, hash);
            images.add(image);
        } catch (Exception ex) {
            logger.info("Error while computing hash", ex);
        }
    }

    private long computeHash(MultipartFile file) {
        long hash = 0;
        try {
            hash = hashingService.computeHashOfImage(file);
        } catch (Exception ex) {
            logger.info("Error while computing hash", ex);
        }
        return hash;
    }


    public String saveImageToDisk(UUID id, MultipartFile file) {
        try {
            return fileSystemService.saveFile(id.toString(), file.getBytes()).get();
        } catch (IOException | InterruptedException | ExecutionException ex) {
            logger.info("Error while processing file", ex);
        }
        return "";
    }

    private void addRecordToPersistentStorage(UUID id, String filePath, long hash) {
        Image image = new Image(id, filePath, hash);
        images.add(image);
    }
    public List<ImageSearchResultDTO> searchImages(MultipartFile file, double threshold) {
       try {

           long hash = hashingService.computeHashOfImage(file);
           List<ImageSearchResultDTO> findImages = findImages(hash, threshold);
           if(findImages.isEmpty()) {
               addNewFileToCache(file, hash);
           }
           return findImages;
       } catch (Exception ex) {
           logger.info("Error while computing hash", ex);
       }
      return new ArrayList<>();
    }

    private List<ImageSearchResultDTO> findImages(long hash, double threshold) {
        List<ImageSearchResultDTO> matches = new ArrayList<ImageSearchResultDTO>();
        System.out.println(hash);
        for (var image : images) {
            Double match = Double.valueOf(1 - (double)(countSetBits(hash ^ image.getHash())) / 64);
            if (match >= threshold) {
                matches.add(new ImageSearchResultDTO(image.getId(), match * 100, image.getPathToFile()));
            }
        }
        return matches;
    }

    private int countSetBits(long value) {
        int bitCounter = 0;
        while(value != 0) {
            value = (value & (value - 1));
            ++bitCounter;
        }
        return bitCounter;
    }

    public void deleteImage(UUID imageId) {
        fileSystemService.deleteImage(imageId.toString());
        for (var image : images) {
            if (image.getId() == imageId) {
                images.remove(image);
            }
        }
    }

    public void clearStorage() {
        fileSystemService.clearDiskStorage();
        images.clear();
    }

    @Async
    public void addNewFileToCache(MultipartFile file, long hash) {
        UUID id = UUID.randomUUID();
        String filePath = saveImageToDisk(id, file);
        addRecordToPersistentStorage(id, filePath, hash);
    }
}

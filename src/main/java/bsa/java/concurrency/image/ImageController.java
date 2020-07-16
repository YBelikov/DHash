package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.ImageSearchResultDTO;
import bsa.java.concurrency.service.ImageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.Size;


@RestController
@RequestMapping("/image")
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    @Autowired
    private ImageService imageService;

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        logger.info("POST: /image/batch");
        Stream.of(files).parallel().forEach(x -> imageService.saveImage(x));
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ImageSearchResultDTO> searchMatches(@RequestParam("image") MultipartFile file, @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        logger.info("POST: /image/search");
        return imageService.searchImages(file, threshold);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        logger.info("DELETE: /image/{id}");
        imageService.deleteImage(imageId);
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages(){
        logger.info("DELETE: /image/purge");
        imageService.clearStorage();
    }
}

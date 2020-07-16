package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.ImageSearchResultDTO;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import bsa.java.concurrency.service.BatchService;
import bsa.java.concurrency.service.HashingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private BatchService batchService;

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        Stream.of(files).parallel().forEach(x -> batchService.saveImage(x));
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ImageSearchResultDTO> searchMatches(@RequestParam("image") MultipartFile file, @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        return batchService.searchImages(file, threshold);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        batchService.deleteImage(imageId);
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages(){
        batchService.clearStorage();
    }
}

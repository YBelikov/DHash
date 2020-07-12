package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchUploadImages(@RequestParam("images") MultipartFile[] files) {
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<SearchResultDTO> searchMatches(@RequestParam("image") MultipartFile file, @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {

    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages(){
    }
}

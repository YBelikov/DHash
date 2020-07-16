package bsa.java.concurrency.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private UUID id;
    private String pathToFile;
    private long hash;
}

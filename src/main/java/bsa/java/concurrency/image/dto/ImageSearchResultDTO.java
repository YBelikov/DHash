package bsa.java.concurrency.image.dto;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class ImageSearchResultDTO implements SearchResultDTO {

    private UUID id;
    private Double match;
    private String imageUrl;

    @Override
    public UUID getImageId() {
        return this.id;
    }

    @Override
    public Double getMatchPercent() {
        return this.match;
    }

    @Override
    public String getImageUrl() {
        return this.imageUrl;
    }
}


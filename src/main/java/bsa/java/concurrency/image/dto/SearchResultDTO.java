package bsa.java.concurrency.image.dto;


import lombok.Data;
import java.util.UUID;

@Data
//Если решите использовать базу данных в качестве персистентного хранилища, вам прийдется ипользовать аннотацию для маппинга, если хотите возвращать из репозитория сразу DTO.
// Помните, что UUID типа "uuid-char" маппится к String, а числа - к BigDecimal
public final class SearchResultDTO {
    private UUID imageId;
    private double matchPercent;
    private String imageUrl;
}

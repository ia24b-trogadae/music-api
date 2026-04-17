package ch.example.musicapi.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Album {

    private Integer id;

    @NotNull(message = "Title must not be null")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotNull(message = "Artist must not be null")
    @Size(min = 2, max = 100, message = "Artist must be between 2 and 100 characters")
    private String artist;

    @Size(max = 50, message = "Genre must be at most 50 characters")
    private String genre;

    @NotNull(message = "Release date must not be null")
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Track count must not be null")
    @Min(value = 1, message = "Track count must be at least 1")
    private Integer trackCount;

    @NotNull(message = "Published must not be null")
    private Boolean published;

    @Valid
    private List<Song> songs = new ArrayList<>();
}

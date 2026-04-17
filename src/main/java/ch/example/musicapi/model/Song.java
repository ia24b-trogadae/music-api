package ch.example.musicapi.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song {

    private Integer id;

    @NotNull(message = "Title must not be null")
    @Size(min = 2, max = 100, message = "Song title must be between 2 and 100 characters")
    private String title;

    @NotNull(message = "Duration must not be null")
    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer durationSeconds;

    private String featuring;

    @NotNull(message = "Explicit flag must not be null")
    private Boolean isExplicit;

    private Integer albumId;
}

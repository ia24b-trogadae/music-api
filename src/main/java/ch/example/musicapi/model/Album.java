package ch.example.musicapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@Getter
@Setter
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Title must not be null")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotNull(message = "Artist must not be null")
    @Size(min = 2, max = 100, message = "Artist must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String artist;

    @Size(max = 50, message = "Genre must be at most 50 characters")
    @Column(length = 50)
    private String genre;

    @NotNull(message = "Release date must not be null")
    @PastOrPresent(message = "Release date cannot be in the future")
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Track count must not be null")
    @Min(value = 1, message = "Track count must be at least 1")
    @Column(name = "track_count")
    private Integer trackCount;

    @NotNull(message = "Published must not be null")
    private Boolean published;

    @Valid
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Song> songs = new ArrayList<>();
}
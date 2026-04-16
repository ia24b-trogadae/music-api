package ch.example.musicapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "song")
@Getter
@Setter
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Title must not be null")
    @Size(min = 2, max = 100, message = "Song title must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotNull(message = "Duration must not be null")
    @Min(value = 1, message = "Duration must be at least 1 second")
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(length = 100)
    private String featuring;

    @NotNull(message = "Explicit flag must not be null")
    @Column(name = "is_explicit")
    private Boolean isExplicit;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    @JsonBackReference
    private Album album;
}
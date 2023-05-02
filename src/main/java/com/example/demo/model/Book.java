package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotEmpty
    String title;
    @NotEmpty
    String author;
    @Column(columnDefinition = "text")
    @NotEmpty
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    Date datepublic;
    @Min(value = 1)
    @Max(value = 1000)
    int pagenumber;
    @NotBlank
    String category;
    @Min(1)
    @Max(100)
    float price;
    String imagepath;
    @OneToMany(mappedBy = "book",cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Review> reviewList;
}

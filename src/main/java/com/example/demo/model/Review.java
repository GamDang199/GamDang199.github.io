package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "Text")
    private String comment;
    @CollectionTable
    private float rating;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date datepost;
    @ManyToOne
    @JoinColumn(name = "book_id",referencedColumnName = "id")
    @JsonBackReference
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference
    private User user;
}

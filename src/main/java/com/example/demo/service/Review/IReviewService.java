package com.example.demo.service.Review;

import com.example.demo.model.Review;

import java.util.List;

public interface IReviewService {
    public Review create(Review review);

    List<Review> findAllReviewByIdBook(int id);
}

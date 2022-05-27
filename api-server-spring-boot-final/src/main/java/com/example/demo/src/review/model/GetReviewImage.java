package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class GetReviewImage {
    private int reviewId;
    private String restaurantName;
    private int imgId;
    private String imgUrl;


}

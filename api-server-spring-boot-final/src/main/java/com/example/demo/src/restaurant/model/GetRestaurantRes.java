package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantRes {
    private BigInteger id;
    private String name;
    private String thirdRegion;
    private String foodCategory;
    private Double latitude;
    private Double longitude;
    private String imgUrl;
    private Double rating;
    private Integer numReviews;
}

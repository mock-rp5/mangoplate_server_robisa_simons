package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantRes {
    @Getter
    private Long id;
    private String name;
    private String regionName;
    private String foodCategory;
    @Getter
    private Double latitude;
    @Getter
    private Double longitude;
    @Getter
    @Setter
    private Double ratingsAvg;
    private Integer numReviews;
    @Getter
    @Setter
    private Double distance;
    @Getter
    @Setter
    private Integer isWishes;
    private String imgUrl;

    public GetRestaurantRes(Long id, String name, String regionName, String foodCategory, Double latitude, Double longitude,
                            Integer numReviews, String imgUrl) {
        this.id = id;
        this.name = name;
        this.regionName = regionName;
        this.foodCategory = foodCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numReviews = numReviews;
        this.imgUrl = imgUrl;
    }
}

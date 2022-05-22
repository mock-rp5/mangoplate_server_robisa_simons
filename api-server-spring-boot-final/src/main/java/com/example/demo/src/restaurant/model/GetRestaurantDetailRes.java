package com.example.demo.src.restaurant.model;

import com.example.demo.src.review.model.GetReviewRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantDetailRes {
    private BigInteger id;
    private String name;
    private Integer view;
    private String address;
    private Double latitude;
    private Double longitude;
    private String dayOff;
    private String openHour;
    private String closeHour;
    private String breakTime;
    private String priceInfo;
    private String parkInfo;
    private String website;
    private String foodCategory;
    private List<String> imgUrl;
    private List<GetReviewRes> reviews;

}

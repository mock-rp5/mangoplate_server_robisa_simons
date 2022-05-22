package com.example.demo.src.restaurant.model;

import com.example.demo.src.menu.model.GetRestaurantMenu;
import com.example.demo.src.review.model.GetReviewRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantDetailRes {
    private Integer id;
    private String name;
    private Integer view;
    private float score;
    private String address;
    private Double latitude;
    private Double longitude;
    private String dayOff;
    private String openHour;
    private String closeHour;
    private String breakTime;
    private int minPrice;
    private int maxPrice;
    private String parkInfo;
    private String website;
    private int foodCategoryId;
    private String foodCategoryName;
    private List<String> imgUrls;
    private List<GetReviewRes> reviews;
    private List<GetRestaurantMenu> menus;

    public GetRestaurantDetailRes(Integer id, String name, Integer view, String address, Double latitude, Double longitude, String dayOff, String openHour, String closeHour, String breakTime,int minPrice, int maxPrice, String parkInfo, String website, int foodCategoryId,String foodCategoryName) {
        this.id = id;
        this.name = name;
        this.view = view;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayOff = dayOff;
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.breakTime = breakTime;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.parkInfo = parkInfo;
        this.website = website;
        this.foodCategoryId = foodCategoryId;
        this.foodCategoryName = foodCategoryName;
    }
}

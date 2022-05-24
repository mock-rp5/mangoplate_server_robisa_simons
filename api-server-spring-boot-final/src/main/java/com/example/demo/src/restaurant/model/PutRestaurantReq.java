package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PutRestaurantReq {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer foodCategory;
    private Integer userId;

}

package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDealRes {
    private int restaurantId;
    private String restaurantName;
    private String menuName;
    private int price;
    private double discountRate;
    private String desc;
}

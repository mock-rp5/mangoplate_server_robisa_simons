package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDeal {
    private int restaurantId;
    private String restaurantName;
    private String restaurantDesc;
    private String menuDesc;
    private String notice;
    private String manual;
    private String refundPolicy;
    private String question;
    private int price;
    private int discountRate;
    private String menuName;
    private String startDate;
    private String endDate;
    private int expiredDate;
    private String emphasis;

}

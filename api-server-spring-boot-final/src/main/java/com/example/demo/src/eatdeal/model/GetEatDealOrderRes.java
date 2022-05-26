package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDealOrderRes {
    private int id;
    private int userId;
    private int restaurantId;
    private String restaurantName;
    private int menuId;
    private String menuName;
    private int price;

    public GetEatDealOrderRes(int id, int userId, int restaurantId, int menuId, int price) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.menuId = menuId;
        this.price = price;
    }
}

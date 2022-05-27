package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVisit {
    private int visitId;
    private int restaurantId;
    private GetRestaurantInfo getRestaurantInfo;

    public GetVisit(int visitId, int restaurantId) {
        this.visitId = visitId;
        this.restaurantId = restaurantId;
    }
}

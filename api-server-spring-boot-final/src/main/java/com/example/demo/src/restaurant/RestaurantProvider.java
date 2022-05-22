package com.example.demo.src.restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RestaurantProvider {
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantProvider.class);

    public RestaurantProvider(RestaurantDao dao) {
        this.dao = dao;
    }
}

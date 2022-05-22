package com.example.demo.src.restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {
    private final RestaurantProvider provider;
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    public RestaurantService(RestaurantProvider provider, RestaurantDao dao) {
        this.provider = provider;
        this.dao = dao;
    }
}

package com.example.demo.src.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RestaurantDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

}

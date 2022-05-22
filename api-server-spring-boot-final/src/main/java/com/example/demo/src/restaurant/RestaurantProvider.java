package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class RestaurantProvider {
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantProvider.class);

    public RestaurantProvider(RestaurantDao dao) {
        this.dao = dao;
    }

    public List<GetRestaurantRes> getRestaurant(Double latitude, Double longitude, String foodCategories) throws BaseException{
        try{
            if (latitude==null){ System.out.println("this is a Null Data2");}
            List<GetRestaurantRes> getRestaurantRes = dao.getRestaurant(latitude, longitude, foodCategories);
            return getRestaurantRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

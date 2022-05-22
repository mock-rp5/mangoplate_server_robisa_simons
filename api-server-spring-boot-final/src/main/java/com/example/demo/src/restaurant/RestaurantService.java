package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_NOT_EXISTS_RESTAURANT;

@Service
public class RestaurantService {
    private final RestaurantProvider provider;
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    public RestaurantService(RestaurantProvider provider, RestaurantDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    public int increaseView(Integer restaurantId) throws BaseException {
        if(provider.checkRestaurantId(restaurantId) == 0 ) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            return dao.increaseView(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;
import com.example.demo.src.review.model.GetReviewRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_NOT_EXISTS_RESTAURANT;

@Service
public class RestaurantProvider {
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantProvider.class);

    public RestaurantProvider(RestaurantDao dao) {
        this.dao = dao;
    }

    /**
     * 식당 존재 여부 체크
     * @param restaurantId
     * @return
     * @throws BaseException
     */
    public GetRestaurantDetailRes getRestaurantDetail(Integer restaurantId) throws BaseException {
        if(dao.checkRestaurantId(restaurantId) == 0 ) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            return dao.getRestaurantDetail(restaurantId);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
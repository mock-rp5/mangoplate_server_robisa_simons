package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
<<<<<<< HEAD
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
=======
import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;
import com.example.demo.src.review.model.GetReviewRes;
>>>>>>> main
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_NOT_EXISTS_RESTAURANT;

@Service
public class RestaurantProvider {
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantProvider.class);

    public RestaurantProvider(RestaurantDao dao) {
        this.dao = dao;
    }

<<<<<<< HEAD
    public List<GetRestaurantRes> getRestaurant(Double latitude, Double longitude, String foodCategories) throws BaseException{
        try{
            if (latitude==null){ System.out.println("this is a Null Data2");}
            List<GetRestaurantRes> getRestaurantRes = dao.getRestaurant(latitude, longitude, foodCategories);
            return getRestaurantRes;
        } catch (Exception exception) {
=======
    /**
     * 식당 존재 여부 체크
     * @param restaurantId
     * @return
     * @throws BaseException
     */
    public GetRestaurantDetailRes getRestaurantDetail(Integer restaurantId) throws BaseException {
        if(checkRestaurantId(restaurantId) == 0 ) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            return dao.getRestaurantDetail(restaurantId);
        } catch (Exception e) {
>>>>>>> main
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRestaurantId(int restaurantId)throws BaseException {
        try {
            return dao.checkRestaurantId(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

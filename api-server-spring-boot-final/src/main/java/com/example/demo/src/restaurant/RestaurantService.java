package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import com.example.demo.src.restaurant.model.PostRestaurantReq;
import com.example.demo.src.restaurant.model.PostRestaurantRes;
import com.example.demo.src.user.model.PostUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

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
    public PostRestaurantRes createRestaurant(PostRestaurantReq postRestaurantReq) throws BaseException {
        try {
            if(dao.findByNameAndAddress(postRestaurantReq).equals(1)){
                throw new BaseException(POST_RESTAURANTS_EXISTS_RESTAURANT);
            }else {
                return dao.createRestaurant(postRestaurantReq);
            }
        }  catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String deleteRestaurant(Integer restaurantId) throws BaseException {
        try {

            if(dao.checkRestaurantId(restaurantId) == 1){
                if(dao.deleteRestaurant(restaurantId).equals(1)){
                    return new String("1 RESTARANT DELETE SUCCESS");
                }else {
                    throw new BaseException(DELETE_FAIL_RESTAURANT);
                }
            } else {
                throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
            }

        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.src.wishes.model.GetWishRestaurantRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_NOT_EXISTS_RESTAURANT;

@Service
public class WishProvider {

    final Logger logger = LoggerFactory.getLogger(WishProvider.class);
    private final WishDao dao;

    @Autowired
    public WishProvider(WishDao dao) { this.dao = dao;}

    public int getWish(int restaurantId, int userId) throws BaseException{
        try {
            //유저-레스토랑 관계의 wish는 하나의 row만 있으면 되므로, 특정 유저가 특정 식당에 대한 wish 데이터가 존재하면 status로 관리한다.
            int wishId = dao.findWishId(restaurantId,userId);
            return wishId != 0 ? dao.getWish(wishId) : 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetWishRestaurantRes> getWishRestaurants(Integer userId, Integer targetUserId) throws BaseException{
        if(dao.checkUser(targetUserId) == 0 ) throw new BaseException(USERS_NOT_EXISTS_USER);

        try {
            //유저-레스토랑 관계의 wish는 하나의 row만 있으면 되므로, 특정 유저가 특정 식당에 대한 wish 데이터가 존재하면 status로 관리한다.
            List<GetWishRestaurantRes> getWishRestaurantRes = dao.getWishRestaurants(userId,targetUserId);
            return getWishRestaurantRes;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public Integer checkWishId(Integer wishId) throws BaseException{
        try {
            return dao.checkWishId(wishId);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Integer getUserIdFromWish(Integer wishId) throws BaseException{
        try {
            return dao.getUserIdFromWish(wishId);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}






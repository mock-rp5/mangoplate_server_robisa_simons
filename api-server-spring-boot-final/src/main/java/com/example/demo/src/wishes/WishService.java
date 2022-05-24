package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.src.restaurant.RestaurantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class WishService {
    private final WishProvider provider;
    private final WishDao dao;

    final Logger logger = LoggerFactory.getLogger(com.example.demo.src.wishes.WishService.class);

    public WishService(WishProvider provider, WishDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public int postWish(Integer restaurantId, Integer userId) throws BaseException {

        try {
            //유저-레스토랑 관계의 wish는 하나의 row만 있으면 되므로, 특정 유저가 특정 식당에 대한 wish 데이터가 존재하면 status로 관리한다.
            int wishId = dao.findWishId(restaurantId,userId);

            if(wishId == 0){
                if(dao.checkRestaurantId(restaurantId) == 1){
                    int result = dao.postWish(restaurantId, userId);
                    if(result == 0){
                        logger.warn("[WishService] postWish fail, userId: {}, restaurantId: {}", userId, restaurantId);
                        throw new BaseException(WISHES_POST_FAIL);
                    }
                    return result;
                } else {
                    throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
                }
            } else {
                return dao.changeStatusToActive(wishId);
            }

        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteWish(Integer wishId) throws BaseException {

        try {
            if(dao.checkWishId(wishId) == 1){
                int result = dao.deleteWish(wishId);
                if(result == 0){
                    logger.warn("[WishService] deleteWish fail, wishId: {}", wishId);
                    throw new BaseException(WISHES_POST_FAIL);
                }
                return result;
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
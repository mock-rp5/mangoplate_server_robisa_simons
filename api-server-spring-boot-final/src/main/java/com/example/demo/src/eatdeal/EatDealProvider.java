package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.src.eatdeal.model.GetEatDealOrderRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class EatDealProvider {
    private final EatDealDao dao;

    final Logger logger = LoggerFactory.getLogger(EatDealProvider.class);

    @Autowired
    public EatDealProvider(EatDealDao dao) {
        this.dao = dao;
    }

    public List<GetEatDealRes> getEatDeals(Double latitude, Double longitude, Integer range) throws BaseException {
        try {
            List<GetEatDealRes> getEatDealRes = dao.getEatDeals(latitude, longitude, range);
            return getEatDealRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRestaurant(Integer restaurantId) throws BaseException {
        try{
            return dao.checkRestaurant(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMenu(Integer menuId) throws BaseException {
        try {
            return dao.checkMenu(menuId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetEatDealOrderRes> getEatDealOrders(Integer userId) throws BaseException {
        try {
            return dao.getEatDealOrders(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

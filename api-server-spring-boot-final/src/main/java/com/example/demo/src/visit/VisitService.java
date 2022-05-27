package com.example.demo.src.visit;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VisitService {
    private final VisitProvider provider;
    private final VisitDao dao;

    final Logger logger = LoggerFactory.getLogger(VisitService.class);

    public VisitService(VisitProvider provider, VisitDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    public int createVisit(int restaurantId, int userId) throws BaseException {
        if(provider.checkUser(userId)==0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkRestaurant(restaurantId) == 0) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            return dao.createVisit(restaurantId, userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int deleteVisit(Integer restaurantId, Integer userId, Integer visitId) throws BaseException {
        if(provider.checkUser(userId)==0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkRestaurant(restaurantId) == 0) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        if(provider.checkVisit(visitId) == 0) {
            throw new BaseException(VISITS_NOT_EXISTS_VISIT);
        }
        try {
            return dao.deleteVisit(visitId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

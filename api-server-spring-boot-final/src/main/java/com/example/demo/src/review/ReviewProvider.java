package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetReviewRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {
    private final ReviewDao dao;

    final Logger logger = LoggerFactory.getLogger(ReviewProvider.class);

    @Autowired
    public ReviewProvider(ReviewDao dao) {
        this.dao = dao;
    }

    /**
     * 식당 존재 여부 체크
     * @param restaurantId
     * @return
     * @throws BaseException
     */
    public GetReviewRes getRestaurantDetail(Integer restaurantId) throws BaseException {
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

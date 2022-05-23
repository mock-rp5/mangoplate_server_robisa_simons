package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.review.model.PostReviewReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewService {
    private final ReviewProvider provider;
    private final ReviewDao dao;

    final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    public ReviewService(ReviewProvider provider, ReviewDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    public int createReview(int restaurantId, int userId, PostReviewReq postReviewReq) throws BaseException {
        if(provider.checkRestaurantId(restaurantId) == 0 ){
            logger.warn("[ReviewService] restaurant not exists, restaurantId: {}", restaurantId);
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try{
            int result =  dao.createReview(restaurantId, userId, postReviewReq);

            if(result == 0) {
                logger.warn("[ReviewService] createReview fail, userId: {}, restaurantId: {}", userId, restaurantId);
                throw new BaseException(REVIEWS_CREATE_FAIL);
            }
            return result;
        }catch (Exception e) {
            logger.warn("[ReviewService] createReview database error");
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

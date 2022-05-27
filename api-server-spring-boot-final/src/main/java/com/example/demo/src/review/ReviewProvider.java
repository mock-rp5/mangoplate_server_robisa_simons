package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetReviewRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {
    private final ReviewDao dao;

    final Logger logger = LoggerFactory.getLogger(ReviewProvider.class);

    @Autowired
    public ReviewProvider(ReviewDao dao) {
        this.dao = dao;
    }


    public GetReviewRes getReviewDetail(int reviewId) throws BaseException {
        if(checkReviewId(reviewId) == 0) {
            throw new BaseException(REVIEWS_NOT_EXISTS_REVIEW);
        }
        try {
            GetReviewRes getReviewRes = dao.getReviewDetail(reviewId);
            return getReviewRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int checkReviewUserId(int reviewId, Integer userId) throws BaseException {
        try{
            return dao.checkReviewAndUserId(reviewId, userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewId(int reviewId) throws BaseException {
        try{
            return dao.checkReviewId(reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRestaurantId(int restaurantId) throws BaseException {
        try {
            return dao.checkRestaurantId(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(int userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReviewRes> getReviewByUser(Integer userId) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetReviewRes> getReviewRes = dao.getReviewByUser(userId);
            return getReviewRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

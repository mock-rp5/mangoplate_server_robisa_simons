package com.example.demo.src.like;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LikeProvider {
    private final LikeDao dao;

    final Logger logger = LoggerFactory.getLogger(LikeProvider.class);

    @Autowired
    public LikeProvider(LikeDao dao) {
        this.dao = dao;
    }

    public int checkCanceledLike(Integer userId, Integer reviewId) throws BaseException {
        try{
            return dao.checkCanceledLike(userId, reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }public int checkLiked(Integer userId, Integer reviewId) throws BaseException {
        try{
            return dao.checkLiked(userId, reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewId(Integer reviewId) throws BaseException {
        try {
            return dao.checkReviewId(reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(Integer userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

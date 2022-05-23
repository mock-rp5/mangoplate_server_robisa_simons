package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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

    @Transactional(rollbackFor = Exception.class)
    public int createReview(int restaurantId, int userId, Review review) throws BaseException {
        if(provider.checkRestaurantId(restaurantId) == 0 ){
            logger.warn("[ReviewService] restaurant not exists, restaurantId: {}", restaurantId);
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }

        try{
            int result =  dao.createReview(restaurantId, userId, review);

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

    public void uploadImages(MultipartFile file) {
        Date date = new Date();
        StringBuilder sb = new StringBuilder();

        sb.append(date.getTime());
        sb.append(file.getOriginalFilename());

        File dest = new File("C://Users//Yeon_J//Desktop//mango-plate-repository//mangoplate_server_robisa_simons//api-server-spring-boot-final//src//main//resources//imges/" + sb.toString());
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

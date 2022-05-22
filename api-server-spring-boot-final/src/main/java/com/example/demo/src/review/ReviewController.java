package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetReviewRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewProvider provider;
    private final ReviewService service;

    final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    public ReviewController(ReviewProvider provider, ReviewService service) {
        this.provider = provider;
        this.service = service;
    }

    /**
     * 식당 상세 정보 조회
     * @param restaurantId
     * @return
     */
    @ResponseBody
    @GetMapping("/{restaurant_id}")
    public BaseResponse<GetReviewRes> getRestaurantDetail(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null ) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }

        try{
            GetReviewRes getReviewRes = provider.getRestaurantDetail(restaurantId);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}

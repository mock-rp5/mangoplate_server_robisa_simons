package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;
import com.example.demo.src.review.model.GetReviewRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantProvider provider;
    private final RestaurantService service;

    final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    public RestaurantController(RestaurantProvider provider, RestaurantService service) {
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
    public BaseResponse<GetRestaurantDetailRes> getRestaurantDetail(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null ) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }

        try{
            // 식당 상세 보기 API 호출 시 해당 식당의 조회수는 1 증가됨
            int result = service.increaseView(restaurantId);

            if(result == 0) {
                return new BaseResponse<>(RESTAURANTS_VIEW_INCREASE_FAIL);
            }

            GetRestaurantDetailRes getReviewRes = provider.getRestaurantDetail(restaurantId);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}

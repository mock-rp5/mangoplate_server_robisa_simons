package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import com.example.demo.src.restaurant.model.GetRestaurantRes;

import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_EMPTY_RESTAURANT_ID;


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

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRestaurantRes>> getRestaurant(@RequestParam(value = "search-mode",required = false) String search_mode,
                                                              @RequestParam(value = "agree-use-location", defaultValue = "Y") String agreeUseLocation,
                                                              @RequestParam(value = "lat", required = false) Double latitude,
                                                              @RequestParam(value = "long",required = false) Double longitude,
                                                              @RequestParam(value = "food-category",defaultValue = "1,2,3,4,5,6,7,8") List<Integer> foodCategories,
                                                              @RequestParam(value = "range", defaultValue = "3") Integer range){
        logger.info("user lat -> ", latitude);
        logger.info("user long -> ", longitude);

        try{
            List<GetRestaurantRes> getRestaurantRes;

            // 사용자의 위치 정보 사용 동의 여부 체크
            if(agreeUseLocation.equals("Y")){
//                위도 경도 validation 필요, 값이 없거나, 값이 범위 안 값이 아닐 경우.
//                food-category 형식상의 validation 필요.

                if(latitude != null & longitude != null) {
                    getRestaurantRes = provider.getRestaurant(latitude, longitude,
                            foodCategories.toString().replace("[", "(").replace("]", ")"), range);
                }
                else {
                    // 사용자의 위도 경도 정보가 없을 경우, 에러 발생
                    return new BaseResponse<>(EMPTY_LOCATION_INFO);
                }
            }else {
                getRestaurantRes = provider.getRestaurant(latitude, longitude, foodCategories.toString().replace("[", "(").replace("]", ")"), range);
            }
            return new BaseResponse<>(getRestaurantRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
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
                logger.warn("view increase fail, restaurantId: {}", restaurantId);
                return new BaseResponse<>(RESTAURANTS_VIEW_INCREASE_FAIL);
            }

            GetRestaurantDetailRes getReviewRes = provider.getRestaurantDetail(restaurantId);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}

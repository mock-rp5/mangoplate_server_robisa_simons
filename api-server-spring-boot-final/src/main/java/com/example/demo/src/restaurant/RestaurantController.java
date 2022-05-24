package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import com.example.demo.src.restaurant.model.*;

import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_EMPTY_RESTAURANT_ID;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;


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
            List<GetRestaurantRes> getRestaurantRes ;

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

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostRestaurantRes> createRestaurant(@RequestBody PostRestaurantReq postRestaurantReq) {
        // JWT 인증 필요.
        // 인증이 성공했다면 isValidJWT = 1,
        int isValidJWT = 1;
        int userId = 1;

        // 작성자 ID가 넘어오지 않았을 경우, jwt 토큰을 통해서 userId에 작성자 ID를 넣어준다
        if (postRestaurantReq.getUserId() == null){
            // 토큰에서 추출한 값
            if(isValidJWT == 1) {
                postRestaurantReq.setUserId(userId);
            }
        }
        if(postRestaurantReq.getName() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_NAME);
        }
        if(postRestaurantReq.getAddress() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_ADDRESS);
        }
        if(postRestaurantReq.getLatitude() == null | postRestaurantReq.getLongitude() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_LOCATION_INFO);
        }
        try{
            PostRestaurantRes postRestaurantRes = service.createRestaurant(postRestaurantReq);
            return new BaseResponse<>(postRestaurantRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/{restaurant_id}")
    public BaseResponse<String> deleteRestaurant(@PathVariable("restaurant_id") Integer restaurantId) {
        // JWT 인증 필요.
        // 인증이 성공했다면 isValidJWT = 1,
        int isValidJWT = 1;
        int userId = 1;

        try{
            String result = service.deleteRestaurant(restaurantId);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PutMapping("/{restaurant_id}")
    public BaseResponse<String> updateRestaurant(@PathVariable("restaurant_id") Integer restaurantId, @RequestBody PutRestaurantReq putRestaurantReq) {
        // JWT 인증 필요.
        // 인증이 성공했다면 isValidJWT = 1,
        int isValidJWT = 1;
        int userId = 1;

        try{
            if (putRestaurantReq.getName() == null){
                return new BaseResponse<>(RESTAURANTS_EMPTY_NAME);
            }
            if (putRestaurantReq.getAddress() == null){
                return new BaseResponse<>(RESTAURANTS_EMPTY_ADDRESS);
            }
            String result = service.updateRestaurant(restaurantId,putRestaurantReq);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}

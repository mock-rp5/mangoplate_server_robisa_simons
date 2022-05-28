package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.wishes.model.GetWishRestaurantRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.src.wishes.model.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/wishes")
public class WishController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WishProvider provider;
    private final WishService service;

    @Autowired
    public WishController(WishProvider provider, WishService service){
        this.provider = provider;
        this.service = service;
    }
    /**
     * 유저의 가고싶다 항목에 포함된 식당 조회
     * @param userId
     * @return 식당 정보가 들어있는 객체
     */
    @ResponseBody
    @GetMapping("/{user_id}")
    public BaseResponse<List<GetWishRestaurantRes>> getWishRestaurants(@PathVariable("user_id") Integer targetUserId) {
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 비회원이면 로그인으로 넘겨야함
        // 일단 임시로...userId = 1
        // 인증이 성공했다면 isValidJWT = 1,
        if(targetUserId == null) return new BaseResponse<>(WISHES_EMPTY_TARGET_USER_ID);
        int isValidJWT = 1;
        int userId = 1;

        try{
            List<GetWishRestaurantRes> getWishRestaurantRes = provider.getWishRestaurants(userId, targetUserId);
            return new BaseResponse<>(getWishRestaurantRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 가고싶다 조회
     * @param restaurantId
     * @return 성공여부
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<Integer> getWish(@RequestParam(value = "restaurant_id") Integer restaurantId) {
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 비회원이면 로그인으로 넘겨야함
        // 일단 임시로...userId = 1
        // 인증이 성공했다면 isValidJWT = 1,

        int isValidJWT = 1;
        int userId = 1;

        try{
            int result = provider.getWish(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 가고싶다 등록
     * @param restaurantId
     * @return
     */
    @ResponseBody
    @PostMapping("/{restaurant_id}")
    public BaseResponse<Integer> postWish(@PathVariable("restaurant_id") Integer restaurantId) {
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 비회원이면 로그인으로 넘겨야함
        // 일단 임시로...userId = 1
        // 인증이 성공했다면 isValidJWT = 1,

        int isValidJWT = 1;
        int userId = 1;

        try{
            int result = service.postWish(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/{restaurant_id}")
    public BaseResponse<Integer> deleteWish(@PathVariable("restaurant_id") Integer restaurantId) {
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 비회원이면 로그인으로 넘겨야함
        // 일단 임시로...userId = 1
        // 인증이 성공했다면 isValidJWT = 1,

        int isValidJWT = 1;
        int userId = 1;

        try{
            int result = service.deleteWish(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Integer> putMemo(@RequestBody PostMemoReq memo) {
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 비회원이면 로그인으로 넘겨야함
        // 일단 임시로...userId = 1
        // 인증이 성공했다면 isValidJWT = 1,

        if(memo.getWishId() == null) return new BaseResponse<>(WISHES_EMPTY_WISH_ID);
        if(memo.getMemo() == null) return new BaseResponse<>(WISHES_EMPTY_MEMO_CONTENT);
        System.out.println(memo);
        int isValidJWT = 1;
        int userId = 2;

        try{
            int result = service.putMemo(memo.getWishId(), memo.getMemo(), userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}


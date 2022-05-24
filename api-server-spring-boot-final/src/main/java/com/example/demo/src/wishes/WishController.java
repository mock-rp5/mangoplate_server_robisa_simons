package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 가고싶다 조회
     * @param restaurantId
     * @return
     */
//    @ResponseBody
//    @GetMapping("/{restaurant_id}")
//    public BaseResponse<Integer> getWish(@PathVariable("restaurant_id") Integer restaurantId) {
//        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
//        // 비회원이면 로그인으로 넘겨야함
//        // 일단 임시로...userId = 1
//        // 인증이 성공했다면 isValidJWT = 1,
//
//        int isValidJWT = 1;
//        int userId = 1;
//
//        try{
//            int result = service.getWish(restaurantId, userId);
//            return new BaseResponse<>(result);
//        } catch (BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
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
    @DeleteMapping("/{wish_id}")
    public BaseResponse<Integer> deleteWish(@PathVariable("wish_id") Integer wishId) {
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 비회원이면 로그인으로 넘겨야함
        // 일단 임시로...userId = 1
        // 인증이 성공했다면 isValidJWT = 1,

        int isValidJWT = 1;
        int userId = 1;

        try{
            int result = service.deleteWish(wishId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 리뷰 상세 보기
     * @param reviewId
     * @return
     */
//    @GetMapping("/{review_id}")
//    @ResponseBody
//    public BaseResponse<GetReviewRes> getReviewDetail(@PathVariable("review_id") Integer reviewId) {
//        if(reviewId == null) {
//            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
//        }
//        try {
//            GetReviewRes getReviewRes = provider.getReviewDetail(reviewId);
//            return new BaseResponse<>(getReviewRes);
//        }catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//    }

}


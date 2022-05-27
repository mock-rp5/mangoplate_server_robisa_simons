package com.example.demo.src.visit;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.visit.model.GetVisitByUserRes;
import com.example.demo.src.visit.model.GetVisitRes;
import com.example.demo.src.visit.model.PostVisitReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitProvider provider;
    private final VisitService service;

    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(VisitController.class);


    @Autowired
    public VisitController(VisitProvider provider, VisitService service, JwtService jwtService) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping("/{restaurant_id}")
    @ResponseBody
    public BaseResponse<GetVisitRes> getVisit(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetVisitRes getVisitRes = provider.getVisit(restaurantId, userIdxByJwt);
            return new BaseResponse<>(getVisitRes);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping
    @ResponseBody
    public BaseResponse<GetVisitByUserRes> getVisitByUser() {
        try {
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetVisitByUserRes getVisitByUserRes = provider.getVisitByUser(userIdxByJwt);
            return new BaseResponse<>(getVisitByUserRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @DeleteMapping("/{restaurant_id}/{visit_id}")
    @ResponseBody
    public BaseResponse<Integer> deleteVisit(@PathVariable("restaurant_id") Integer restaurantId,
                                             @PathVariable("visit_id") Integer visitId) {
        if(restaurantId == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        if(visitId == null) {
            return new BaseResponse<>(VISITS_EMPTY_VISIT_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            int result = service.deleteVisit(restaurantId, userIdxByJwt, visitId);
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("")
    @ResponseBody
    public BaseResponse<Integer> createVisit(@RequestBody PostVisitReq postVisitReq) {
        if(postVisitReq.getRestaurantId() == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            int visitId = service.createVisit(postVisitReq.getRestaurantId(), userIdxByJwt);
            return new BaseResponse<>(visitId);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}

package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mylist.model.*;
import org.hibernate.sql.Insert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/mylists")
public class MyListController {
    private final MyListService service;
    private final MyListProvider provider;

    final Logger logger = LoggerFactory.getLogger(MyListController.class);

    @Autowired
    public MyListController(MyListService service, MyListProvider provider) {
        this.service = service;
        this.provider = provider;
    }

//    [DONE]특정 유저의 마이리스트 조회
    @GetMapping("/{user_id}")
    @ResponseBody
    public BaseResponse<List<GetMyListRes>> getMyList(@PathVariable(value = "user_id", required = false) Integer userId) {
        if(userId.equals(null)) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try {
            List<GetMyListRes> getMyListRes = provider.getMyList(userId);
            return new BaseResponse<>(getMyListRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
    // [DONE]특정 유저의 특정 마이리스트 조회
//    특정 유저의 마이리스트 번호가 아니면 밸리데이션
    @GetMapping("/{user_id}/{mylist_id}")
    @ResponseBody
    public BaseResponse<GetMyListDetailRes> getMyListDetail(@PathVariable(value = "user_id", required = false) Integer userId,
                                                            @PathVariable(value = "mylist_id", required = false) Integer myListId) {
        if(userId.equals(null)) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(myListId.equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
        try {
            GetMyListDetailRes getMyListDetailRes = provider.getMyListDetail(userId,myListId);
            return new BaseResponse<>(getMyListDetailRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

//    새로운 마이리스트 등록, 다른 사람의 마이리스트에는 접근 X, 식당을 바로 마이리스트에 추가할 수 있음.
    @PostMapping("")
    @ResponseBody
    public BaseResponse<PostMyListRes> createMyList(@RequestParam(value = "restaurant-id",required = false) Optional<List<Integer>> restaurantId,
                                                    @RequestBody PostMyListReq postMyListReq) {
        // jwt 밸리데이션 필요.
        Integer userId = 1;
        PostMyListRes postMyListRes;
        if(userId.equals(null)) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try {
//            postMyListReq.setUserId(userId);
            if(restaurantId.isEmpty())
                postMyListRes = service.createMyList(postMyListReq, userId);
            else
                postMyListRes = service.insert2MyList(restaurantId.get(),service.createMyList(postMyListReq, userId).getMyListId());
            return new BaseResponse<>(postMyListRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    기존 마이리스트에 식당 추가
    @PostMapping("/{mylist_id}")
    @ResponseBody
    public BaseResponse<PostMyListRes> insert2MyList(@PathVariable(value = "mylist_id", required = false) Integer myListId,
                                               @RequestParam(value = "restaurant-id",required = false) List<Integer> restaurantId) {
        // 임시 유저 아이디
        int userId = 3;

        if(myListId.equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
        if(restaurantId.equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_RESTAURANT_ID);
        }
        try {
            PostMyListRes postMyListRes = service.insert2MyList(restaurantId, myListId);
            return new BaseResponse<>(postMyListRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

//    마이리스트 삭제
    @DeleteMapping("")
    @ResponseBody
    public BaseResponse<Integer> deleteMyList(@RequestBody DeleteMyListReq deleteMyListReq) {
        // 임시 유저 아이디
//        유저 아이디 밸리데이션 필요
        int userId = 3;

        if(Optional.ofNullable(deleteMyListReq.getMyListId()).equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
//        if(postCommentReq.getComment()== null) {
//            return new BaseResponse<>(COMMENTS_EMPTY_COMMENT);
//        }
        try {
            return new BaseResponse<>(service.deleteMyList(deleteMyListReq));
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

//    마이리스트 수정
    @PutMapping("")
    @ResponseBody
    public BaseResponse<Integer> updateMyList(@RequestBody PutMyListReq putMyListReq) {
        // 임시 유저 아이디
        int userId = 3;

        if(Optional.ofNullable(putMyListReq.getMyListId()).equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
//        if(postCommentReq.getComment()== null) {
//            return new BaseResponse<>(COMMENTS_EMPTY_COMMENT);
//        }
        try {
            return new BaseResponse<>(service.updateMyList(putMyListReq));
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}

package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.bookmark.model.GetBookmarkCountRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import com.example.demo.utils.ValidationRegex;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    private final BookmarkService service;
    private final BookmarkProvider provider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(BookmarkController.class);

    @Autowired
    public BookmarkController(BookmarkService service, BookmarkProvider provider, JwtService jwtService) {
        this.service = service;
        this.provider = provider;
        this.jwtService = jwtService;
    }
    @GetMapping("/{user_id}")
    @ResponseBody
    public BaseResponse<GetBookmarkCountRes> getBookmarkCount(@PathVariable(value = "user_id") Integer userId) throws BaseException {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try {
//            Integer userId = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            GetBookmarkCountRes getBookmarkCountRes = provider.getBookmarkCount(userId);
            return new BaseResponse<>(getBookmarkCountRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
//    @GetMapping("/{user_id}/{contents_type}")
//    @ResponseBody
//    public BaseResponse<GetBookmarkCountRes> getBookmarkedContents(@PathVariable(value = "user_id") Integer userId,
//                                                                   @PathVariable(value = "contents_type") Integer contentsType) throws BaseException {
//        if(userId == null) {
//            return new BaseResponse<>(USERS_EMPTY_USER_ID);
//        }
//        if(contentsType != "top_lists" && contentsType != "mylists" && contentsType != "mango_pick_stories"){
//            return new BaseResponse<>(BOOKMARKS_CONTENT_TYPE_INVALID_FORM);
//        }
//        try {
////            Integer userId = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            GetBookmarkCountRes getBookmarkCountRes = provider.getBookmarkedContents(userId, contentsType);
//            return new BaseResponse<>(getBookmarkCountRes);
//        }catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//    }
    @PostMapping("")
    @ResponseBody
    public BaseResponse<Integer> postBookmark(@RequestParam(value = "contents-type") String contentsType,
                                              @RequestParam(value = "contents-id") Integer contentsId) throws BaseException {
        Integer userId = 1;
        if(contentsType == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_TYPE);
        }
        if(contentsId == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_ID);
        }
        if(contentsType != "top_lists" && contentsType != "mylists" && contentsType != "mango_pick_stories"){
            return new BaseResponse<>(BOOKMARKS_CONTENT_TYPE_INVALID_FORM);
        }
        try {
//            Integer userId = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            Integer result = service.postBookmark(userId, contentsType, contentsId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @DeleteMapping("")
    @ResponseBody
    public BaseResponse<Integer> cancelBookmark(@RequestParam(value = "contents-type") String contentsType,
                                            @RequestParam(value = "contents-id") Integer contentsId) throws BaseException {
        Integer userId = 1;
        if(contentsType == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_TYPE);
        }
        if(contentsId == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_ID);
        }
        if(contentsType != "top_lists" && contentsType != "mylists" && contentsType != "mango_pick_stories"){
            return new BaseResponse<>(BOOKMARKS_CONTENT_TYPE_INVALID_FORM);
        }
        try {
//            Integer userId = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            Integer result = service.cancelBookmark(userId, contentsType, contentsId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}

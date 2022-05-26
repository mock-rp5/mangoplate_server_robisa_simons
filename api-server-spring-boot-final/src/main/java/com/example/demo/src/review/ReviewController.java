package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;

import com.example.demo.src.review.upload.FileStore;
import com.example.demo.src.review.upload.UploadFile;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewProvider provider;
    private final ReviewService service;
    private final JwtService jwtService;

    private final FileStore fileStore;

    final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    public ReviewController(ReviewProvider provider, ReviewService service, JwtService jwtService, FileStore fileStore) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
        this.fileStore = fileStore;
    }

    /**
     * 리뷰 작성
     * @param restaurantId
     * @param postReviewReq
     * @return
     */
    @PostMapping(value = "/{restaurant_id}", consumes = "multipart/form-data" )
    @ResponseBody
    public BaseResponse<PostReviewRes> createReview(@PathVariable("restaurant_id") Integer restaurantId,
                                                    @ModelAttribute PostReviewReq postReviewReq) throws IOException {
        logger.info("[ReviewController] createReview, restaurantId: {}, postReviewReq: {}", restaurantId, postReviewReq.toString());

        if(restaurantId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_RESTAURANT_ID);
        }
        if(postReviewReq.getScore() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_SOCRE);
        }
        if(postReviewReq.getContent() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_CONTENT);
        }

        List<UploadFile> storeImageFiles=null;


        Review review = new Review(postReviewReq.getContent(), postReviewReq.getScore(), storeImageFiles);

        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(postReviewReq.getFile()!=null) {
                storeImageFiles = fileStore.storeFiles(postReviewReq.getFile());
            }

            PostReviewRes postReviewRes = new PostReviewRes(service.createReview(restaurantId, userId, review));
            logger.info("[ReviewController] createReview, userId: {}, reviewId: {}", userId, postReviewRes.getId());
            return new BaseResponse<>(postReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 리뷰 수정
     * @param reviewId
     * @param putReviewReq
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/{review_id}", consumes = "multipart/form-data" )
    @ResponseBody
    public BaseResponse<PutReviewRes> updateReview(@PathVariable("review_id") Integer reviewId,
                                                   @ModelAttribute PutReviewReq putReviewReq) throws BaseException, IOException {

        if(reviewId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        if(putReviewReq.getScore() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_SOCRE);
        }
        if(putReviewReq.getContent() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_CONTENT);
        }

        List<UploadFile> storeImageFiles = null;

        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            if(putReviewReq.getFile()!=null) {
                storeImageFiles = fileStore.storeFiles(putReviewReq.getFile());
            }

            Review review = new Review(putReviewReq.getContent(), putReviewReq.getScore(), storeImageFiles);
            PutReviewRes putReviewRes = service.updateReview(reviewId, userId, review);
            return new BaseResponse<>(putReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 리뷰 상세 보기
     * @param reviewId
     * @return
     */
    @GetMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<GetReviewRes> getReviewDetail(@PathVariable("review_id") Integer reviewId) {
        if(reviewId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        try {
            GetReviewRes getReviewRes = provider.getReviewDetail(reviewId);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @DeleteMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<DeleteReviewRes> deleteReview(@PathVariable("review_id") Integer reviewId) throws BaseException {
        Integer userId = jwtService.getUserIdx();
        //userIdx와 접근한 유저가 같은지 확인

        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        if(reviewId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        try {
            DeleteReviewRes deleteReviewRes = service.deleteReview(reviewId, userId);
            return new BaseResponse<>(deleteReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



}

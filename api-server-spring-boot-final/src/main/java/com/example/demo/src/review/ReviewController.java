package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetReviewRes;

import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.PostReviewRes;
import com.example.demo.src.review.model.Review;
import com.example.demo.src.review.upload.FileStore;
import com.example.demo.src.review.upload.UploadFile;
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

    private final FileStore fileStore;

    final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    public ReviewController(ReviewProvider provider, ReviewService service, FileStore fileStore) {
        this.provider = provider;
        this.service = service;
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
        // 로그인 기능 추가하면 토큰으로 유저 체크 추가해야함
        // 일단 임시로...userId = 2
        int userId = 2;

        if(restaurantId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_RESTAURANT_ID);
        }
        if(postReviewReq.getScore() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_SOCRE);
        }
        if(postReviewReq.getContent() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_CONTENT);
        }

        Review review = new Review();
        review.setContent(postReviewReq.getContent());
        review.setScore(postReviewReq.getScore());

        if(postReviewReq.getFile()!=null) {
            List<UploadFile> storeImageFiles = fileStore.storeFiles(postReviewReq.getFile());
            review.setFile(storeImageFiles);
        }

        try{
            return new BaseResponse<>(new PostReviewRes(service.createReview(restaurantId, userId, review)));
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


}

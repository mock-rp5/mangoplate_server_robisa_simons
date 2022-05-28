package com.example.demo.src.review.model;

import com.example.demo.src.comment.model.GetCommentRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewTodayRes {
    private Integer reviewId;
    private Integer userId;
    private String userName;
    private String profileImgUrl;
    private String content;
    private Integer score;
    private Integer restaurantId;
    private String restaurantName;
    private List<String> imgUrls;
    private List<GetCommentRes> comments;
    private Integer reviewCnt;
    private Integer followCnt;
    private Boolean isHolic;
    private String updatedAt;
    private Boolean wish;
    private Boolean like;

    public GetReviewTodayRes(Integer id, int userId, String userName, String content, int score, String profileImgUrl, int restaurantId, String restaurantName, boolean isHolic, String updatedAt, Boolean wish) {
        this.reviewId = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.score = score;
        this.profileImgUrl = profileImgUrl;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.isHolic = isHolic;
        this.updatedAt = updatedAt;
        this.wish = wish;
    }

}
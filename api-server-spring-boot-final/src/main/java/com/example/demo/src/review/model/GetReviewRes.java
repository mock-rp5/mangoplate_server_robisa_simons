package com.example.demo.src.review.model;

import com.example.demo.src.comment.model.GetCommentRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewRes {
    private Integer id;
    private int userId;
    private String userName;
    private String profileImgUrl;
    private String content;
    private int score;
    private List<String> imgUrls;
    private List<GetCommentRes> comments;

    public GetReviewRes(Integer id, int userId, String userName, String content, int score, String profileImgUrl) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.score = score;
        this.profileImgUrl = profileImgUrl;
    }
}

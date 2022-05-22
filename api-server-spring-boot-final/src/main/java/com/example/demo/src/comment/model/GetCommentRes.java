package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private Integer id;
    private int userId;
    private String userName;
    private String content;
    private int order;
    private List<GetSubComment> subComments;

    public GetCommentRes(Integer id, int userId, String userName, String content, int order) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.order = order;
    }
}

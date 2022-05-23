package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSubComment {
    private Integer id;
    private int userId;
    private String userName;
    private String content;
    private String parentCommentUserName;
    private int order;

    public GetSubComment(Integer id, int userId, String userName, String content, int order) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.order = order;
    }
}

package com.example.demo.src.review;

import com.example.demo.src.comment.model.Comment;
import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.review.model.GetReviewRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {
    @Autowired private JdbcTemplate jdbcTemplate;


}

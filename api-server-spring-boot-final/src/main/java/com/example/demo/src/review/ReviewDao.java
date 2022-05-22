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


    public int checkReviewId(int reviewId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ?)";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId);
    }

    public GetReviewRes getReviewDetail(int reviewId) {
        String getReviewDetailQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.id = ?";

        GetReviewRes getReviewRes = jdbcTemplate.queryForObject(getReviewDetailQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8)
                ), reviewId );

        getReviewRes.setImgUrls(getReviewImgURLs(reviewId));
        getReviewRes.setComments(getComments(reviewId));

        return getReviewRes;

    }

    public List<String> getReviewImgURLs(int reviewId) {
        String getReviewImgQuery = "select img_url from images_review where review_id = ?";
        List<String> imgUrls = new ArrayList<>();

        jdbcTemplate.query(getReviewImgQuery,
                (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), reviewId);
        return imgUrls;
    }


    public List<GetCommentRes> getComments(int reviewId) {
        String getComments = "select C.id, C.user_id, U.user_name, C.comment, `order` " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id and C.review_id = ? " +
                "where level = 1 " +
                "order by `order` asc";

        List<GetCommentRes> getCommentRes = jdbcTemplate.query(getComments,
                (rs, rowNum) -> new GetCommentRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5)
                ), reviewId);

        for(GetCommentRes comment : getCommentRes) {
            List<GetSubComment> subComments = getSubComments(comment.getId());
            comment.setSubComments(subComments);
        }
        return getCommentRes;
    }

    private List<GetSubComment> getSubComments(int groupNum) {
        String getSubComments = "select C.id, C.user_id, U.user_name, C.comment, `order` " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id " +
                "where level > 1 and group_num = ? " +
                "order by `order` asc, level asc ";

        return jdbcTemplate.query(getSubComments,
                (rs, rowNum) -> new GetSubComment(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5)
                ), groupNum);
    }
}

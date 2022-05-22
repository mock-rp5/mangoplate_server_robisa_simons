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

    public int checkRestaurantId(Integer restaurantId) {
        String checkRestaurantQuery = "select exists " +
                "(select * from restaurants where id = ?)";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public GetReviewRes getRestaurantDetail(Integer restaurantId) {
        String getRestaurantQuery = "";

        return new GetReviewRes();
    }

    public List<GetReviewRes> getReviews(int restaurantId) {
        String getReviewsQuery = "select R.id, R.user_id, U.user_name, R.content " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id and R.id = ?";

        List<GetReviewRes> getReviewRes = jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5)
                ), restaurantId);

        for(GetReviewRes review : getReviewRes) {
            List<GetCommentRes> commentRes = getComments(review.getId());
            List<String> imgUrls = getReviewImgURLs(review.getId());

            review.setComments(commentRes);
            review.setImgUrl(imgUrls);
        }

        return getReviewRes;

    }

    public List<String> getReviewImgURLs(int reviewId) {
        String getReviewImgQuery = "select img_url from images_restaurant";
        List<String> imgUrls = new ArrayList<>();

        jdbcTemplate.query(getReviewImgQuery,
                (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), reviewId);
        return imgUrls;
    }

    public List<GetCommentRes> getComments(int reviewId) {
        String getComments = "select C.id, C.user_id, U.user_name, C.comment, `order` " +
                "fromm review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id and C.review_id = 6 " +
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
                "fromm review_comments as C " +
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

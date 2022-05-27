package com.example.demo.src.review;

import com.example.demo.src.comment.model.Comment;
import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.Review;
import com.example.demo.src.review.upload.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {
    @Autowired private JdbcTemplate jdbcTemplate;


    public int checkReviewAndUserId(int reviewId, Integer userId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId, userId);
    }

    public int checkReviewId(int reviewId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and status = 'ACTIVE')";
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
            for (GetSubComment getSubComment : subComments) {
                getSubComment.setParentCommentUserName(getParentCommentUserName(getSubComment.getId()));
            }
            comment.setSubComments(subComments);
        }
        return getCommentRes;
    }

    public String getParentCommentUserName(int commentId) {
        String getParentCommentUserQuery = "select user_name " +
                "from users where id = (select parent_user_id from review_comments where id = ?)";

        return jdbcTemplate.queryForObject(getParentCommentUserQuery, String.class, commentId);
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

    public int checkRestaurantId(int restaurantId) {
        String checkReviewQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, restaurantId);
    }

    public int createReview(int restaurantId, int userId, Review review) {
        String createReviewQuery = "insert into reviews(content, score, status, user_id, restaurant_id) " +
                "values(?, ?, 'ACTIVE', ?, ?) ";
        Object[] queryParams = new Object[]{review.getContent(), review.getScore(), userId, restaurantId};
        jdbcTemplate.update(createReviewQuery, queryParams);

        String lastInserIdQuery = "select id from reviews order by id desc limit 1";
        int reviewId =  this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

        if(review.getFile()!=null) {
            storeReviewImg(reviewId, review.getFile());
        }
        return reviewId;
    }

    private void storeReviewImg(int reviewId, List<UploadFile> files) {
        String storeReviewImgQuery = "insert into images_review(review_id, img_url, status) " +
                "values(?, ?, 'ACTIVE')";
        for(UploadFile uploadFile : files) {
            jdbcTemplate.update(storeReviewImgQuery, reviewId, uploadFile.getStoreFileUrl());
        }
    }

    public int updateReview(Integer reviewId, Review review) {
        String updateReviewQuery = "update reviews set content = ?, score = ? where id = ?";
        Object[] updateQueryParams = new Object[]{review.getContent(), review.getScore(), reviewId};

        int result = jdbcTemplate.update(updateReviewQuery, updateQueryParams);

        if(review.getFile()!= null) {
            storeReviewImg(reviewId, review.getFile());
        }

        return result;
    }

    public int deleteReview(Integer reviewId) {
        String deleteReviewQuery = "update reviews set status = 'INACTIVE' where id = ?";
        int result = jdbcTemplate.update(deleteReviewQuery, reviewId);

        deleteReviewImg(reviewId);

        return result;
    }

    private void deleteReviewImg(Integer reviewId) {
        String deleteReviewImgQuery = "update images_review set status = 'INACTIVE' where review_id = ?";
        jdbcTemplate.update(deleteReviewImgQuery, reviewId);
    }

    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ?)";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }

    public List<GetReviewRes> getReviewByUser(Integer userId) {
        String getReviewByUserQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.user_id = ?";

        List<GetReviewRes> getReviewRes = jdbcTemplate.query(getReviewByUserQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8)
                ), userId);

        for(GetReviewRes review: getReviewRes) {
            review.setImgUrls(getReviewImgURLs(review.getId()));
            review.setComments(getComments(review.getId()));
        }
        return getReviewRes;
    }
}

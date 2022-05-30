package com.example.demo.src.review;

import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.review.model.*;
import com.example.demo.src.review.upload.UploadFile;
import com.example.demo.src.visit.model.GetUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {
    @Autowired private JdbcTemplate jdbcTemplate;


    public int checkReviewAndUserId(int reviewId, Integer userId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId, userId);
    }

    public int checkReviewId(int reviewId,  int userId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId, userId);
    }

    public GetReviewRes getReviewDetail(int reviewId) {
        String getReviewDetailQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name,  U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.id = ? and R.status = 'ACTIVE'";

        GetReviewRes getReviewRes = jdbcTemplate.queryForObject(getReviewDetailQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), reviewId );

        getReviewRes.setImgUrls(getReviewImgURLs(reviewId));
        getReviewRes.setComments(getComments(reviewId));
        getReviewRes.setReviewCnt(getReviewCnt(getReviewRes.getUserId()));
        getReviewRes.setFollowCnt(getFollowCnt(getReviewRes.getUserId()));

        return getReviewRes;

    }

    private int getFollowCnt(int userId) {
        String getFollowCntQuery = "select count(*) from follows where user_id = ? and status = 'ACTIVE' ";
        return jdbcTemplate.queryForObject(getFollowCntQuery, int.class, userId);
    }

    private int getReviewCnt(int userId) {
        String getReviewCntQuery = "select count(*) from reviews where user_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getReviewCntQuery, int.class, userId);
    }

    public List<String> getReviewImgURLs(int reviewId) {
        String getReviewImgQuery = "select img_url from images_review where review_id = ? and status = 'ACTIVE'";
        //List<String> imgUrls = new ArrayList<>();
        try {
             //jdbcTemplate.query(getReviewImgQuery,
                   // (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), reviewId);
             List<String> imgUrls =  jdbcTemplate.query(getReviewImgQuery,
                     (rs, rowNum) -> rs.getString("img_url"), reviewId);
//
//             if(imgUrls.isEmpty()) {
//                 return null;
//             }
             return imgUrls;
        }catch (EmptyResultDataAccessException e) {
            return null;
        }

    }


    public List<GetCommentRes> getComments(int reviewId) {
        String getComments = "select C.id, C.user_id, U.user_name, C.comment, `order`, U.is_holic, date_format(C.updated_at, '%Y-%m-%d'), U.profile_img_url " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id and C.review_id = ? " +
                "where level = 1 and C.status = 'ACTIVE' " +
                "order by `order` asc";

        List<GetCommentRes> getCommentRes = jdbcTemplate.query(getComments,
                (rs, rowNum) -> new GetCommentRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getBoolean(6),
                        rs.getString(7),
                        rs.getString(8)
                ), reviewId);

        for(GetCommentRes comment : getCommentRes) {
            List<GetSubComment> subComments = getSubComments(reviewId);
            for (GetSubComment getSubComment : subComments) {
                getSubComment.setParentCommentUserName(getParentCommentUserName(getSubComment.getId()));
            }
            comment.setSubComments(subComments);
        }
        return getCommentRes;
    }

    public String getParentCommentUserName(int commentId) {
        String getParentCommentUserQuery = "select user_name " +
                "from users where id = (select parent_user_id from review_comments where id = ? and status = 'ACTIVE')";

        return jdbcTemplate.queryForObject(getParentCommentUserQuery, String.class, commentId);
    }

    private List<GetSubComment> getSubComments(int groupNum) {
        String getSubComments = "select C.id, C.user_id, U.user_name, C.comment, `order`, U.profile_img_url, U.is_holic, C.updated_at " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id " +
                "where level > 1 and group_num = ? and C.status ='ACTIVE' " +
                "order by `order` asc, level asc ";

        return jdbcTemplate.query(getSubComments,
                (rs, rowNum) -> new GetSubComment(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getBoolean(7),
                        rs.getString(8)
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

    public int updateReview(Integer reviewId, Review review, int userId) {
        String updateReviewQuery = "update reviews set content = ?, score = ? where id = ? and user_id = ?";
        Object[] updateQueryParams = new Object[]{review.getContent(), review.getScore(), reviewId, userId};

        int result = jdbcTemplate.update(updateReviewQuery, updateQueryParams);

        if(review.getFile()!= null) {
            storeReviewImg(reviewId, review.getFile());
        }else {
            deleteReviewImg(reviewId);
        }

        return result;
    }

    public int deleteReview(Integer reviewId) {
        String deleteReviewQuery = "update reviews set status = 'INACTIVE' where id = ? ";
        int result = jdbcTemplate.update(deleteReviewQuery, reviewId);

        deleteReviewImg(reviewId);

        return result;
    }

    private void deleteReviewImg(Integer reviewId) {
        String deleteReviewImgQuery = "update images_review set status = 'INACTIVE' where review_id = ?";
        jdbcTemplate.update(deleteReviewImgQuery, reviewId);
    }

    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }

    public List<GetReviewRes> getReviewByUser(Integer userId) {
        String getReviewByUserQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.user_id = ? and R.status = 'ACTIVE'";

        List<GetReviewRes> getReviewRes = jdbcTemplate.query(getReviewByUserQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), userId);

        for(GetReviewRes review: getReviewRes) {
            review.setImgUrls(getReviewImgURLs(review.getId()));
            review.setComments(getComments(review.getId()));
            review.setFollowCnt(getFollowCnt(userId));
            review.setReviewCnt(getReviewCnt(userId));
        }
        return getReviewRes;
    }

    public GetReviewImageRes getReviewImages(Integer userId) {
        GetUserInfo getUserInfo = getUserInfo(userId);

        String getReviewImageQuery = "select R.id , RT.name, IR.id, IR.img_url " +
                "from images_review as IR " +
                "join reviews as R " +
                "on IR.review_id = R.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.user_id = ? and IR.status = 'ACTIVE'";

        List<GetReviewImage> getReviewImages = jdbcTemplate.query(getReviewImageQuery,
                (rs, rowNum) -> new GetReviewImage(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getString(4)
                ), userId);

        return new GetReviewImageRes(getUserInfo, getReviewImages);

    }

    private GetUserInfo getUserInfo(Integer userIdxByJwt) {
        String getUserInfoQuery = "select id, user_name, profile_img_url, " +
                "(select count(*) from follows where user_id = ?) as follow, " +
                "(select count(*) from reviews where user_id = ?) as review " +
                " from users " +
                " where id = ? and status = 'ACTIVE'";

        return jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new GetUserInfo(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5)
                ), userIdxByJwt, userIdxByJwt, userIdxByJwt);
    }

    public int checkReviewImg(Integer imgId, Integer userId) {
        String checkReviewImgQuery = "select exists ( " +
                "select I.*, R.* " +
                "from images_review as I " +
                "join reviews as R " +
                "on I.review_id = R.id " +
                "where I.id = ? and I.status = 'ACTIVE' and R.user_id = ?)";

        return jdbcTemplate.queryForObject(checkReviewImgQuery, int.class, imgId, userId);
    }

    public int deleteReviewImgByUser(Integer imgId) {
        String deleteReviewImgQuery = "update images_review set status = 'INACTIVE' where id = ? ";
        return jdbcTemplate.update(deleteReviewImgQuery, imgId);
    }

    public GetNewsRes getReviewToday(Integer userId) {
        String reviewTodayQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, \n" +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, R.updated_at \n" +
                "from reviews as R\n" +
                "left join users as U\n" +
                "on R.user_id = U.id \n" +
                "left join restaurants as RT \n" +
                "on R.restaurant_id = RT.id\n" +
                "left join (select max(A.count + B.count), A.user_id as user_id\n" +
                "from (select count(*) as count, user_id from reviews group by user_id) as A,\n" +
                "(select count(*) as count, user_id from follows group by user_id) as B\n" +
                "where A.user_id = B.user_id) as AB\n" +
                "on AB.user_id = R.user_id\n" +
                "where R.status = 'ACTIVE' and R.updated_at like ? order by R.updated_at DESC " +
                "limit 1";

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = now.format(formatter)+"%";
       //String currentDate = "2022-05-23";

        try {
            GetNewsRes getReviewTodayRes = jdbcTemplate.queryForObject(reviewTodayQuery,
                    (rs, rowNum) -> new GetNewsRes(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5),
                            rs.getString(6),
                            rs.getInt(7),
                            rs.getString(8),
                            rs.getBoolean(9),
                            rs.getString(10)
                    ), currentDate);

            if(userId != 0) {
                getReviewTodayRes.setWish(getWish(userId, getReviewTodayRes.getRestaurantId()));
                getReviewTodayRes.setLike(getLike(userId, getReviewTodayRes.getReviewId()));
            }
            getReviewTodayRes.setFollowCnt(getFollowCnt(userId));
            getReviewTodayRes.setReviewCnt(getReviewCnt(userId));
            getReviewTodayRes.setImgUrls(getReviewImgURLs(getReviewTodayRes.getReviewId()));
            getReviewTodayRes.setComments(getComments(getReviewTodayRes.getReviewId()));

            return getReviewTodayRes;
        }catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    private Boolean getLike(Integer userId, Integer reviewId) {
        String getLikeQuery = "select exists (select * from likes where user_id = ? and review_id = ? )";
        return jdbcTemplate.queryForObject(getLikeQuery, Boolean.class, userId, reviewId);
    }

    private Boolean getWish(Integer userId, Integer restaurantId) {
        String getWishQuery = "select exists (select * from wishes where user_id = ? and restaurant_id = ?)";
        return jdbcTemplate.queryForObject(getWishQuery, Boolean.class, userId, restaurantId);
    }

    public List<GetNewsRes> getNews(Integer userId, List<Integer> scores) {
        String getNewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id  " +
                "join restaurants as RT  " +
                "on R.restaurant_id = RT.id " +
                "where R.status = 'ACTIVE' and R.score IN(";

        Object[] params = new Object[scores.size()];
        for(int i=0; i<scores.size(); i++) {
            params[i] = scores.get(i);
            getNewsQuery+="?,";
        }

        getNewsQuery = getNewsQuery.substring(0, getNewsQuery.length()-1);
        getNewsQuery+=") order by R.updated_at desc";

        List<GetNewsRes> getNewsRes = jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                rs.getInt(1),
                rs.getInt(2),
                rs.getString(3),
                rs.getString(4),
                rs.getInt(5),
                rs.getString(6),
                rs.getInt(7),
                rs.getString(8),
                rs.getBoolean(9),
                rs.getString(10)
        ), params);

        for(GetNewsRes news : getNewsRes) {
            if (userId != 0) {
                news.setWish(getWish(userId, news.getRestaurantId()));
                news.setLike(getLike(userId, news.getReviewId()));
            }
            news.setFollowCnt(getFollowCnt(userId));
            news.setReviewCnt(getReviewCnt(userId));
            news.setImgUrls(getReviewImgURLs(news.getReviewId()));
            news.setComments(getComments(news.getReviewId()));
        }
        return getNewsRes;
    }

    public List<GetNewsRes> getHolicNews(Integer userId, List<Integer> scores) {
        String getNewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id  " +
                "join restaurants as RT  " +
                "on R.restaurant_id = RT.id " +
                "join (select * from follows where  follower_id= ?) as F " +
                "on R.user_id = F.user_id " +
                "where R.status = 'ACTIVE' and U.is_holic = 1 and R.score IN(";

        Object[] params = new Object[scores.size()+1];
        params[0] = userId;
        for(int i=0; i<scores.size(); i++) {
            params[i+1] = scores.get(i);
            getNewsQuery+="?,";
        }

        getNewsQuery = getNewsQuery.substring(0, getNewsQuery.length()-1);
        getNewsQuery+=") order by R.updated_at desc ";

        List<GetNewsRes> getNewsRes = jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), params);

        for(GetNewsRes news : getNewsRes) {
            news.setWish(getWish(userId, news.getRestaurantId()));
            news.setLike(getLike(userId, news.getReviewId()));
            news.setFollowCnt(getFollowCnt(userId));
            news.setReviewCnt(getReviewCnt(userId));
            news.setImgUrls(getReviewImgURLs(news.getReviewId()));
            news.setComments(getComments(news.getReviewId()));
        }
        return getNewsRes;
    }

    public List<GetNewsRes> getFollowNews(Integer userId, List<Integer> scores) {
        String getNewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id  " +
                "join restaurants as RT  " +
                "on R.restaurant_id = RT.id " +
                "join (select * from follows where  follower_id= ?) as F " +
                "on R.user_id = F.user_id " +
                "where R.status = 'ACTIVE' and R.score IN(";

        Object[] params = new Object[scores.size()+1];
        params[0] = userId;
        for(int i=0; i<scores.size(); i++) {
            params[i+1] = scores.get(i);
            getNewsQuery+="?,";
        }

        getNewsQuery = getNewsQuery.substring(0, getNewsQuery.length()-1);
        getNewsQuery+=") order by R.updated_at desc ";

        List<GetNewsRes> getNewsRes = jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), params);

        for(GetNewsRes news : getNewsRes) {
            news.setWish(getWish(userId, news.getRestaurantId()));
            news.setLike(getLike(userId, news.getReviewId()));
            news.setFollowCnt(getFollowCnt(userId));
            news.setReviewCnt(getReviewCnt(userId));
            news.setImgUrls(getReviewImgURLs(news.getReviewId()));
            news.setComments(getComments(news.getReviewId()));
        }
        return getNewsRes;
    }

    public int checkReviewId(int reviewId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId);
    }
}

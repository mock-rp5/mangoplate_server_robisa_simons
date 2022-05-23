package com.example.demo.src.restaurant;

import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.menu.model.GetRestaurantMenu;
import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;
import com.example.demo.src.review.model.GetReviewRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RestaurantDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkRestaurantId(int restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ?)";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public GetRestaurantDetailRes getRestaurantDetail(Integer restaurantId) {
        String getRestaurantQuery = " select R.id, R.name, R.view, R.address, R.latitude, R.longitude, R.day_off, R.open_hour, R.close_hour, R.break_time, R.min_price, R.max_price, R.park_info, R.website, R.food_category, C.name " +
                "from restaurants as R " +
                "join categories_food as C " +
                "on R.food_category = C.id " +
                "where R.id = ?";

        GetRestaurantDetailRes getRestaurantDetailRes = jdbcTemplate.queryForObject(getRestaurantQuery,
                (rs, rowNum) -> new GetRestaurantDetailRes(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getDouble(5),
                        rs.getDouble(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getInt(11),
                        rs.getInt(12),
                        rs.getString(13),
                        rs.getString(14),
                        rs.getInt(15),
                        rs.getString(16)
                ), restaurantId);

        getRestaurantDetailRes.setImgUrls(getRestaurantImgUrls(restaurantId));
        getRestaurantDetailRes.setReviews(getReviews(restaurantId));
        getRestaurantDetailRes.setScore(getRestaurantScore(restaurantId));
        getRestaurantDetailRes.setMenus(getRestaurantMenus(restaurantId));

        return getRestaurantDetailRes;

    }

    private List<String> getRestaurantImgUrls(Integer restaurantId) {
        String getRestaurantImgUrlQuery = "select img_url from images_restaurant where restaurant_id = ?";
        List<String> imgUrls = new ArrayList<>();

        jdbcTemplate.query(getRestaurantImgUrlQuery,
                (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), restaurantId);

        return imgUrls;
    }

    public List<GetReviewRes> getReviews(int restaurantId) {
        String getReviewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, U.profile_img_url, R.restaurant_id, RT.name " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.id = ?";

        List<GetReviewRes> getReviewRes = jdbcTemplate.query(getReviewsQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8)
                ), restaurantId);

        for(GetReviewRes review : getReviewRes) {
            //List<GetCommentRes> commentRes = getComments(review.getId());
            List<String> imgUrls = getReviewImgURLs(review.getId());

            //review.setComments(commentRes);
            review.setImgUrls(imgUrls);
        }

        return getReviewRes;

    }
    public Integer getRestaurantScore(int restaurantId) {
        String getScoreQuery = "select avg(score) from reviews where restaurant_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getScoreQuery, int.class, restaurantId);
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

    public int increaseView(Integer restaurantId) {
        String increaseViewQuery = "update restaurants " +
                "set view = " +
                "(select viewer from " +
                "(select view+1 as viewer from restaurants where id = ?) A) " +
                "where id = ?";

        return jdbcTemplate.update(increaseViewQuery, restaurantId, restaurantId);
    }

    public List<GetRestaurantMenu> getRestaurantMenus(Integer restaurantId) {
        String getMenusQuery = "select id, name, price from menus where restaurant_id = ?";
        return jdbcTemplate.query(getMenusQuery,
                (rs, rowNum) -> new GetRestaurantMenu(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price")
                ), restaurantId);
    }
}

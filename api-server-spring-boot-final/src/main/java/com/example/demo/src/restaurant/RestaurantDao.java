package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentRes;

import com.example.demo.src.restaurant.model.GetRestaurantRes;
import com.example.demo.src.restaurant.model.PostRestaurantReq;
import com.example.demo.src.restaurant.model.PostRestaurantRes;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.user.model.GetUserRes;

import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.menu.model.GetRestaurantMenu;
import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.ArrayList;


@Repository
public class RestaurantDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GetRestaurantRes> getRestaurant(Double latitude, Double longitude, String foodCategories, int range) {

            String getRestaurantQuery = "select R.id,\n" +
                    "       R.name,\n" +
                    "       tr.emd_kor_nm as thirdRegion,\n" +
                    "       cf.name as foodCategory,\n" +
                    "       R.latitude,\n" +
                    "       R.longitude,\n" +
                    "       (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl,\n" +
                    "       (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews\n" +
                    "       from restaurants as R\n " +
                    "       join (SELECT * FROM (\n " +
                    "               SELECT ( 6371 * acos( cos( radians( ?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id\n " +
                    "           FROM restaurants) DATA \n" +
                    "           WHERE DATA.distance < ?) as D\n" +
                    "       on R.id = D.id" +
                    "       inner join third_regions tr on R.third_region_id = tr.id\n" +
                    "       inner join categories_food cf on R.food_category = cf.id\n" +
                    "       where R.status = 'ACTIVE' and R.food_category in " + foodCategories;

            Object[] params = new Object[] {latitude, longitude, latitude, range};
            List<GetRestaurantRes> getRestaurantRes = this.jdbcTemplate.query(getRestaurantQuery,
                    (rs, rowNum) -> new GetRestaurantRes(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("thirdRegion"),
                            rs.getString("foodCategory"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getInt("numReviews"),
                            rs.getString("imgUrl"))
            ,params );
            for (GetRestaurantRes restaurant : getRestaurantRes) {
//          평점 계산
                Double ratingsAvg = calculateRatings(restaurant.getId());
//          거리 계산
                Double distance = calculateDistance(latitude, longitude, restaurant.getLatitude(), restaurant.getLongitude(), "kilometer");
                restaurant.setRatingsAvg(ratingsAvg);
                restaurant.setDistance(distance);
            }
            return getRestaurantRes;
    }
    /*
    * 평점 계산 함수
    * Params : 레스토랑 ID
    * Return : Double 평점
    * */
    public Double calculateRatings(Long restaurantId){
        try {
            String calculateRatingsQuery = "select avg(score)as ratingsAvg\n" +
                    "from reviews\n" +
                    "where restaurant_id = ? and status = 'ACTIVE'\n" +
                    "group by restaurant_id";
            Long calculateRatingsParams = restaurantId;
            Double ratingsAvg = this.jdbcTemplate.queryForObject(calculateRatingsQuery, Double.class, calculateRatingsParams);
            return ratingsAvg;
        } catch (EmptyResultDataAccessException e) {
            return 3.0;
        }
    }
    /*
     * 거리 계산 함수
     * Params : latUser - 유저 위도,
     *          longUser - 유저 경도,
     *          latRstr - 레스토랑 위도,
     *          latRstr - 레스토랑 경도,
     *          unit - 거리 단위 ( kilometer / meter )
     * Return : Double - 거리
     * */
    public Double calculateDistance(Double latUser, Double longUser, Double latRstr, Double longRstr, String unit){

        double theta = longUser - longRstr;
        double dist = Math.sin(deg2rad(latUser)) * Math.sin(deg2rad(latRstr)) + Math.cos(deg2rad(latUser)) * Math.cos(deg2rad(latRstr)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return dist;
    }

    /*
     * 거리 계산 함수를 위한 반위 변경 함수
     * degrees -> radians
     *
     * Params : deg - 각도,
     * Return : Double - 거리
     * */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*
     * 거리 계산 함수를 위한 단위 변경 함수
     * radians -> degrees
     *
     * Params : rad - 라디언,
     * Return : Double - 거리
     * */
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

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

    @Transactional
    public PostRestaurantRes createRestaurant(PostRestaurantReq postRestaurantReq) {
        String createRestaurantQuery =
                "INSERT INTO restaurants (name, view, address, first_region_id, second_region_id, third_region_id, latitude, longitude, open_hour, " +
                        "close_hour, break_time, min_price, max_price, day_off, park_info, last_order, website, status, created_at, updated_at, food_category, user_id) " +
                        "VALUES ( ?, 0, ?, null, null, null, ?, ?, null, null, null, null, null, null, DEFAULT, null, null, DEFAULT, DEFAULT, DEFAULT, 1, ?)\n";
//        food_category default 값 설정 필요.
        Object[] createRestaurantParams = new Object[]{postRestaurantReq.getName(), postRestaurantReq.getAddress(),
                                                    postRestaurantReq.getLatitude(), postRestaurantReq.getLongitude(), postRestaurantReq.getUserId()};
        this.jdbcTemplate.update(createRestaurantQuery, createRestaurantParams);

        String lastInsertIdQuery = "select name, address, concat(DATEDIFF(NOW(),created_at),'일 전')as 'createdAt'\n" +
                "from restaurants\n" +
                "where id = (select last_insert_id())";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,
                (rs,rowNum)-> new PostRestaurantRes(
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("createdAt")
        ));
    }

    public Integer findByNameAndAddress(PostRestaurantReq postRestaurantReq){
        String findByNameAndAddressQuery = "select exists(select id from restaurants where name = ? and address = ?)";
        Object[] findByNameAndAddressParams = new Object[]{postRestaurantReq.getName(), postRestaurantReq.getAddress()};
        return this.jdbcTemplate.queryForObject(findByNameAndAddressQuery,
                int.class,
                findByNameAndAddressParams);
    }
    @Transactional
    public Integer deleteRestaurant(Integer restaurantId){
        String deleteRestaurantQuery = "UPDATE restaurants r SET r.status = 'INACTIVE' WHERE r.id = ?";
        Integer deleteRestaurantParams = restaurantId;
        return this.jdbcTemplate.update(deleteRestaurantQuery, deleteRestaurantParams);
    }


}

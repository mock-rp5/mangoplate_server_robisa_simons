package com.example.demo.src.restaurant;

import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.user.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public class RestaurantDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GetRestaurantRes> getRestaurant(Double latitude, Double longitude, String foodCategories) {
        try {
            String getRestaurantQuery = "select R.id,\n" +
                    "       R.name,\n" +
                    "       tr.emd_kor_nm as thirdRegion,\n" +
                    "       cf.name as foodCategory,\n" +
                    "       R.latitude,\n" +
                    "       R.longitude,\n" +
                    "       (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl,\n" +
                    "       (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews\n" +
                    "\n" +
                    "from restaurants as R\n" +
                    "inner join third_regions tr on R.third_region_id = tr.id\n" +
                    "inner join categories_food cf on R.food_category = cf.id\n";
            List<GetRestaurantRes> getRestaurantRes = this.jdbcTemplate.query(getRestaurantQuery,
                    (rs, rowNum) -> new GetRestaurantRes(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("thirdRegion"),
                            rs.getString("foodCategory"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getInt("numReviews"),
                            rs.getString("imgUrl")));
            for (GetRestaurantRes restaurant : getRestaurantRes) {
//          평점 계산
                Double ratingsAvg = calculateRatings(restaurant.getId());
//          거리 계산
                Double distance = calculateDistance(latitude, longitude, restaurant.getLatitude(), restaurant.getLongitude(), "kilometer");
                restaurant.setRatingsAvg(ratingsAvg);
                restaurant.setDistance(distance);
                System.out.println(restaurant.toString());
            }
            return getRestaurantRes;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
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
}

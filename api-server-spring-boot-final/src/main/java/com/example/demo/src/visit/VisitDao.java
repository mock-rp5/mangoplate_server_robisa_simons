package com.example.demo.src.visit;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.visit.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VisitDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkRestaurant(Integer restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ?)";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public Integer createVisit(int restaurantId, int userId) {
        String createVisitQuery = "insert into visits(restaurant_id, user_id, status) " +
                "values(?,?,'ACTIVE')";
        jdbcTemplate.update(createVisitQuery, restaurantId, userId);

        String getLastIdQuery = "select id from visits order by id desc limit 1";
        return jdbcTemplate.queryForObject(getLastIdQuery, int.class);
    }

    public int checkUser(Integer userIdxByJwt) {
        String checkUserQuery = "select exists (select * from users where id = ?)";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdxByJwt);
    }

    public int getVisitCheck(Integer restaurantId, Integer userIdxByJwt) {
        String getVisitQuery = "select exists (select restaurant_id, user_id from visits where restaurant_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getVisitQuery, int.class, restaurantId, userIdxByJwt);
    }

    public int checkVisit(Integer restaurantId, Integer userId,Integer visitId) {
        String checkVisitQuery = "select exists (select * from visits where id = ? and restaurant_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkVisitQuery, int.class, visitId, restaurantId, userId);
    }

    public int deleteVisit(int reviewId) {
        String deleteVisitQuery = "update visits set status = 'INACTIVE' where id = ? and status = 'ACTIVE'";
        return jdbcTemplate.update(deleteVisitQuery, reviewId);
    }

    public GetVisitRes GetVisitRes(Integer restaurantId, Integer userIdxByJwt) {
        String getVisitQuery = "select id, count(*) as count from visits where restaurant_id = ? and user_id = ? and status = 'ACTIVE' group by user_id, restaurant_id ";
        return jdbcTemplate.queryForObject(getVisitQuery,
                (rs, rowNum) ->new GetVisitRes(rs.getInt("id"), rs.getInt("count")),
                restaurantId, userIdxByJwt);
    }


    public GetVisitByUserRes getVisitByUser(Integer userIdxByJwt) {
        String getVisitId = "select id, restaurant_id from visits where user_id = ? and status = 'ACTIVE'";

        GetUserInfo getUserInfo = getUserInfo(userIdxByJwt);
        
        List<GetVisit> getVisits = jdbcTemplate.query(getVisitId,
                (rs, rowNum) -> new GetVisit(
                        rs.getInt("id"),
                        rs.getInt("restaurant_id")
                ), userIdxByJwt);
        
        for(GetVisit visit : getVisits) {
            visit.setGetRestaurantInfo(getRestaurantInfo(visit.getRestaurantId()));
        }

        return new GetVisitByUserRes(getUserInfo, getVisits);
    }

    private GetRestaurantInfo getRestaurantInfo(int restaurantId) {
        String getRestaurantInfoQuery = "select R.name, R.view, C.name, " +
                "(select img_url from images_restaurant where restaurant_id = ? limit 1) as img, " +
                "(select count(*) from reviews where restaurant_id = ?) as review " +
                " from restaurants as R " +
                " join categories_food as C " +
                " on R.food_category = C.id " +
                " where R.id = ?";

        return jdbcTemplate.queryForObject(getRestaurantInfoQuery,
                (rs, rowNum) -> new GetRestaurantInfo(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5)

                ), restaurantId, restaurantId, restaurantId );
    }

    private GetUserInfo getUserInfo(Integer userIdxByJwt) {
        String getUserInfoQuery = "select id, user_name, profile_img_url, " +
                "(select count(*) from follows where user_id = ?) as follow, " +
                "(select count(*) from reviews where user_id = ?) as review " +
                " from users " +
                " where id = ?";

        return jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new GetUserInfo(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5)
                ), userIdxByJwt, userIdxByJwt, userIdxByJwt);
    }

    public int updateVisit(PutVisitReq putVisitReq, Integer userIdxByJwt) {
        String updateVisitQuery = "update visits set content = ? where id = ? and restaurant_id = ? and user_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.update(updateVisitQuery, putVisitReq.getContent(),putVisitReq.getVisitId(), putVisitReq.getRestaurantId(), userIdxByJwt);
    }

    public int checkTodayVisit(Integer restaurantId, Integer userIdxByJwt, String currentDate) {
        String checkTodayVisit = "select exists (select * from visits where updated_at like ? and restaurant_id =? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkTodayVisit, int.class, currentDate+"%", restaurantId, userIdxByJwt);
    }
}

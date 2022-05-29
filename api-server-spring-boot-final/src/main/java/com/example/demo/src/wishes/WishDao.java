package com.example.demo.src.wishes;

import com.example.demo.src.wishes.model.GetWishRestaurantRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GetWishRestaurantRes> getWishRestaurants(Integer userId, Integer targetUserId) {
        String getWishRestaurantsQuery = "select w.id," +
                "       R.id,\n" +
                "       R.name,\n" +
                "       (select r.name from regions r where r.id = R.region) as regionName,\n" +
                "       R.view,\n" +
                "       ROUND((select avg(Rev.score) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE'),1)as ratingsAvg,\n" +
                "       (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews,\n" +
                "       (select EXISTS(select id from wishes w2 where w2.user_id = ? and w2.status = 'ACTIVE' and w2.restaurant_id = R.id)) as isWish,\n" +
                "       (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl\n" +
                "from wishes w\n" +
                "inner join restaurants R on w.restaurant_id = R.id\n" +
                "where w.user_id = ? and w.status = 'ACTIVE'";

        List<GetWishRestaurantRes> getWishRestaurantRes = jdbcTemplate.query(getWishRestaurantsQuery,
                (rs, rowNum) -> new GetWishRestaurantRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getDouble(6),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getString(9)), userId, targetUserId);
        return getWishRestaurantRes;
    }

    public int putMemo(Integer wishId, String memo) {
        String putMemoQuery = "UPDATE wishes w SET w.memo = ? WHERE w.id = ?";
        return jdbcTemplate.update(putMemoQuery, memo, wishId);
    }
    public int checkRestaurantId(int restaurantId) {
        // 레스토랑이 존재하는지
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public int findWishId(Integer restaurantId, Integer userId){
        // 위시 정보를 레스토랑 아이디와 유저 아이디로 조회
        try {
            String findWishIdQuery = "select id from wishes where restaurant_id = ? and user_id = ?";
            return jdbcTemplate.queryForObject(findWishIdQuery, int.class, new Object[]{restaurantId, userId});
        } catch (EmptyResultDataAccessException e){
            return 0;
        }
    }
    public int checkWishId(int wishId) {
        // 위시 존재 정보를 위시 아이디로 조회
        String checkWishIdQuery = "select exists (select * from wishes where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkWishIdQuery, int.class, wishId);
    }

    public int postWish(Integer restaurantId, Integer userId) {
        String postWishQuery = "insert into wishes(restaurant_id, status, created_at, updated_at, user_id, memo) " +
                "values(?, 'ACTIVE', DEFAULT , DEFAULT , ?, null) ";
        Object[] queryParams = new Object[]{restaurantId, userId};
        return jdbcTemplate.update(postWishQuery, queryParams);
    }

    public int changeStatusToActive(int wishId){
        String Query = "UPDATE wishes w SET w.status = 'ACTIVE' WHERE w.id = ?";
        Integer Params = wishId;
        return this.jdbcTemplate.update(Query, Params);
    }
    public int deleteWish(int restaurantId, int userId) {
        String deleteWishQuery = "UPDATE wishes w SET w.status = 'INACTIVE' WHERE w.restaurant_id = ? and user_id = ?";
        Object[] deleteWishParams = new Object[]{restaurantId, userId};
        return this.jdbcTemplate.update(deleteWishQuery, deleteWishParams);
    }

    public int getWish(int wishId) {
        String getWishQuery = "select exists (select * from wishes where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getWishQuery, int.class, wishId);
    }
    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists (select * from users where id =? and status = 'ACTIVE') ";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }

    public int getUserIdFromWish(int wishId) {
        String getUserIdFromWishQuery = "select user_id from wishes where id =? and status = 'ACTIVE'";
        Integer result = jdbcTemplate.queryForObject(getUserIdFromWishQuery, int.class, wishId);
        return result;
    }

}

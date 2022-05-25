package com.example.demo.src.wishes;

import com.example.demo.src.restaurant.model.PutRestaurantReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.security.PublicKey;
import java.util.Optional;

@Repository
public class WishDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkRestaurantId(int restaurantId) {
        // 레스토랑이 존재하는지
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public int findWishId(Integer restaurantId, Integer userId){
        // 위시 정보를 레스토랑 아이디와 유저 아이디로 조회
        try {
            String checkRestaurantQuery = "select id from wishes where restaurant_id = ? and user_id = ?";
            return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, new Object[]{restaurantId, userId});
        } catch (EmptyResultDataAccessException e){
            return 0;
        }
    }
    public int checkWishId(int wishId) {
        // 위시 존재 정보를 위시 아이디로 조회
        String checkRestaurantQuery = "select exists (select * from wishes where id = ?)";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, wishId);
    }

    public int postWish(Integer restaurantId, Integer userId) {
        String postWishQuery = "insert into wishes(restaurant_id, status, created_at, updated_at, user_id) " +
                "values(?, 'ACTIVE', DEFAULT , DEFAULT , ?) ";
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
        String checkRestaurantQuery = "select exists (select * from wishes where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, wishId);
    }
}

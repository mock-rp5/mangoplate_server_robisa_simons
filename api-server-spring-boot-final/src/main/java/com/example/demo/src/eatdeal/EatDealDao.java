package com.example.demo.src.eatdeal;

import com.example.demo.src.eatdeal.model.GetEatDealOrderRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.eatdeal.model.PostEatDealReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EatDealDao {
    @Autowired private JdbcTemplate jdbcTemplate;

    public List<GetEatDealRes> getEatDeals(Double latitude, Double longitude, Integer range) {
        String getEatDealQuery = "select R.id, R.name, M.name, M.price, M.discount_rate, M.eatdeal_desc " +
                "    from menus as M " +
                "    join restaurants as R " +
                "    on M.restaurant_id = R.id " +
                "    join (SELECT * FROM ( " +
                "             SELECT ( 6371 * acos( cos( radians(?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id " +
                "    FROM restaurants) DATA " +
                "                  WHERE DATA.distance < ?) as D " +
                "    on R.id = D.id " +
                "    where M.discount_rate >0 and M.status = 'ACTIVE'";

        return jdbcTemplate.query(getEatDealQuery,
                (rs, rowNum) -> new GetEatDealRes(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getDouble(5),
                        rs.getString(6)
                )
                ,latitude, longitude, latitude, range);

    }

    public int checkRestaurant(Integer restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ?)";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public int checkMenu(Integer menuId) {
        String checkMenuQuery = "select exists (select * from menus where id = ? and discount_rate >0)";
        return jdbcTemplate.queryForObject(checkMenuQuery, int.class, menuId);
    }

    public int orderEatDeal(Integer userId, PostEatDealReq postEatDealReq) {
        String orderEatDealQuery = "insert into eat_deal_orders(user_id, menu_id, restaurant_id, price, status) " +
                "values(?,?,?,?,'ACTIVE')";
        int price = getMenuPrice(postEatDealReq.getMenuId());
        Object[] params = new Object[]{userId, postEatDealReq.getMenuId(), postEatDealReq.getRestaurantId(), price};

        return jdbcTemplate.update(orderEatDealQuery, params);
    }

    private int getMenuPrice(Integer menuId) {
        String getMenuPriceQuery = "select (price - (price / 100 * discount_rate)) as price from menus where id = ?";
        return jdbcTemplate.queryForObject(getMenuPriceQuery, int.class, menuId);
    }

    public List<GetEatDealOrderRes> getEatDealOrders(Integer userId) {
        String getEatDealOrdersQuery = "select id, user_id, restaurant_id, menu_id, price from eat_deal_orders where user_id = ?";
        List<GetEatDealOrderRes> getEatDealOrderRes = jdbcTemplate.query(getEatDealOrdersQuery,
                (rs, rowNum) -> new GetEatDealOrderRes(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("menu_id"),
                        rs.getInt("price")

                ), userId);

        for(GetEatDealOrderRes order : getEatDealOrderRes) {
            order.setMenuName(getMenuName(order.getMenuId()));
            order.setRestaurantName(getRestaurantName(order.getRestaurantId()));
        }
        return getEatDealOrderRes;
    }

    private String getRestaurantName(int restaurantId) {
        String getRestaurantNameQuery = "select name from restaurants where id = ?";
        return jdbcTemplate.queryForObject(getRestaurantNameQuery, String.class, restaurantId);
    }

    private String getMenuName(int menuId) {
        String getMenuNameQuery = "select name from menus where id = ?";
        return jdbcTemplate.queryForObject(getMenuNameQuery, String.class, menuId);
    }
}

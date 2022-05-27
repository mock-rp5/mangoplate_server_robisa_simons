package com.example.demo.src.eatdeal;

import com.example.demo.src.eatdeal.model.GetEatDeal;
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

    public List<GetEatDeal> getEatDeals(Double latitude, Double longitude, Integer range) {
        String getEatDealQuery = "select D.id, D.name, E.restaurant_desc, E.menu_desc, E.notice, E.manual, E.refund_policy, E.question, E.price, E.discount_rate, E.menu_name, date_format(E.start_date, '%Y-%m-%d'), DATE_ADD(date_format(E.start_date, '%Y-%m-%d'), interval E.expired_date DAY), E.expired_date, E.emphasis,  E.id\n" +
                "from eat_deals as E\n" +
                "join (SELECT * FROM \n" +
                "\t\t(SELECT name, ( 6371 * acos( cos( radians(?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id\n" +
                "        FROM restaurants) DATA\n" +
                "      WHERE DATA.distance < ?\n" +
                "      ) as D\n" +
                " on E.restaurant_id = D.id\n" +
                " where E.status = 'ACTIVE'";

        List<GetEatDeal> getEatDeals = jdbcTemplate.query(getEatDealQuery,
                (rs, rowNum) -> new GetEatDeal(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getInt(9),
                        rs.getInt(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getInt(14),
                        rs.getString(15),
                        rs.getInt(16)
                )
                ,latitude, longitude, latitude, range);

        for(GetEatDeal getEatDeal : getEatDeals) {
            getEatDeal.setImgUrls(getEatDealUrl(getEatDeal.getEatDealId()));
        }
        return getEatDeals;
    }

    private List<String> getEatDealUrl(int eatDealId) {
        String eatDealUrlQuery = "select img_url from eat_deal_imgs where eat_deal_id = ?";
        return jdbcTemplate.query(eatDealUrlQuery, (rs, rowNum) -> rs.getString("img_url"), eatDealId);
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

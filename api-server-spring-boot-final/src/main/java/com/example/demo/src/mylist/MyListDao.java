package com.example.demo.src.mylist;

import com.example.demo.src.mylist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyListDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<GetMyListRes> getMyList(int userId){
        String getMyListQuery = "select m2.id, m2.title, m2.content, \n" +
                "       (select r.img_url \n" +
                "        from mylist_restaurant m \n" +
                "        inner join images_restaurant r on m.restaurant_id = r.id\n" +
                "        where m.mylist_id = m2.id\n" +
                "        limit 1) as imgUrl \n" +
                "from mylists m2 where m2.user_id = ? and status='ACTIVE'";
        return this.jdbcTemplate.query(getMyListQuery,
                (rs,rowNum) -> new GetMyListRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("imgUrl")), userId);
    }

    public GetMyListDetailRes getMyListDetail(int userId, int myListId){
        String getMyListQuery = "select m.id, m.created_at as createdAt, m.view, m.title, m.user_id as userId, u.user_name as userName, u.profile_img_url as profileImgUrl,\n" +
                "       (select COUNT(*) from reviews r where r.user_id = m.user_id) as numReviews,\n" +
                "       (select COUNT(*) from follows f where f.user_id = m.user_id) as numFollowers,\n" +
                "       m.content\n" +
                "from mylists m\n" +
                "join users u on u.id = m.user_id\n" +
                "where m.status = 'ACTIVE' and m.id = ?";
        GetMyListDetailRes getMyListDetailRes = this.jdbcTemplate.queryForObject(getMyListQuery,
                (rs,rowNum) -> new GetMyListDetailRes(
                        rs.getInt("id"),
                        rs.getString("createdAt"),
                        rs.getInt("view") + 1,
                        rs.getString("title"),
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("numReviews"),
                        rs.getInt("numFollowers"),
                        rs.getString("content"))
                        , myListId);
        getMyListDetailRes.setRestaurants(getRestaurants(getMyListDetailRes.getMylistId()));
        updateView(getMyListDetailRes.getView(), getMyListDetailRes.getMylistId());
        return getMyListDetailRes;
    }

    public List<SubRestaurantInfo> getRestaurants(int myListId) {
        String getRestaurantsQuery = "select restaurantId,\n" +
                "       restaurantStatus,\n" +
                "       imgUrl,\n" +
                "       restaurantName,\n" +
                "       address,\n" +
                "       ratingsAvg,\n" +
                "       reviewId,\n" +
                "       reviewUserId,\n" +
                "       reviewUserProfileImg,\n" +
                "       reviewUserName,\n" +
                "       reviewContent\n" +
                "from mylist_restaurant m\n" +
                "inner join (select r.id as restaurantId,\n" +
                "       r.status as restaurantStatus,\n" +
                "       ir.img_url as imgUrl,\n" +
                "       r.name as restaurantName,\n" +
                "       r.address as address,\n" +
                "       round((select AVG(rv.score) from reviews rv where rv.restaurant_id = r.id),1) as ratingsAvg,\n" +
                "       r2.reviewId,\n" +
                "       r2.reviewUserId,\n" +
                "       r2.reviewUserProfileImg,\n" +
                "       r2.reviewUserName,\n" +
                "       r2.reviewContent\n" +
                "\n" +
                "\n" +
                "       from restaurants r\n" +
                "        left join (select r3.user_id as reviewUserId,\n" +
                "                          r3.id as reviewId,\n" +
                "                          u.profile_img_url as reviewUserProfileImg,\n" +
                "                          u.user_name as reviewUserName,\n" +
                "                          r3.content as reviewContent,\n" +
                "                          r3.restaurant_id\n" +
                "                          from reviews r3 inner join users u on r3.user_id = u.id\n" +
                "                          order by r3.created_at desc\n" +
                "                          ) as r2 on r.id = r2.restaurant_id\n" +
                "        inner join images_restaurant ir on r.id = ir.restaurant_id\n" +
                "        group by r.id )as A on m.restaurant_id = A.restaurantId\n" +
                "where m.status = 'ACTIVE' and m.mylist_id = ?";

        List<SubRestaurantInfo> getRestaurantInfo = this.jdbcTemplate.query(getRestaurantsQuery,
                (rs, rowNum) -> new SubRestaurantInfo(
                        rs.getInt("restaurantId"),
                        rs.getString("restaurantStatus"),
                        rs.getString("imgUrl"),
                        rs.getString("restaurantName"),
                        rs.getString("address"),
                        rs.getDouble("ratingsAvg"),
                        rs.getInt("reviewId"),
                        rs.getInt("reviewUserID"),
                        rs.getString("reviewUserProfileImg"),
                        rs.getString("reviewUserName"),
                        rs.getString("reviewContent"))
                , myListId);
        return getRestaurantInfo;
    }

    public int insert2MyList(Integer restaurantId, Integer myListId) {
        String insert2MyListQuery = "INSERT INTO mylist_restaurant (mylist_id, restaurant_id, status, created_at, updated_at) VALUES (?, ?, DEFAULT, DEFAULT, DEFAULT)";
        return jdbcTemplate.update(insert2MyListQuery, myListId, restaurantId);
    }
    public int checkMyList(Integer userId) {
        String checkMyListQuery = "select exists (select * from mylists where user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkMyListQuery, int.class, userId);
    }

    public int checkMyListId(Integer myListId) {
        String checkMyListIdQuery = "select exists (select * from mylists where id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkMyListIdQuery, int.class, myListId);
    }

    public int checkDuplicated(Integer myListId, Integer restaurantId) {
        String checkDuplicatedQuery = "select exists (select * from mylist_restaurant where mylist_id = ? and restaurant_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkDuplicatedQuery, int.class, new Object[]{myListId, restaurantId});
    }

    public Integer createMyList(PostMyListReq postMyListReq, Integer userId) {
        String createMyListQuery = "insert into mylists(title, content, view, status, created_at, updated_at, user_id) " +
                    "values(?, ?, DEFAULT, DEFAULT, DEFAULT, DEFAULT, ?)";

        Object[] createMyListParams = new Object[] {postMyListReq.getTitle(), postMyListReq.getContent(), userId};
        int result = jdbcTemplate.update(createMyListQuery, createMyListParams);

        return result;
    }

    public int updateMyList(PutMyListReq putMyListReq) {
        String updateMyListQuery = "update mylists set title = ?, content = ? where id = ?;\n";
        return jdbcTemplate.update(updateMyListQuery, putMyListReq.getTitle(), putMyListReq.getContent(), putMyListReq.getMyListId());
    }

    public int deleteMyList(int myListId) {
        String deleteMyListQuery = "update mylists set status = 'INACTIVE' where id = ?";
        return jdbcTemplate.update(deleteMyListQuery, myListId);
    }
    public int deleteAllRestaurants(int myListId) {
        String deleteAllQuery = "update mylist_restaurant set status = case mylist_id when ? then 'INACTIVE' END\n" +
                "where mylist_id = ?;";
        return jdbcTemplate.update(deleteAllQuery, myListId, myListId);
    }
    public void updateView(Integer view , Integer myListId) {
        String updateView = "update mylists set view = ? where id = ?;\n";
        this.jdbcTemplate.update(updateView, view, myListId);
    }

}

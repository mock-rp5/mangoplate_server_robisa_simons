package com.example.demo.src.bookmark;

import com.example.demo.src.bookmark.model.GetBookmarkCountRes;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookmarkDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public GetBookmarkCountRes getBookmarkCount(Integer userId) {
        try{
            String getBookmarkCountQuery = "select count(case when contents_type='TOPLIST' then 1 end)as toplistCount,\n" +
                    "       count(case when contents_type='MYLIST' then 1 end)as mylistCount,\n" +
                    "       count(case when contents_type='MANGOPICKSTORY' then 1 end)as mangoPickStoryCount\n" +
                    "from bookmarks\n" +
                    "where user_id = ? and status = 'ACTIVE'";
            GetBookmarkCountRes getBookmarkCountRes = this.jdbcTemplate.queryForObject(getBookmarkCountQuery,
                    (rs, rowNum) -> new GetBookmarkCountRes(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3)), userId);
            return getBookmarkCountRes;
        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return new GetBookmarkCountRes();
        }
    }

    public int checkContentId(String contentsType, int contentsId) {
        String checkContentIdQuery = "select exists (select * from " + contentsType + " where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkContentIdQuery, int.class, contentsId);
    }

    public int checkBookmarked(int userId, String contentsType, int contentsId) {
        String checkBookmarkedQuery = "select exists (select * from bookmarks where user_id = ? and contents_type = ? and contents_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkBookmarkedQuery, int.class, userId, contentsType, contentsId);
    }
    public int checkUnmarked(int userId, String contentsType, int contentsId) {
        String checkUnmarkedQuery = "select exists (select * from bookmarks where user_id = ? and contents_type = ? and contents_id = ? and status ='INACTIVE')";
        return jdbcTemplate.queryForObject(checkUnmarkedQuery, int.class, userId, contentsType, contentsId);
    }


    public int createBookmark(int userId, String contentsType, int contentsId) {
        String createRelationQuery = "insert into bookmarks (user_id, contents_id, contents_type, status, created_at, updated_at) VALUES (?, ?, ?, DEFAULT, DEFAULT, DEFAULT)";
        return jdbcTemplate.update(createRelationQuery, userId, contentsId, contentsType);

    }

    public int postBookmark(int userId, String contentsType, int contentsId) {
        String postBookmarkQuery = "update bookmarks t SET t.status = 'ACTIVE' WHERE t.user_id = ? and t.contents_type = ? and t.contents_id = ? ";
        return jdbcTemplate.update(postBookmarkQuery, userId, contentsType, contentsId);
    }

    public int cancelBookmark(int userId, String contentsType, int contentsId) {
        String cancelBookmarkQuery = "update bookmarks t SET t.status = 'INACTIVE' WHERE t.user_id = ? and t.contents_type = ? and t.contents_id = ? ";
        return jdbcTemplate.update(cancelBookmarkQuery, userId, contentsType, contentsId);
    }
}

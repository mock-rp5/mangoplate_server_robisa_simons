package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select * from UserInfo";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("user_phone"),
                        rs.getString("profile_img_url"))
                );
    }

    public List<GetUserRes> getUsersByEmail(String email){
        String getUsersByEmailQuery = "select id, user_name, email, user_phone, profile_img_url from users where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("user_phone"),
                        rs.getString("profile_img_url")),
                getUsersByEmailParams);
    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select id, user_name, email, user_phone, profile_img_url from users where id = ?";
        int getUserParams = userIdx;
        GetUserRes getUserRes = this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("user_phone"),
                        rs.getString("profile_img_url")),
                getUserParams);

        Pair<List<Integer>, List<Integer>> followPair = getFollow(userIdx);

        getUserRes.setFollowers(getFollows(followPair.getFirst()));
        getUserRes.setFollowings(getFollows(followPair.getSecond()));

        return getUserRes;
    }

    private List<GetFollowRes> getFollows(List<Integer> followingsIds) {
        String getFollowQuery = "select U.id, U.user_name, U.is_holic, R.postCnt, F.followerCnt " +
                "from users U " +
                "left join (select count(user_id) as postCnt , user_id from reviews where user_id = ? and status = 'ACTIVE') R " +
                "on U.id = R.user_id " +
                "left join (select count(user_id) as followerCnt , user_id from follows where user_id = ? and status = 'ACTIVE') F " +
                "on U.id = F.user_id " +
                "where U.id = ?";

        List<GetFollowRes> getFollowRes = new ArrayList<>();

        for(Integer id : followingsIds) {
            getFollowRes.add(
                    jdbcTemplate.queryForObject(getFollowQuery,
                            (rs, rowNum) -> new GetFollowRes(
                                    rs.getInt(1),
                                    rs.getString(2),
                                    rs.getString(3),
                                    rs.getInt(4),
                                    rs.getInt(5)
                            ), id, id, id));
        }
        return getFollowRes;
    }


    // 유저의 팔로우, 팔로윙 유저들 조회
    private Pair<List<Integer>, List<Integer>> getFollow(int userIdx) {
        String getFollowQuery = "select follower_id from follows where user_id = ? and status = 'ACTIVE'";
        String getFollowingQuery = "select user_id from follows where follower_id = ? and status = 'ACTIVE'";

        List<Integer> follows = new ArrayList<>();
        List<Integer> followings = new ArrayList<>();

        jdbcTemplate.query(getFollowQuery, (rs, rowNum) -> follows.add(rs.getInt("follower_id")), userIdx);
        jdbcTemplate.query(getFollowingQuery, (rs, rowNum) -> followings.add(rs.getInt("user_id")), userIdx);

        return Pair.of(follows, followings);
    }


    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into users (password, email, user_name, is_holic, status) VALUES (?,?,?, 0, 'ACTIVE')";
        Object[] createUserParams = new Object[]{postUserReq.getPassword(), postUserReq.getEmail(), postUserReq.getUserName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select id from users order by id desc limit 1";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from users where email = ? and social_provider is null)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PutUserReq putUserReq){
        String modifyUserNameQuery = "update users set user_name = ?, user_phone = ? where id = ?";
        Object[] modifyUserNameParams = new Object[]{putUserReq.getPhoneNumber(), putUserReq.getUserName(), putUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select id, password, email, user_name, user_phone from users where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("id"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("user_name"),
                        rs.getString("user_phone")
                ),
                getPwdParams
                );

    }


    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists (select * from users where id =? and status = 'ACTIVE') ";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }
}

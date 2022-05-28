package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FollowProvider {
    private final FollowDao dao;

    final Logger logger = LoggerFactory.getLogger(FollowProvider.class);

    @Autowired
    public FollowProvider(FollowDao dao) {
        this.dao = dao;
    }

    public int checkUnFollowed(Integer userId, Integer followeeId) throws BaseException {
        try{
            return dao.checkUnFollowed(userId, followeeId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }public int checkFollowed(Integer userId, Integer followeeId) throws BaseException {
        try{
            return dao.checkFollowed(userId, followeeId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(Integer userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

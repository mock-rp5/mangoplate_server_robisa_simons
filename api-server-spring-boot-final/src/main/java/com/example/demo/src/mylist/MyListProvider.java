package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.src.mylist.model.GetMyListDetailRes;
import com.example.demo.src.mylist.model.GetMyListRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class MyListProvider {
    private final MyListDao dao;

    final Logger logger = LoggerFactory.getLogger(MyListProvider.class);

    @Autowired
    public MyListProvider(MyListDao dao) {
        this.dao = dao;
    }

    public List<GetMyListRes> getMyList(Integer userId) throws BaseException {
        try{
            //다른 유저에 접근할 수 있지
            if(checkMyList(userId) == 0 ) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
            return dao.getMyList(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetMyListDetailRes getMyListDetail(Integer userId, Integer myListId) throws BaseException {
        try{
            if(checkMyListId(myListId) == 0 ) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);

            return dao.getMyListDetail(userId,myListId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkMyList(Integer userId) throws BaseException {
        try{
            return dao.checkMyList(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMyListId(Integer myListId) throws BaseException {
        try{
            return dao.checkMyListId(myListId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkDuplicated(Integer myListId, Integer restaurantId) throws BaseException {
        try{
            return dao.checkDuplicated(myListId, restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

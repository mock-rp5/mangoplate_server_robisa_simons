package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.src.mylist.model.DeleteMyListReq;
import com.example.demo.src.mylist.model.PostMyListReq;
import com.example.demo.src.mylist.model.PostMyListRes;
import com.example.demo.src.mylist.model.PutMyListReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
@Service
public class MyListService {
    private final MyListProvider provider;
    private final MyListDao dao;

    final Logger logger = LoggerFactory.getLogger(MyListService.class);

    @Autowired
    public MyListService(MyListProvider provider, MyListDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public PostMyListRes createMyList(PostMyListReq postMyListReq) throws BaseException {
        if(postMyListReq.getTitle().equals(null)) throw new BaseException(MYLISTS_EMPTY_TITLE);
        try {
            PostMyListRes postMyListRes = new PostMyListRes(dao.createMyList(postMyListReq), 0);
            return postMyListRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public PostMyListRes insert2MyList(List<Integer> restaurantId, Integer myListId) throws BaseException {
        int count = 0;

        try {
            for(Integer resId : restaurantId){
                if(provider.checkDuplicated(myListId, resId) == 1) { count++;}
                else dao.insert2MyList(resId, myListId);
            }
            return new PostMyListRes(myListId, count);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer updateMyList(PutMyListReq putMyListReq ) throws BaseException {
        if(provider.checkMyListId(putMyListReq.getMyListId()) == 0) {
            throw new BaseException(MYLISTS_EMPTY_MYLIST_ID);
        }
        try{
            return dao.updateMyList(putMyListReq);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public int deleteMyList(DeleteMyListReq deleteMyListReq) throws BaseException {
        if(provider.checkMyListId(deleteMyListReq.getMyListId()) == 0) {
            throw new BaseException(MYLISTS_EMPTY_MYLIST_ID);
        }
        try{
            deleteAllRestaurants(deleteMyListReq.getMyListId());
            return dao.deleteMyList(deleteMyListReq.getMyListId());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAllRestaurants(Integer myListId) throws BaseException {
        try{
            int result = dao.deleteAllRestaurants(myListId);
            if(result != 1) throw new BaseException(MYLISTS_DELETE_FAIL);
        }catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

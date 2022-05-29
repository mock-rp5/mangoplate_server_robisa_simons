package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.src.bookmark.model.GetBookmarkCountRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.BOOKMARKS_CONTENT_TYPE_INVALID_FORM;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class BookmarkProvider {
    private final BookmarkDao dao;

    final Logger logger = LoggerFactory.getLogger(BookmarkProvider.class);

    @Autowired
    public BookmarkProvider(BookmarkDao dao) {
        this.dao = dao;
    }

    public GetBookmarkCountRes getBookmarkCount(Integer userId) throws BaseException {
        try{
            return dao.getBookmarkCount(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
    public int checkBookmarked(Integer userId, String contentsType, int contentsId) throws BaseException {
        try{
            return dao.checkBookmarked(userId, contentsType, contentsId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUnmarked(Integer userId, String contentsType, int contentsId) throws BaseException {
        try{
            return dao.checkUnmarked(userId, contentsType, contentsId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkContentId(String contentsType, int contentsId) throws BaseException {
        try {
            return dao.checkContentId(contentsType, contentsId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

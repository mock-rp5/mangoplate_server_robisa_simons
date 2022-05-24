package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostCommentRes;
import com.example.demo.src.comment.model.PutCommentReq;
import com.example.demo.src.comment.model.PutCommentRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentService {
    private final CommentProvider provider;
    private final CommentDao dao;

    final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    public CommentService(CommentProvider provider, CommentDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public PostCommentRes createComment(PostCommentReq postCommentReq, int userId) throws BaseException {
        if(provider.checkReviewId(postCommentReq.getReviewId()) == 0) {
            throw new BaseException(COMMENTS_NOT_EXISTS_REVIEW);
        }
        // 댓글의 대댓글인 경우
        if(postCommentReq.getCommentId()!=null) {
            if(provider.checkCommentId(postCommentReq.getCommentId()) == 0) {
                throw new BaseException(COMMENTS_NOT_EXISTS_COMMENT);
            }
            if(provider.checkUserId(postCommentReq.getParentUserId()) == 0) {
                throw new BaseException(COMMENTS_NOT_EXISTS_PARENT_USER_ID);
            }
        }

        try {
            PostCommentRes postCommentRes = new PostCommentRes(dao.createComment(postCommentReq, userId));
            return postCommentRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PutCommentRes updateComment(PutCommentReq putCommentReq) throws BaseException {
        if(provider.checkCommentId(putCommentReq.getCommentId()) == 0) {
            throw new BaseException(COMMENTS_NOT_EXISTS_COMMENT);
        }
        try{
            PutCommentRes putCommentRes = new PutCommentRes(dao.updateComment(putCommentReq));
            return putCommentRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int deleteComment(int commentId) throws BaseException {
        if(provider.checkCommentId(commentId) == 0) {
            throw new BaseException(COMMENTS_NOT_EXISTS_COMMENT);
        }
        try{
            return dao.deleteComment(commentId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

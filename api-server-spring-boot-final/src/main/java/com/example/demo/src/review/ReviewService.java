package com.example.demo.src.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    private final ReviewProvider provider;
    private final ReviewDao dao;

    final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    public ReviewService(ReviewProvider provider, ReviewDao dao) {
        this.provider = provider;
        this.dao = dao;
    }
}

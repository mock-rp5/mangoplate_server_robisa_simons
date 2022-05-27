package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_PASSWORD(false, 2014, "비밀번호를 입력해주세요"),
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EMPTY_USER_NAME(false, 2018, "유저 이름을 입력해주세요"),

    GET_KAKAO_USER_INFO_FALI(false, 2020, "카카오 유저 정보 조회 실패했습니다."),
    KAKAO_LOGIN_FAIL(false, 2021, "카카오 로그인 실패"),
    KAKAO_LOGOUT_FAIL(false, 2022, "카카오 로그아웃 실패"),

    RESTAURANTS_EMPTY_RESTAURANT_ID(false, 2030, "식당 아이디 값을 확인해주세요"),
    MENUS_EMPTY_MENU_ID(false, 2031, "메뉴 아이디 값을 확인해주세요"),

    REVIEWS_EMPTY_REVIEW_ID(false, 2040, "리뷰 아이디 값을 확인해주세요"),
    REVIEWS_EMPTY_RESTAURANT_ID(false, 2041, "식당 아이디 값을 확인해주세요"),
    REVIEWS_EMPTY_SOCRE(false, 2042, "리뷰 점수를 입력해주세요"),
    REVIEWS_EMPTY_CONTENT(false, 2043, "리뷰 내용을 입력해주세요"),

    REVIEWS_EMPTY_IMG_ID(false, 2044, "리뷰 이미지 아이디를 입력해주세요."),

    COMMENTS_EMPTY_REVIEW_ID(false, 2060, "리뷰 아이디를 입력해주세요"),
    COMMENTS_EMPTY_COMMENT(false, 2061, "댓글 내용을 입력해주세요"),
    COMMENTS_EMPTY_PARENT_USER_ID(false, 2062, "부모 댓글의 유저 아이디를 입력해주세요"),
    COMMENTS_EMPTY_COMMENT_ID(false, 2063, "댓글 아이디를 입력해주세요"),

    EAT_DEALS_EMPTY_LATITUDE(false, 2070, "위도를 입력해주세요"),
    EAT_DEALS_EMPTY_LONGITUDE(false, 2071, "경도를 입력해주세요"),

    VISITS_EMPTY_VISIT_ID(false, 2080, "가봤어요 아이디를 입력해주세요"),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    USERS_NOT_EXISTS_USER(false, 3015, "존재하지 않은 유저입니다."),

    RESTAURANTS_NOT_EXISTS_RESTAURANT(false, 3030, "존재하지 않은 식당입니다."),
    RESTAURANTS_VIEW_INCREASE_FAIL(false, 3031, "조회수 증가 실패했습니다"),

    MENUS_NOT_EXISTS_MENU(false, 3032, "존재하지 않은 메뉴입니다."),

    REVIEWS_NOT_EXISTS_REVIEW(false, 3040, "존재하지 않은 리뷰입니다."),
    REVIEWS_NOT_EXISTS_IMG(false, 3041, "존재하지 않은 이미지 입니다."),


    COMMENTS_NOT_EXISTS_REVIEW(false, 3060, "존재하지 않은 리뷰입니다"),
    COMMENTS_NOT_EXISTS_COMMENT(false, 3061, "존재하지 않은 댓글입니다."),
    COMMENTS_NOT_EXISTS_PARENT_USER_ID(false, 3062, "존재하지 않은 유저 입니다"),

    VISITS_NOT_EXISTS_VISIT(false, 3080, "존재하지 않은 가봤어요 입니다"),
    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    REVIEWS_CREATE_FAIL(false, 4030, "리뷰 생성에 실패하였습니다"),
    REVIEWS_UPDATE_FAIL(false, 4031, "리뷰 수정에 실패했습니다"),
    REVIEWS_DELETE_FAIL(false, 4032, "리뷰 삭제에 실패했습니다"),

    REVIEW_DELETE_IMG_FAIL(false, 4033, "리뷰 이미지 삭제에 실패했습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

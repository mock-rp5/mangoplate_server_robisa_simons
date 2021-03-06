package com.example.demo.src.oauth.facebook;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.oauth.facebook.model.FacebookUser;
import com.example.demo.src.user.model.PostLoginRes;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;

@RestController
@RequestMapping("/oauth")
public class OauthController {
    private final OauthService service;
    private final OauthProvider provider;

    final Logger logger = LoggerFactory.getLogger(OauthController.class);

    @Autowired
    public OauthController(OauthService service, OauthProvider provider) {
        this.service = service;
        this.provider = provider;
    }

    //    @ResponseBody
//    @GetMapping("/facebook/login")
//    public BaseResponse<Integer> conntect() {
//        try {
//            return new BaseResponse<>(service.deleteComment(commentId));
//        }catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//    }


    @ResponseBody
    @GetMapping(value = "/facebook/login-test")
    public String getCode() {

        return "redirect:https://www.facebook.com/v14.0/dialog/oauth?&client_id=700903091250073&redirect_uri=https://halfmbbn.shop/oauth/facebook/login/callback&state=1234&scope=email";

    }

    @ResponseBody
    @GetMapping(value = "/facebook/login/callback")
    public BaseResponse<PostLoginRes> getFacebookAccessToken(@RequestParam(value = "code", required = false) String code, @RequestBody(required = false) HashMap<String, String> param) {
        System.out.println("param : " + code + param);

        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://graph.facebook.com/v14.0/oauth/access_token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST ????????? ?????? ???????????? false??? setDoOutput??? true???
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            //POST ????????? ????????? ???????????? ???????????? ???????????? ?????? ??????
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("client_id=700903091250073"); // TODO REST_API_KEY ??????
            sb.append("&redirect_uri=https://halfmbbn.shop/oauth/facebook/login/callback"); // TODO ???????????? ?????? redirect_uri ??????
            sb.append("&client_secret=35fefde06db7166853fb7c02707cc4fd"); // TODO REST_API_KEY ??????
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //?????? ????????? 200????????? ??????
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //????????? ?????? ?????? JSON????????? Response ????????? ????????????
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson ?????????????????? ????????? ???????????? JSON?????? ?????? ??????
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();

            System.out.println("access_token : " + access_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        FacebookUser userInfo = getFacebookUserInfo(access_Token);
        PostLoginRes postLoginRes = provider.loginFacebook(userInfo);
        return new BaseResponse<>(postLoginRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


//    @ResponseBody
//    @RequestMapping(value = "/facebook/login")
//    public BaseResponse<PostLoginRes> loginFacebookUser(@RequestParam(value = "code", required = false) String code, @RequestBody(required = false) HashMap<String, String> param) {
//        // ?????? ????????? ??? ??????
//        try {
//            String accessToken = "";
//            // if (code != null) accessToken = getFacebookAccessToken();
//            PostLoginRes postLoginRes = provider.loginFacebook(accessToken);
//            return new BaseResponse<>(postLoginRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

//
//    @ResponseBody
//    @Mapping(value = "/facebook/login/callback")
    public FacebookUser getFacebookUserInfo(String accessToken)  {
        System.out.println("param : " + accessToken);

        FacebookUser userInfo = new FacebookUser();
        String userId = "";
        String userName = "";
        String userEmail = "";

        String reqURL = "https://graph.facebook.com/v14.0/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST ????????? ?????? ???????????? false??? setDoOutput??? true???
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            //POST ????????? ????????? ???????????? ???????????? ???????????? ?????? ??????
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("fields=id,name,email"); // TODO REST_API_KEY ??????
            sb.append("&access_token="+accessToken); // TODO ???????????? ?????? redirect_uri ??????
            bw.write(sb.toString());
            bw.flush();

            //?????? ????????? 200????????? ??????
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //????????? ?????? ?????? JSON????????? Response ????????? ????????????
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson ?????????????????? ????????? ???????????? JSON?????? ?????? ??????
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            userId = element.getAsJsonObject().get("id").getAsString();
            userName = URLDecoder.decode(element.getAsJsonObject().get("name").getAsString(),"UTF-8");
            userEmail = URLDecoder.decode(element.getAsJsonObject().get("email").getAsString(),"UTF-8");

            System.out.println("userId : " + userId);
            System.out.println("userName : " + userName);
            System.out.println("userEmail : " + userEmail);

            userInfo.setUserName(userName);
            userInfo.setEmail(userEmail);
            userInfo.setSocialProvider("FACEBOOK");

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;
}
}


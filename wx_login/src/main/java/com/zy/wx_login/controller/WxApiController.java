package com.zy.wx_login.controller;

import com.google.gson.Gson;
import com.zy.wx_login.util.ConstantWxUtils;
import com.zy.wx_login.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@CrossOrigin
@Controller
@RequestMapping("/api/ucenter/wx")
public class WxApiController {
//    @Autowired
//    private UcenterMemberService memberService;

    /**
     * 生成微信扫描二维码
     * @return 定向到请求微信地址
     */
    @GetMapping("login")
    public String getWxCode() {

        //微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
            "?appid=%s" +
            "&redirect_uri=%s" +
            "&response_type=code" +
            "&scope=snsapi_login" +
            "&state=%s" +
            "#wechat_redirect";


        //对redirect_url进行URLEncoder编码
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
//            throw new GuliException(20001, e.getMessage());
            e.printStackTrace();
        }
        //设置%s的值
        String url = String.format(
            baseUrl,
            ConstantWxUtils.WX_OPEN_APP_ID,
            redirectUrl,
            "atguigu"
        );

        //重定向到请求微信地址
        return "redirect:" + url;
    }

    @GetMapping("callback")
    public String callback(String code,String state) {
        try {
            //1 获取code ,临时票据 ，类似于验证码


            //2 拿着code请求 微信固定的地址 ，得到两个值 access_token 和open id
            //向认证服务器发送请求换取access_token
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            String accessTokenUrl= String.format(baseAccessTokenUrl,ConstantWxUtils.WX_OPEN_APP_ID,ConstantWxUtils.WX_OPEN_APP_SECRET,code);
            //请求拼好的地址
            //使用httpclient 发送请求
            String accessTokenInfo=   HttpClientUtils.get(accessTokenUrl);
            //使用json 转换工具gosn 取出信息
            Gson gson=new Gson();
            //参数一 要转换的参数    参数2 要转换的类型
            HashMap mapAccessToken=    gson.fromJson(accessTokenInfo, HashMap.class);
            String openid= (String) mapAccessToken.get("openid");
            String access_token=  (String) mapAccessToken.get("access_token");

            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";

            //拼接两个参数
            String  userInfoUrl=String.format(baseUserInfoUrl,access_token,openid);
            String userInfo= HttpClientUtils.get(userInfoUrl);
            System.out.println("=========扫码人信息=======");
            System.out.println(userInfo);
            System.out.println("=========扫码人信息=======");

        }catch (Exception e){

        }



        System.out.println(code);
        return null;
    }

}

package com.zy.wxpayv3.controller;

import com.zy.wxpayv3.config.WxPayConfig;
import com.zy.wxpayv3.vo.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "测试控制器")
@RestController
@RequestMapping("")
@CrossOrigin
public class TestController {

    @Resource
    private WxPayConfig wxPayConfig;

    @GetMapping("/system/idea/get/list")
    public R getWxPayConfig(){
        String domain = wxPayConfig.getDomain();
        return R.ok().data("domain", domain);
    }

}

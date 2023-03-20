package com.zy.wxpayv3.controller;

import com.zy.wxpayv3.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@CrossOrigin
@RequestMapping("/api/product")
@Api(tags = "商品管理")
public class ProductController {

    @GetMapping("/test")
    @ApiOperation("测试接口")
    private R test() {
        return R
             .ok()
             .data("message","hello")
             .data("now" , new Date());
    }

}

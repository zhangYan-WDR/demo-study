package com.zy.wxpayv3.controller;

import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.vo.R;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "订单相关接口")
@RequestMapping("/api/order-info")
public class OrderController {

    @Resource
    private OrderInfoService orderInfoService;

    @GetMapping("/list")
    public R list(){
        List<OrderInfo> orderInfos = orderInfoService.listOrderByCreateTimeDesc();
        return R.ok().data("list", orderInfos);
    }

}

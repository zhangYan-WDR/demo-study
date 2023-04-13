package com.zy.wxpayv3.controller;

import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.enums.OrderStatus;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 查询订单状态
     * @param orderNo
     * @return
     */
    @ApiOperation("查询订单状态")
    @GetMapping("/query-order-status/{orderNo}")
    public R queryOrderStatus(@PathVariable("orderNo") String orderNo){
        String orderStatus = orderInfoService.getOrderStatus(orderNo);
        if (OrderStatus.SUCCESS.getType().equals(orderStatus)) {
            return R.ok().setMessage("支付成功");
        }
        return R.ok().setCode(101).setMessage("支付中......");
    }

}

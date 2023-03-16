package com.zy.wxpay.controller;

import com.zy.wxpay.restful.AppResponse;
import com.zy.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("wxPay")
@RestController
@Api(value = "微信支付相关接口", tags = "微信支付相关接口")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    @ApiOperation(value = "根据订单编号生成支付二维码")
    @GetMapping("createNative/{orderNo}")
    public AppResponse createNative(@PathVariable String orderNo){
        Map<String,Object> map= wxPayService.createNative(orderNo);
        return AppResponse.success("获取成功",map);
    }

    @GetMapping("queryPayStatus/{orderNo}")
    @ApiOperation(value = "根据订单编号查询支付状态")
    public AppResponse queryPayStatus(@PathVariable String orderNo) {
        //1调用微信接口查询支付状态
        Map<String, String> map = wxPayService.queryPayStatus(orderNo);
        //2判断支付状态
        if (map == null) {
            return AppResponse.bizError("支付出错");
        }
        if ("SUCCESS".equals(map.get("trade_state"))) {
            //3 支付成功后，更新订单状态，记录支付日志
            //TODO 业务逻辑处理
//            payLogService.updateOrderStatus(map);
            return AppResponse.success("支付成功");
        }
        return AppResponse.bizError("支付中");
    }

    @GetMapping("refund/{orderNo}")
    @ApiOperation(value = "根据订单编号申请退款")
    public AppResponse refund(@PathVariable String orderNo) {
        //1调用微信接口查询支付状态
        Map<String, String> map = wxPayService.refund(orderNo);
        //2判断支付状态
        if (map == null) {
            return AppResponse.bizError("支付出错");
        }
        if ("SUCCESS".equals(map.get("trade_state"))) {
            //3 支付成功后，更新订单状态，记录支付日志
            //TODO 业务逻辑处理
//            payLogService.updateOrderStatus(map);
            return AppResponse.success("支付成功");
        }
        return AppResponse.bizError("支付中");
    }



}

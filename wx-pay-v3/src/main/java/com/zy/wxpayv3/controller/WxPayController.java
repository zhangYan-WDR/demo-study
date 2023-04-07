package com.zy.wxpayv3.controller;

import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.zy.wxpayv3.service.WxPayService;
import com.zy.wxpayv3.util.HttpUtils;
import com.zy.wxpayv3.util.WechatPay2ValidatorForRequest;
import com.zy.wxpayv3.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags = "微信支付相关接口")
@Slf4j
public class WxPayController {

    @Resource
    private WxPayService wxPayService;

    @Resource
    private Verifier verifier;

    @ApiOperation("调用统一下单API，生成支付二维码")
    @PostMapping("/native/{productId}")
    public R nativePay(@PathVariable("productId") Long productId) throws Exception {
        log.info("发起支付请求");
        //返回支付二维码连接和订单号
        Map<String, Object> map = wxPayService.nativePay(productId);
        return R.ok().setData(map);
    }

    @PostMapping("/native/notify")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response) {

        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();//创建应答对象

        try {
            String body = HttpUtils.readData(request);
            Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
            String requestId = (String) bodyMap.get("id");
            log.info("支付回调通知的id==={}", requestId);
            //签名验证
            WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest = new WechatPay2ValidatorForRequest(verifier, requestId, body);
            if (!wechatPay2ValidatorForRequest.validate(request)) {
                log.error("通知验签失败");
                response.setStatus(500);
                map.put("code", "FAIL");
                map.put("message", "通知验签失败");
                return gson.toJson(map);
            }
            log.info("通知验签成功");
            //处理订单信息
            wxPayService.processOrder(bodyMap);

            response.setStatus(200);
            //现在返回成功不需要返回应答报文
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            //现在返回成功不需要返回应答报文
            map.put("code", "FAIL");
            map.put("message", "失败");
            return gson.toJson(map);
        }

    }

    @PostMapping("/cancel/{orderNo}")
    public R cancel(@PathVariable("orderNo") String orderNo) throws Exception {
        log.info("取消订单");
        wxPayService.cancelOrder(orderNo);
        return R.ok().setMessage("订单已取消");
    }

    @GetMapping("/query/{orderNo}")
    public R queryOrder(@PathVariable("orderNo") String orderNo) throws Exception {
        String result = wxPayService.queryOrder(orderNo);
        return R.ok().setMessage("查询成功").data("result", result);
    }

    @PostMapping("/refunds/{orderNo}/{reason}")
    public R refunds(@PathVariable("orderNo") String orderNo, @PathVariable("reason") String reason) throws Exception {
        log.info("退款申请 --->{}", orderNo);
        wxPayService.refund(orderNo, reason);
        return R.ok().setMessage("退款成功");
    }

    @GetMapping("/query-refunds/{refundNo}")
    public R queryRefund(@PathVariable("refundNo") String refundNo) throws Exception {
        log.info("查询退款订单 退款单编号为--->{}", refundNo);
        String result = wxPayService.queryRefund(refundNo);
        return R.ok().setMessage("查询退款成功").data("result", result);
    }

    @PostMapping("/refunds/notify")
    public String refundsNotify(HttpServletRequest request, HttpServletResponse response) {

        log.info("退款通知执行");
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();//创建应答对象

        try {
            String body = HttpUtils.readData(request);
            Map<String, Object> bodyMap = gson.fromJson(body, HashMap.class);
            String requestId = (String) bodyMap.get("id");
            log.info("支付回调通知的id===>{}", requestId);
            //签名验证
            WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest = new WechatPay2ValidatorForRequest(verifier, requestId, body);
            if (!wechatPay2ValidatorForRequest.validate(request)) {
                log.error("通知验签失败");
                response.setStatus(500);
                map.put("code", "FAIL");
                map.put("message", "通知验签失败");
                return gson.toJson(map);
            }
            log.info("通知验签成功");
            //处理退款单
            wxPayService.processRefund(bodyMap);

            response.setStatus(200);
            //现在返回成功不需要返回应答报文
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            //现在返回成功不需要返回应答报文
            map.put("code", "FAIL");
            map.put("message", "失败");
            return gson.toJson(map);
        }

    }

    @ApiOperation("获取账单url，测试用")
    @GetMapping("/querybill/{billDate}/{type}")
    public R queryTradeBill(@PathVariable("billDate") String billDate,@PathVariable("type") String type) throws Exception {
        log.info("获取账单url");
        String downloadUrl = wxPayService.queryBill(billDate, type);
        return R.ok().setMessage("查询成功").data("downloadUrl", downloadUrl);
    }
}

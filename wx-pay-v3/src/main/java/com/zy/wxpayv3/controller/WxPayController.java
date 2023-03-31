package com.zy.wxpayv3.controller;

import com.google.gson.Gson;
import com.zy.wxpayv3.service.WxPayService;
import com.zy.wxpayv3.util.HttpUtils;
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

    @ApiOperation("调用统一下单API，生成支付二维码")
    @PostMapping("/native/{productId}")
    public R nativePay(@PathVariable("productId") Long productId) throws Exception {
      log.info("发起支付请求");
      //返回支付二维码连接和订单号
      Map<String,Object> map = wxPayService.nativePay(productId);
      return R.ok().setData(map);
    }

    @PostMapping("/native/notify")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response){

        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();//创建应答对象

        String body = HttpUtils.readData(request);
        Map<String,Object> bodyMap = gson.fromJson(body, HashMap.class);
        log.info("支付回调通知的id==={}",bodyMap.get("id"));
        log.info("支付回调通知的完整报文为==={}",body);
        /**
         * {
         *     "id":"76388f27-4aa5-5496-905c-96959606dae2",
         *     "create_time":"2023-03-31T16:53:06+08:00",
         *     "resource_type":"encrypt-resource",
         *     "event_type":"TRANSACTION.SUCCESS",
         *     "summary":"支付成功",
         *     "resource":{
         *         "original_type":"transaction",
         *         "algorithm":"AEAD_AES_256_GCM",
         *         "ciphertext":"H7mcmifQ4RIMKy8jukcCB98uYgGoHGm5whLUbuBRDb1uTWpTW+8qhKEQgTBuEq5d49+IufuS0vTxBA29fGDOnD1DJH5PAuPniAsMWjsSNUrEV3u7BCdqPBF/R6IR7jTzNz0tU1CPMwphV1m89nxqSlXMOW0ZpFSydC2IOHBallRKK4wWODjsoJWG1bXYZHFWKcm3z7x858SBTJfYZHxhn8VHnzwsv/+sh8PsPxSjEZQD8aDSrQbjyajxeX02qimdMeiypjo+zJ4ELIbmtPSYm0go8VfGLjvJTxg77a3n/2Bv/2Dfu3wJ5IvZp+kaYOrzLh9Yh4HWLgnRQcmYrYlqoHryAWEfQ7yFuK3zqnCEjJC+P4ihBgKa29+ysoQAjJ3FkIIq8GkITeeSVhKfbbyZpHeEkjhfXcz7kDmhmBdkX//VBZAIndoqAIygf7/q4zRenaMg+FNrirnrIM2BmjWBJPiOXDzrEbfRFTDzx8mxUwrL3SvCIiTUxIAWNPrJUqlPXXne+dybdA75z6/Pg2IDzE2LMVoVlSFFxN3xuEdddB8Qz6t17/Lu8/7R38u02FkbWsvn+3t0Dw==",
         *         "associated_data":"transaction",
         *         "nonce":"x4GTK3C0VrH5"
         *     }
         * }
         */

        //TODO : 签名验证
        //TODO : 处理订单信息

        response.setStatus(200);
        //现在返回成功不需要返回应答报文
        map.put("code", "SUCCESS");
        map.put("message","成功");
        return gson.toJson(map);
    }

}

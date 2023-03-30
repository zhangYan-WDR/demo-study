package com.zy.wxpayv3.controller;

import com.zy.wxpayv3.service.WxPayService;
import com.zy.wxpayv3.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

}

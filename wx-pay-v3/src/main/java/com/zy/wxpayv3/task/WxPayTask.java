package com.zy.wxpayv3.task;

import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class WxPayTask {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private WxPayService wxPayService;
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void task1(){
//        log.info("task1 被执行。。。");
//    }

    /**
     * 从0秒开始,每30秒查询创建超过五分钟并且没有支付的订单
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm(){
        log.info("orderConfirm 被执行。。。");
        List<OrderInfo> orderInfoList = orderInfoService.getNoPayOrderByDuration(5);

        for (OrderInfo orderInfo : orderInfoList) {
            String orderNo = orderInfo.getOrderNo();
            log.warn("超时订单 === {}",orderNo);

            //向微信核实订单状态
            wxPayService.checkOrderStatus(orderNo);
        }

    }

}

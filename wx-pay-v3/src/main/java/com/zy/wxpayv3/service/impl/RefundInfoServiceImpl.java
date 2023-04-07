package com.zy.wxpayv3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.entity.RefundInfo;
import com.zy.wxpayv3.mapper.RefundInfoMapper;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.service.RefundInfoService;
import com.zy.wxpayv3.util.OrderNoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Resource
    private OrderInfoService orderInfoService;

    @Override
    public RefundInfo createRefundByOrderNo(String orderNo, String reason) {
        //查询订单信息
        OrderInfo orderInfo = orderInfoService.getOrderByOrderNo(orderNo);
        //创建新的退款单
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setRefundNo(OrderNoUtils.getRefundNo());
        refundInfo.setOrderNo(orderNo);
        refundInfo.setReason(reason);
        refundInfo.setTotalFee(orderInfo.getTotalFee());
        refundInfo.setRefund(orderInfo.getTotalFee());
        baseMapper.insert(refundInfo);

        return refundInfo;
    }

    @Override
    public void updateRefund(String content) {
        //将json转map对象
        Gson gson = new Gson();
        Map<String,String> resultMap = gson.fromJson(content, HashMap.class);

        //构造修改条件
        LambdaQueryWrapper<RefundInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundInfo::getRefundNo,resultMap.get("out_refund_no"));

        //修改要退款的字段
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setRefundId(resultMap.get("refund_id"));

        //查询退款和申请退款中的返回参数
        if (resultMap.get("status") != null) {
            refundInfo.setRefundStatus(resultMap.get("status"));
            refundInfo.setContentReturn(content);
        }
        //退款回调中的返回参数
        if (resultMap.get("refund_status") != null) {
            refundInfo.setRefundStatus(resultMap.get("refund_status"));
            refundInfo.setContentNotify(content);
        }

        baseMapper.update(refundInfo, queryWrapper);
    }
}

package com.zy.wxpayv3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.wxpayv3.entity.OrderInfo;
import com.zy.wxpayv3.entity.Product;
import com.zy.wxpayv3.enums.OrderStatus;
import com.zy.wxpayv3.mapper.OrderInfoMapper;
import com.zy.wxpayv3.mapper.ProductMapper;
import com.zy.wxpayv3.service.OrderInfoService;
import com.zy.wxpayv3.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public OrderInfo createOrderByProductId(Long productId) {

        OrderInfo orderInfo = getNoPayOrderByProductId(productId);
        if (orderInfo != null) {
            return orderInfo;
        }

        Product product = productMapper.selectById(productId);

        //生成订单
        orderInfo = new OrderInfo();
        orderInfo.setTitle(product.getTitle());
        orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo.setProductId(productId);
        orderInfo.setTotalFee(product.getPrice());
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
        //入库
        baseMapper.insert(orderInfo);

        return orderInfo;
    }

    @Override
    public void saveCodeUrl(String orderNo, String codeUrl) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCodeUrl(codeUrl);
        baseMapper.update(orderInfo, queryWrapper);
    }

    private OrderInfo getNoPayOrderByProductId(Long productId) {
        //防止一直重复生成订单
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getProductId, productId);
        queryWrapper.eq(OrderInfo::getOrderStatus, OrderStatus.NOTPAY.getType());
        //正式的项目中，应该还加一个判断条件是当前用户
        OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
        return orderInfo;
    }

    @Override
    public List<OrderInfo> listOrderByCreateTimeDesc() {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(OrderInfo::getCreateTime);
        List<OrderInfo> orderInfos = baseMapper.selectList(queryWrapper);
        return orderInfos;
    }

    /**
     * 根据订单号更新订单状态
     * @param orderNo
     * @param success
     */
    @Override
    public void updateStatusByOrderNo(String orderNo, OrderStatus orderStatus) {
        log.info("更新订单状态为--->{}",orderStatus.getType());
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderStatus(orderStatus.getType());
        baseMapper.update(orderInfo, queryWrapper);
    }

    @Override
    public String getOrderStatus(String orderNo) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
        if (orderInfo == null) {
            return null;
        }
        return orderInfo.getOrderStatus();
    }

    /**
     * 查询创建超过minutes分钟并且未支付的订单
     * @param minutes
     * @return
     */
    @Override
    public List<OrderInfo> getNoPayOrderByDuration(int minutes) {
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderStatus, OrderStatus.NOTPAY.getType());
        queryWrapper.le(OrderInfo::getCreateTime, instant);

        List<OrderInfo> orderInfos = baseMapper.selectList(queryWrapper);
        return orderInfos;
    }

    @Override
    public OrderInfo getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderNo,orderNo);
        OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
        return orderInfo;
    }
}

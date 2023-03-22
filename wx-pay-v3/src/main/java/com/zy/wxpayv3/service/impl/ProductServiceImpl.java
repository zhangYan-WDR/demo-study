package com.zy.wxpayv3.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.wxpayv3.entity.Product;
import com.zy.wxpayv3.mapper.ProductMapper;
import com.zy.wxpayv3.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}

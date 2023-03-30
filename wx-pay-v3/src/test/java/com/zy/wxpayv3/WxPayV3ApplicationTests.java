package com.zy.wxpayv3;

import com.zy.wxpayv3.config.WxPayConfig;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class WxPayV3ApplicationTests {


    @Resource
    private WxPayConfig wxPayConfig;
    /**
     * 测试商户的私钥
     */
//    @Test
//    void testGetPrivateKey() {
//
//        String privateKeyPath = wxPayConfig.getPrivateKeyPath();
//
//        PrivateKey privateKey = wxPayConfig.getPrivateKey(privateKeyPath);
//
//        System.out.println(privateKey);
//
//    }

}

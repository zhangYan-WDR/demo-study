package com.zy.redisson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/set_deduct_stock")
    public String setDeductStock(){
        stringRedisTemplate.opsForValue().set("stock", "300");
        return "success";
    }

    @RequestMapping("/deduct_stock")
    public String deductStock(){
        int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
        if (stock > 0) {
            int realStock = stock - 1;
            stringRedisTemplate.opsForValue().set("stock", realStock + "");
            System.out.println("扣减成功,剩余库存:"+realStock);
        }else {
            System.out.println("扣减失败,库存不足");
        }
        return "end";
    }

}

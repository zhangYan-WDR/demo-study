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

    /**
     * 原始版本，并发时会有超卖问题
     * @return
     */
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

    /**
     * 使用synchronized锁来解决并发问题，但是只适用于一台服务器
     * 如果使用nginx进行请求的分发，同一时间访问不同的服务器，tomcat
     * 还是会导致超卖的情况出现
     * @return
     */
    @RequestMapping("/deduct_stock_by_synchronized")
    public String deductStockBySynchronized(){
        synchronized (this) {
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                System.out.println("扣减成功,剩余库存:"+realStock);
            }else {
                System.out.println("扣减失败,库存不足");
            }
        }
        return "end";
    }

    /**
     * 使用redis的setNX方法可以让每个请求进来时都执行一遍
     * redis:setNX 只有在当前的key不存在是才会创建
     * redis对同一个key进行赋值时，后面就替换掉前面的
     * 但是setNX后面不会生效，因为生效方式只有在设置的key对应的value为null时才生效
     */
    @RequestMapping("/deduct_stock_by_redis")
    public String deductStockByRedis(){
        String lockKey = "lock:product:101";  //根据具体的业务场景设置一个lockKey
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "zz");   //想当于执行了setNX方法
        if (!result) {
            return "error";     //如果线程没有抢到这一把锁，就给前端返回错误，请重试
        }
        //开始执行业务逻辑
        int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
        if (stock > 0) {
            int realStock = stock - 1;
            stringRedisTemplate.opsForValue().set("stock", realStock + "");
            System.out.println("扣减成功,剩余库存:"+realStock);
        }else {
            System.out.println("扣减失败,库存不足");
        }
        //业务逻辑执行结束之后，需要删除这一把锁
        stringRedisTemplate.delete(lockKey);
        return "end";
    }

}

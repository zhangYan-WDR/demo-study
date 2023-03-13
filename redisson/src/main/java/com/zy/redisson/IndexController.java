package com.zy.redisson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    /**
     * 会出现的其他问题
     * 抛异常导致锁没释放，或者执行业务逻辑一半的时候服务宕机了，造成死锁
     * 可能还会有的问题：线程一加的锁，在线程一还没有执行完时时间到了，导致线程2获得了锁，在线程2还没有执行完时，线程一执行了删除锁的操作
     * 导致线程三拿到了锁，后续线程2又删掉了线程三的锁。。。。。。
     */
    @RequestMapping("/deduct_stock_by_other_error")
    public String deductStockByOtherError(){
        String lockKey = "lock:product:101";  //根据具体的业务场景设置一个lockKey
        //执行业务逻辑时服务宕机了造成的死锁，可以通过设置一个超时时间，一定时间没有释放时，自动释放锁，避免造成死锁
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "zz",10, TimeUnit.SECONDS);   //想当于执行了setNX方法
        if (!result) {
            return "error";     //如果线程没有抢到这一把锁，就给前端返回错误，请重试
        }
        try {   //抛异常导致锁没释放的死锁，可以使用tryfinally
            //开始执行业务逻辑
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                System.out.println("扣减成功,剩余库存:"+realStock);
            }else {
                System.out.println("扣减失败,库存不足");
            }
        }finally {
            //业务逻辑执行结束之后，需要删除这一把锁
            stringRedisTemplate.delete(lockKey);
        }
        return "end";
        //一般的小型不是特别特别高的并发就是已经够用了
    }

    /**
     * 解决别的线程可能会删除当前线程锁的问题
     */
    @RequestMapping("/deduct_stock_by_delete_lock")
    public String deductStockByDeleteLock(){
        String lockKey = "lock:product:101";  //根据具体的业务场景设置一个lockKey
        //添加一个uuid，在删除的时候先判断当前的是不是我的uuid对应的
        String clientId = UUID.randomUUID().toString();
        //执行业务逻辑时服务宕机了造成的死锁，可以通过设置一个超时时间，一定时间没有释放时，自动释放锁，避免造成死锁
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId,10, TimeUnit.SECONDS);   //想当于执行了setNX方法
        if (!result) {
            return "error";     //如果线程没有抢到这一把锁，就给前端返回错误，请重试
        }
        try {   //抛异常导致锁没释放的死锁，可以使用tryfinally
            //开始执行业务逻辑
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                System.out.println("扣减成功,剩余库存:"+realStock);
            }else {
                System.out.println("扣减失败,库存不足");
            }
        }finally {
            //业务逻辑执行结束之后，需要删除这一把锁
            //在删除的时候先判断是不是我自己加的锁;是我自己加的锁，删除，不是就不做任何操作
            if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))) {
                stringRedisTemplate.delete(lockKey);
            }
        }
        return "end";
        //解决了别的线程删除我的锁的问题，但是还存在的是超时时间到底设置多久比较合适（理论上来说不管设置多久都会又这个问题）
    }


}

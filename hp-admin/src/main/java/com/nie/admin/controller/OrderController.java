package com.nie.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nie.admin.pojo.Balance;
import com.nie.admin.pojo.GenderList;
import com.nie.admin.pojo.SectionList;
import com.nie.admin.service.IBalanceService;
import com.nie.admin.service.OrderService;
import com.nie.admin.tools.DateAndTimeUtils;
import com.nie.common.tools.Result;
import com.nie.common.tools.UserContest;
import com.nie.feign.client.CouponClient;
import com.nie.feign.dto.Orders;
import com.nie.feign.dto.PageDtoTwo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final StringRedisTemplate stringRedisTemplate;

    private final CouponClient couponClient;

    private final IBalanceService balanceService;

    @GetMapping("/orderSeven")
    public Result orderSeven() {
        ArrayList<Integer> peopleCounts = new ArrayList<>();
        String pastTime = null;
        for (int i = 1; i < 20; i++) {
            pastTime = DateAndTimeUtils.getPastDate(i);
            LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.like(Orders::getOStart, pastTime + "%");
            final int count = (int) orderService.count(lambdaQueryWrapper);
            peopleCounts.add(count);
        }
        return Result.success(peopleCounts);
    }

    @GetMapping("/orderGender")
    public Result orderGender() {
        List<GenderList> genderLists = orderService.getGenderNums();
        return Result.success(genderLists);
    }

    @GetMapping("/orderSection")
    public Result orderSection() {
        int day = 50;
        String pastDate = DateAndTimeUtils.getPastDate(day);
        String nowDate = DateAndTimeUtils.getNowDate();
        List<SectionList> nums = orderService.getSectionNums(pastDate, nowDate);
        System.out.println(nums);
        return Result.success(nums);
    }

    @PostMapping("/updateOrder")
    public Result updateOrder(@RequestBody Orders orders) {
        LambdaUpdateWrapper<Orders> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Orders::getOId, orders.getOId())
                .set(Orders::getORecord, orders.getORecord())
                .set(Orders::getODrug, orders.getODrug())
                .set(Orders::getOCheck, orders.getOCheck())
                .set(Orders::getOTotalPrice, orders.getOTotalPrice());
        orderService.update(lambdaUpdateWrapper);
        return Result.success();
    }

    @GetMapping("/orderPeopleByDid")
    public Result orderPeopleByDid(int dId) {
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getdId, dId);
        int count = (int) orderService.count(lambdaQueryWrapper);
        return Result.success(count);
    }

    @PostMapping("/findOrderByDid")
    public Result findOrderByDid(@RequestBody PageDtoTwo pageDtoTwo) {
        Page<Orders> page = new Page<>(pageDtoTwo.getPageNumber(), pageDtoTwo.getSize());
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getdId, pageDtoTwo.getDId());
        orderService.page(page);
        return Result.success(page);
    }

    @PostMapping("/findOrderFinish")
    public Result findOrderFinish(@RequestBody PageDtoTwo pageDtoTwo) {
        Page<Orders> page = new Page<>(pageDtoTwo.getPageNumber(), pageDtoTwo.getSize());
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getdId, UserContest.getUserId());
        orderService.page(page);
        return Result.success(page);
    }

    @GetMapping("/orderPeople")
    public Result ordPeople() {
        String total = stringRedisTemplate.opsForValue().get("hp:orderTotal");
        if (total == null) {
            LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(Orders::getOId);
            int count = (int) orderService.count(lambdaQueryWrapper);
            stringRedisTemplate.opsForValue().set("hp:orderTotal", String.valueOf(count));
            return Result.success(count);
        }

        return Result.success(total);
    }

    @GetMapping("/findOrderTime")
    public Result findOrderTime(String arId) {
        String hKey = "hp:workTime";

        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(hKey);
        if (map.isEmpty()) {
            map = new HashMap<>();
            map.put("mT1", "20");
            map.put("mT2", "20");
            map.put("mT3", "20");
            map.put("aT4", "20");
            map.put("aT5", "20");
            map.put("aT6", "20");

            stringRedisTemplate.opsForHash().putAll(hKey, map);
            log.info("hkey is empty");
        } else {
            log.info("hkey is not empty");
        }

        stringRedisTemplate.expireAt(hKey, reSetRedis());

        return Result.success(map);
    }

    @GetMapping("addOrder")
    public Result addOrder(int pId, int dId, String oStart, String arId) {
        boolean b = orderService.addOrder(pId, dId, oStart, arId);
        if (b) {
            log.info("挂号成功");
            updateRedis(oStart);
        }
        return Result.success("更新中");
    }

    @GetMapping("/updatePrice")
    public Result updatePrice(@RequestParam("oId") int oId, @RequestParam("couponIds") String couponIds) {
        final ArrayList<Integer> list = couponClient.useCoupon(couponIds);
        final Orders byId = orderService.getById(oId);
        if (list.size() > 0) {
            final double amount = list.stream().mapToDouble(x -> x).sum();
            double need = byId.getOTotalPrice() - amount;
            double balance = balanceService.getById(byId.getPId()).getUserBalance();
            LambdaUpdateWrapper<Balance> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Balance::getUserBalance, balance - need);
            balanceService.update(lambdaUpdateWrapper);
            log.info("已使用优惠劵");
            //优惠劵使用成功更新缓存

        } else {
            double balance = balanceService.getById(byId.getPId()).getUserBalance();
            LambdaUpdateWrapper<Balance> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Balance::getUserBalance, balance - byId.getOTotalPrice());
            balanceService.update(lambdaUpdateWrapper);
            log.info("未使用优惠劵");
        }
        LambdaUpdateWrapper<Orders> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Orders::getOId, oId)
                .set(Orders::getOPriceState, 1)
                .set(Orders::getOState, 1);
        orderService.update(lambdaUpdateWrapper);
        System.out.println(list);
        return Result.success();
    }

    public void updateRedis(String oStart) {
        String hKey = "hp:workTime";
        String period = oStart.split(" ")[1];
        String[] ks = {"mT1", "mT2", "mT3", "aT4", "aT5", "aT6"};
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(hKey);

        Map<String, Integer> periodMap = Map.of(
                "08:30-09:30", 0,
                "09:30-10:30", 1,
                "10:30-11:30", 2,
                "14:30-15:30", 3,
                "15:30-16:30", 4,
                "16:30-17:30", 5
        );

        if (periodMap.containsKey(period)) {
            int index = periodMap.get(period);
            String key = ks[index];

            Object value = entries.get(key);
            if (value != null) {
                int newValue = Integer.parseInt(value.toString()) - 1;
                entries.put(key, String.valueOf(newValue));
            }
        }

        stringRedisTemplate.opsForHash().putAll(hKey, entries);

    }

    public Date reSetRedis() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.withHour(23).withMinute(0).withSecond(0).withNano(0);
        if (now.isAfter(expireTime))
            expireTime.plusDays(1);
        long expireAtTimestamp = expireTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        Date expireDate = new Date(expireAtTimestamp * 1000);
        return expireDate;
    }
}

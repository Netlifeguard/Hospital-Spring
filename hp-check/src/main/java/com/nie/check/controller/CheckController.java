package com.nie.check.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nie.check.service.CheckService;
import com.nie.common.tools.Result;
import com.nie.feign.dto.Checks;
import com.nie.feign.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
public class CheckController {
    private final CheckService checkService;

    @PostMapping("/findAllChecks")
    public Result findAllOrders(@RequestBody PageDTO pageDTO) {
        Page<Checks> page = new Page<>(pageDTO.getPageNumber(), pageDTO.getSize());
        LambdaQueryWrapper<Checks> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(pageDTO.getQuery() != null && !pageDTO.getQuery().isEmpty(), Checks::getChName, pageDTO.getQuery());
        checkService.page(page, lambdaQueryWrapper);
        return Result.success(page);
    }

    @PostMapping("/addCheck")
    public void addCheck(@RequestBody Checks checks) {
        checkService.save(checks);
    }

    @DeleteMapping("/deleteCheck")
    public void deleteCheck(int chId) {
        checkService.removeById(chId);
    }

    @GetMapping("/findCheck")
    public Result findCheck(int chId) {
        Checks check = checkService.getById(chId);
        return Result.success(check);
    }

    @PostMapping("/modifyCheck")
    public void modifyCheck(@RequestBody Checks checks) {
//        LambdaUpdateWrapper<Checks> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(Checks::getChId,checks.getChId())
//                .set(Checks::getChName,checks.getChName())
//                .set(Checks::getChPrice,checks.getChPrice());
//        checkService.update(lambdaUpdateWrapper);
        checkService.updateById(checks);
    }
}

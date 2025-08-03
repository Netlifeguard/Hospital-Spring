package com.nie.check.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nie.check.service.DrugService;
import com.nie.common.tools.Result;
import com.nie.feign.dto.Drug;
import com.nie.feign.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/drug")
@RequiredArgsConstructor
public class DrugController {
    private final DrugService drugService;

    @PostMapping("/findAllDrugs")
    public Result findAllOrders(@RequestBody PageDTO pageDTO) {
        Page<Drug> page = new Page<>(pageDTO.getPageNumber(), pageDTO.getSize());
        LambdaQueryWrapper<Drug> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(pageDTO.getQuery() != null && !pageDTO.getQuery().isEmpty(), Drug::getDrName, pageDTO.getQuery());
        lambdaQueryWrapper.orderByDesc(Drug::getDrId);
        drugService.page(page, lambdaQueryWrapper);
        return Result.success(page);
    }

    @PostMapping("/addDrug")
    public void addCheck(@RequestBody Drug Drug) {
        drugService.save(Drug);
    }

    @DeleteMapping("/deleteDrug")
    public void deleteCheck(int drId) {
        drugService.removeById(drId);
    }

    @GetMapping("/findDrug")
    public Result findCheck(int drId) {
        Drug drug = drugService.getById(drId);
        return Result.success(drug);
    }

    @PostMapping("/modifyDrug")
    public void modifyCheck(@RequestBody Drug Drug) {
        drugService.updateById(Drug);
    }

    @GetMapping("/reduceDrugNumber")
    public Result reduceDrugNumber(int drId, int usedNumber) {
        LambdaUpdateWrapper<Drug> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Drug::getDrId, drId).setSql("dr_number = dr_number -" + usedNumber);
        boolean b = drugService.update(lambdaUpdateWrapper);
        if (b)
            return Result.success("药物已更新");
        return Result.success("失败");
    }
}

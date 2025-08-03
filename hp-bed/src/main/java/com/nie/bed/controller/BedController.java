package com.nie.bed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nie.bed.service.BedService;
import com.nie.common.tools.Result;
import com.nie.feign.dto.Bed;
import com.nie.feign.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bed")
@RequiredArgsConstructor
public class BedController {

    private final BedService bedService;

    @PostMapping("/findAllBeds")
    public Result findBeds(@RequestBody PageDTO pageDTO) {
        Page<Bed> pageInfo = new Page<>(pageDTO.getPageNumber(), pageDTO.getSize());
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(pageDTO.getQuery() != null && !pageDTO.getQuery().isEmpty(), Bed::getpId, pageDTO.getQuery());
        bedService.page(pageInfo, lambdaQueryWrapper);

        return Result.success(pageInfo);
    }

    @PostMapping("/addBed")
    Result addBed(@RequestBody Bed bed) {
        int i = bedService.addBed(bed);
        if (i < 0)
            return Result.error("新增失败");
        return Result.success("新增成功");
    }

    @PostMapping("/modifyBed")
    public void modifyBed(@RequestBody Bed bed) {
        bedService.updateById(bed);

    }

    @DeleteMapping("/deleteBed")
    void deleteBed(int bId) {
        bedService.removeById(bId);
    }

    @DeleteMapping("/emptyBed")
    void emptyBed(int bId) {
        bedService.removeById(bId);
    }

    @GetMapping("/findBed")
    public Result findBed(int bId) {
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<Bed>();
        lambdaQueryWrapper.eq(Bed::getBId, bId);
        Bed bed = bedService.getOne(lambdaQueryWrapper);
        return Result.success(bed);
    }

    @GetMapping("/findNullBed")
    public Result findNullBed() {
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Bed::getBState, 0);
        List<Bed> beds = bedService.list(lambdaQueryWrapper);
        return Result.success(beds);
    }

    @PostMapping("/updateBed")
    public Result updateBed(@RequestBody Bed bed) {
        boolean b = bedService.updateById(bed);
        if (b)
            return Result.success("已申请住院");
        return Result.success("操作失败");
    }

    @GetMapping("/bedPeople")
    public Result bedPeople() {
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Bed::getBState, 1);
        int count = bedService.count(lambdaQueryWrapper);
        return Result.success(count);
    }

    @GetMapping("/findBedByPid")
    public Result findBedByPid(int pId) {
        LambdaQueryWrapper<Bed> lambdaQueryWrapper = new LambdaQueryWrapper<Bed>();
        lambdaQueryWrapper.eq(Bed::getpId, pId);
        Bed bed = bedService.getOne(lambdaQueryWrapper);
        return Result.success(bed);
    }


}

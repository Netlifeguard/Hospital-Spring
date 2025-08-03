package com.nie.admin.controller;

import com.nie.admin.service.ArrangeService;
import com.nie.common.tools.Result;
import com.nie.feign.dto.Arrange;
import com.nie.feign.dto.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arrange")
@RequiredArgsConstructor
public class ArrangeController {
    private final ArrangeService arrangeService;

    @PostMapping("/addArrange")
    public Result addArrange(@RequestBody Arrange arrange) {
        System.out.println(arrange);
        if (arrangeService.isArranged(arrange.getArId())) {
            int i = arrangeService.insertArrange(arrange);
            if (i < 0) {
                return Result.error("排班已满");
            }
        }
        return Result.success();
    }

    @GetMapping("/findByTime")
    public Result findByTime(String arTime, String dSection) {
        List<Doctor> list = arrangeService.getArrangeList(arTime, dSection);
        return Result.success(list);
    }


}

package com.nie.netty.controller;

import com.nie.common.tools.Result;
import com.nie.feign.dto.MessageDTO;
import com.nie.netty.config.ESConfig;
import com.nie.netty.pojo.HitsData;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/es")
public class ESController {
    private ESConfig esConfig = new ESConfig();
    private static final RestHighLevelClient client = ESConfig.getClient();

    @GetMapping("/search")
    public Result getInfo(@RequestParam("userId") String userId) {
        HitsData hitsData = null;
        try {
            hitsData = esConfig.searchWithPrecise(client, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success(hitsData);
    }

    @DeleteMapping("/delete")
    public Result deleteRecords(@RequestParam("userId") String userId) {
        final String s = esConfig.deleteRecords2(client, userId);
        return Result.success(s);
    }

}

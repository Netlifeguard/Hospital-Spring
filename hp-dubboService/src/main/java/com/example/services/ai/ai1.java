package com.example.services.ai;

import com.example.ai.AiTest;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class ai1 implements AiTest {
    @Override
    public String test() {
        return "我是DeepSeek AI";
    }
}

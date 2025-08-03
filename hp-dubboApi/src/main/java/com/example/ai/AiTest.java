package com.example.ai;

public interface AiTest {
    public String test();

    default String test2() {
        return "我是你豆包";
    }
}

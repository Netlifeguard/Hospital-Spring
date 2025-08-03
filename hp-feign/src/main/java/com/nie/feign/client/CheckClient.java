package com.nie.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("hp-check")
public interface CheckClient {

}

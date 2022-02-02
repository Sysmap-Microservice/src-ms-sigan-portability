package com.sysmap.srcmssignportability.framework.interfaces.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "portability", url = "${portability-url}")
public interface PortabilityFeignClient {

    @GetMapping(value = "/callback")
    ResponseEntity<String> callback(@RequestParam("message")String message);
}

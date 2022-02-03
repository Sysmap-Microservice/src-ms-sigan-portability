package com.sysmap.srcmssignportability.framework.interfaces.client;

import com.sysmap.srcmssignportability.framework.adapters.in.dto.InputPutStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(value = "portability", url = "${portability-url}")
public interface PortabilityFeignClient {

    @PutMapping("/portability/{portabilityId}")
    ResponseEntity<String> putStatusPortability(@RequestBody InputPutStatus inputPutStatus,
                                              @PathVariable UUID portabilityId, @RequestParam("message") String message);
}

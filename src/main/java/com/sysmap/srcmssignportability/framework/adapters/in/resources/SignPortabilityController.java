package com.sysmap.srcmssignportability.framework.adapters.in.resources;

import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ms-src-sign-portability/v1")
public class SignPortabilityController {

    private final SignPortabilityService signPortabilityService;

    @Autowired
    public SignPortabilityController(SignPortabilityService signPortabilityService) {
        this.signPortabilityService = signPortabilityService;
    }

    @PostMapping
    public ResponseEntity<String> callback(){
        return ResponseEntity.ok(signPortabilityService.callback());
    }

}

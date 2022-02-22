package com.sysmap.srcmssignportability.framework.adapters.config;

import com.sysmap.srcmssignportability.SrcMsSignPortabilityApplication;
import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.application.service.SignPortabilityServiceImpl;
import com.sysmap.srcmssignportability.framework.interfaces.client.PortabilityFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SrcMsSignPortabilityApplication.class)
public class BeanConfiguration {

    @Bean
    SignPortabilityServiceImpl signPortabilityService(PortabilityRepository portabilityRepository, PortabilityFeignClient portabilityFeignClient) {
        return new SignPortabilityServiceImpl(portabilityRepository, portabilityFeignClient);
    }

}

package com.sysmap.srcmssignportability.application.ports.in;

import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import org.springframework.http.HttpStatus;

public interface SignPortabilityService {

    HttpStatus savePortabilityInfo(String messageKafka);
    StatusPortability getStatusPortability();

}

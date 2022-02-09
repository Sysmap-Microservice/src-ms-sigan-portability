package com.sysmap.srcmssignportability.application.ports.in;

import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;
import org.springframework.http.HttpStatus;

public interface SignPortabilityService {

    Boolean savePortabilityInfo(String messageKafka);
    StatusPortability getStatusPortability();
    PortabilityInputKafka preparePortabilityForSaving(String messageKafka);
}

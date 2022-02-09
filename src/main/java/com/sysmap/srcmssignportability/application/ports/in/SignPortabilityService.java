package com.sysmap.srcmssignportability.application.ports.in;

import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;

public interface SignPortabilityService {

    StatusPortability savePortabilityInfo(String messageKafka);
    StatusPortability getStatusPortability();
    PortabilityInputKafka preparePortabilityForSaving(String messageKafka);
}

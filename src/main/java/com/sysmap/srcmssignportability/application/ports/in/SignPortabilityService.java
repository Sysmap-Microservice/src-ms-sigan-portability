package com.sysmap.srcmssignportability.application.ports.in;

import com.sysmap.srcmssignportability.domain.enums.StatusPortability;

public interface SignPortabilityService {

    void savePortabilityInfo(String messageKafka);
    StatusPortability getStatusPortability();
}

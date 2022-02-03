package com.sysmap.srcmssignportability.application.ports.in;

import com.sysmap.srcmssignportability.domain.entities.Portability;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;

public interface SignPortabilityService {

    Portability savePortabilityInfo(String messageKafka);
    StatusPortability getStatusPortability();

}

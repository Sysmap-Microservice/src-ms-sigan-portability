package com.sysmap.srcmssignportability.application.ports.out;

import com.sysmap.srcmssignportability.domain.entities.Portability;

public interface PortabilityRepository {

    Portability savePortability(Portability portability);
}

package com.sysmap.srcmssignportability.application.service;

import com.google.gson.Gson;
import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;

public class SignPortabilityServiceImpl implements SignPortabilityService {

    StatusPortability statusPortability = StatusPortability.UNPORTED;

    @Override
    public void savePortabilityInfo(String messageKafka) {
        Gson gson = new Gson();
        PortabilityInputKafka portabilityInputKafka = gson.fromJson(messageKafka, PortabilityInputKafka.class);

        StatusPortability statusPortability = getStatusPortability(portabilityInputKafka);

    }

    private StatusPortability getStatusPortability(PortabilityInputKafka portabilityInputKafka) {

        if (portabilityInputKafka.getNumber().length() == 9
                && portabilityInputKafka.getPortability().getSource().equals(CellPhoneOperator.VIVO)
                && !portabilityInputKafka.getPortability().getTarget().equals(CellPhoneOperator.VIVO)) {
            statusPortability = StatusPortability.PORTED;
        }

        return statusPortability;
    }

    public StatusPortability getStatusPortability() {
        return this.statusPortability;
    }
}

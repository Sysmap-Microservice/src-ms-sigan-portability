package com.sysmap.srcmssignportability.application.service;

import com.google.gson.Gson;
import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;

public class SignPortabilityServiceImpl implements SignPortabilityService {

    @Override
    public void savePortabilityInfo(String messageKafka) {
        Gson gson = new Gson();
        PortabilityInputKafka portabilityInputKafka = gson.fromJson(messageKafka, PortabilityInputKafka.class);

        StatusPortability statusPortability = StatusPortability.UNPORTED;

        if (portabilityInputKafka.getNumber().length() == 9
                && portabilityInputKafka.getPortability().getSource() == CellPhoneOperator.VIVO
                && portabilityInputKafka.getPortability().getTarget() != CellPhoneOperator.VIVO) {
            statusPortability = StatusPortability.PORTED;
        }

    }

}

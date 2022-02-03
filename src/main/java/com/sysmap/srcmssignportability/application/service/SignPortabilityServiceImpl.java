package com.sysmap.srcmssignportability.application.service;

import com.google.gson.Gson;
import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.domain.entities.Portability;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignPortabilityServiceImpl implements SignPortabilityService {

    private final PortabilityRepository portabilityRepository;

    public SignPortabilityServiceImpl(PortabilityRepository portabilityRepository) {
        this.portabilityRepository = portabilityRepository;
    }

    @Override
    public void savePortabilityInfo(String messageKafka) {
        Gson gson = new Gson();

        var portabilityInputKafka = gson
                .fromJson(messageKafka, PortabilityInputKafka.class);

        var statusPortability = getStatusPortability(portabilityInputKafka);

        var request = Portability.builder()
                .documentNumber(portabilityInputKafka.getDocumentNumber())
                .number(portabilityInputKafka.getNumber())
                .target(portabilityInputKafka.getPortability().getTarget())
                .portabilityId(portabilityInputKafka.getPortability().getPortabilityId())
                .source(portabilityInputKafka.getPortability().getSource())
                .status(statusPortability)
                .build();

        portabilityRepository.savePortability(request);
    }

    private StatusPortability getStatusPortability(PortabilityInputKafka portabilityInputKafka) {
        StatusPortability statusPortability = StatusPortability.UNPORTED;

        if (portabilityInputKafka.getNumber().length() == 9
                && portabilityInputKafka.getPortability().getSource().equals(CellPhoneOperator.VIVO)
                && !portabilityInputKafka.getPortability().getTarget().equals(CellPhoneOperator.VIVO)) {
            statusPortability = StatusPortability.PORTED;
        }

        return statusPortability;
    }

}

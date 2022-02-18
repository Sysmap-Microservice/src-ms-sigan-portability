package com.sysmap.srcmssignportability.application.service;

import com.google.gson.Gson;
import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.domain.entities.Portability;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.domain.exceptions.CallbackNotFound;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.InputPutStatus;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;
import com.sysmap.srcmssignportability.framework.interfaces.client.PortabilityFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class SignPortabilityServiceImpl implements SignPortabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignPortabilityServiceImpl.class);
    private final PortabilityFeignClient portabilityFeignClient;
    private final PortabilityRepository portabilityRepository;
    private StatusPortability statusPortability = StatusPortability.UNPORTED;

    public SignPortabilityServiceImpl(PortabilityRepository portabilityRepository, PortabilityFeignClient portabilityFeignClient) {
        this.portabilityRepository = portabilityRepository;
        this.portabilityFeignClient = portabilityFeignClient;
    }

    @Override
    public void savePortabilityInfo(String messageKafka) {
        var portabilityInputKafka = getPortabilityInputKafkaFromMessageKafka(messageKafka);
        statusPortability = validateIfCanBePorted(portabilityInputKafka);

        var portability = Portability.builder()
                .documentNumber(portabilityInputKafka.getDocumentNumber())
                .number(portabilityInputKafka.getNumber())
                .target(portabilityInputKafka.getPortability().getTarget())
                .portabilityId(portabilityInputKafka.getPortability().getPortabilityId())
                .source(portabilityInputKafka.getPortability().getSource())
                .status(statusPortability)
                .build();

        portabilityRepository.savePortability(portability);
        callback(portability);
    }

    @Override
    public StatusPortability getStatusPortability() {
        return this.statusPortability;
    }

    private PortabilityInputKafka getPortabilityInputKafkaFromMessageKafka(String messageKafka) {
        var gson = new Gson();
        return gson.fromJson(messageKafka, PortabilityInputKafka.class);
    }

    private StatusPortability validateIfCanBePorted(PortabilityInputKafka portabilityInputKafka) {
        if (portabilityInputKafka.getNumber().length() == 9
                && portabilityInputKafka.getPortability().getSource().equals(CellPhoneOperator.VIVO)
                && !portabilityInputKafka.getPortability().getTarget().equals(CellPhoneOperator.VIVO)) {
            statusPortability = StatusPortability.PORTED;
        }
        return statusPortability;
    }

    public void callback(Portability portability) {
        var inputPutStatus = new InputPutStatus();
        inputPutStatus.setStatus(portability.getStatus());

        try {
            LOGGER.info("Enviando Callback.");
            var returned = portabilityFeignClient.putStatusPortability(inputPutStatus, portability.getPortabilityId());
            LOGGER.info("Callback de atualização do status enviado.");
            LOGGER.info(returned.getBody());

        } catch (Exception e) {
            LOGGER.error("Falha ao enviar um callback!");
            throw new CallbackNotFound("Falha ao enviar o callback de atualização do status!");
        }
    }
}

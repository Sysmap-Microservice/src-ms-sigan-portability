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
    public StatusPortability savePortabilityInfo(String messageKafka) {
        var portabilityInputKafka = preparePortabilityForSaving(messageKafka);
        var request = Portability.builder()
                .documentNumber(portabilityInputKafka.getDocumentNumber())
                .number(portabilityInputKafka.getNumber())
                .target(portabilityInputKafka.getPortability().getTarget())
                .portabilityId(portabilityInputKafka.getPortability().getPortabilityId())
                .source(portabilityInputKafka.getPortability().getSource())
                .status(statusPortability)
                .build();

        portabilityRepository.savePortability(request);
        callback(request);
        return this.statusPortability;
    }

    public PortabilityInputKafka preparePortabilityForSaving(String messageKafka) {
        Gson gson = new Gson();

        var portabilityInputKafka = gson
                .fromJson(messageKafka, PortabilityInputKafka.class);

        statusPortability = validateIfPorted(portabilityInputKafka);

        return portabilityInputKafka;
    }

    private StatusPortability validateIfPorted(PortabilityInputKafka portabilityInputKafka) {

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

    public void callback(Portability request) {

        InputPutStatus inputPutStatus = new InputPutStatus();
        inputPutStatus.setStatus(request.getStatus());

        String message = "SignPortability: A portabilidade foi concluida com sucesso!";
        if (portabilityFeignClient.putStatusPortability(inputPutStatus, request.getPortabilityId(), message) != null) {

            LOGGER.info("Enviando Callback.");
            var responseDefaultDto = portabilityFeignClient.putStatusPortability(inputPutStatus, request.getPortabilityId(), message).getBody();
            if (responseDefaultDto == null) {
                LOGGER.error("Falha ao enviar um callback!");
                throw new CallbackNotFound("Falha ao enviar um callback!");
            }
            LOGGER.info("Callback enviado.");
            LOGGER.info(responseDefaultDto);
        }
    }
}

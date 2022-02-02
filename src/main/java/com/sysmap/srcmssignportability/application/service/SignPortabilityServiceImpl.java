package com.sysmap.srcmssignportability.application.service;

import com.google.gson.Gson;
import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.domain.exceptions.PortabilityNotFound;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;
import com.sysmap.srcmssignportability.framework.interfaces.client.PortabilityFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignPortabilityServiceImpl implements SignPortabilityService {

    private static Logger logger = LoggerFactory.getLogger(SignPortabilityServiceImpl.class);

    @Autowired
    private PortabilityFeignClient portabilityFeignClient;

    @Override
    public void savePortabilityInfo(String messageKafka) {
        Gson gson = new Gson();
        PortabilityInputKafka portabilityInputKafka = gson.fromJson(messageKafka, PortabilityInputKafka.class);

        StatusPortability statusPortability = getStatusPortability(portabilityInputKafka);

    }

    private StatusPortability getStatusPortability(PortabilityInputKafka portabilityInputKafka) {
        StatusPortability statusPortability = StatusPortability.UNPORTED;

        if (portabilityInputKafka.getNumber().length() == 9
                && portabilityInputKafka.getPortability().getSource().equals(CellPhoneOperator.VIVO)
                && !portabilityInputKafka.getPortability().getTarget().equals(CellPhoneOperator.VIVO)) {
            statusPortability = StatusPortability.PORTED;
        }

        logger.info("Enviando Callback.");
        var responseDefaultDto = portabilityFeignClient.callback("SignPortability: A portabilidade foi concluida com sucesso!").getBody();
        if(responseDefaultDto.isEmpty()){
            logger.error("Falha ao enviar um callback!");
            throw new PortabilityNotFound("Falha ao enviar um callback!");
        }
        logger.info("Callback enviado.");
        logger.info(responseDefaultDto);

        return statusPortability;
    }
}

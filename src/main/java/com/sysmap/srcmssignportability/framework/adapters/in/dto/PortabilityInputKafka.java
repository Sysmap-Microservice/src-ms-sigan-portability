package com.sysmap.srcmssignportability.framework.adapters.in.dto;

import com.sysmap.srcmssignportability.domain.entities.Portability;
import lombok.Data;

@Data
public class PortabilityInputKafka {

    private String number;

    private String documentNumber;

    private Portability portability;

}

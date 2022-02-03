package com.sysmap.srcmssignportability.framework.adapters.in.dto;

import com.sysmap.srcmssignportability.domain.entities.Portability;
import lombok.Data;

@Data
public class PortabilityInputKafkaNewForKafkaTests {

    private String number;
    private String documentNumber;
    private Portability portability;

    public PortabilityInputKafkaNewForKafkaTests(String number, String documentNumber) {
        this.number = number;
        this.documentNumber = documentNumber;
    }

    public PortabilityInputKafkaNewForKafkaTests(String number, String documentNumber, Portability portability) {
        this.number = number;
        this.documentNumber = documentNumber;
        this.portability = portability;
    }
}

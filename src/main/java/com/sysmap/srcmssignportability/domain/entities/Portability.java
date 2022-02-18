package com.sysmap.srcmssignportability.domain.entities;

import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document
public class Portability {
    private UUID portabilityId;
    private CellPhoneOperator source;
    private CellPhoneOperator target;
    private String number;
    private String documentNumber;
    private StatusPortability status;
}

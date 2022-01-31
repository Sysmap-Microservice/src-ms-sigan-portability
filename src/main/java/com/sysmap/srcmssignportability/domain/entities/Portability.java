package com.sysmap.srcmssignportability.domain.entities;

import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import lombok.Data;

import java.util.UUID;

@Data
public class Portability {

    private UUID portabilityId;
    private CellPhoneOperator source;
    private CellPhoneOperator target;

}

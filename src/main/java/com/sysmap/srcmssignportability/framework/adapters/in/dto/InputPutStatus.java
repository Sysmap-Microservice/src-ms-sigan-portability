package com.sysmap.srcmssignportability.framework.adapters.in.dto;

import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputPutStatus {

    private StatusPortability status;
}

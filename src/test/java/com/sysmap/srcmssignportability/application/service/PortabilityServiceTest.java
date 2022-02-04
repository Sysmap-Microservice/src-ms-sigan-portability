package com.sysmap.srcmssignportability.application.service;

import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.domain.entities.Portability;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.InputPutStatus;
import com.sysmap.srcmssignportability.framework.interfaces.client.PortabilityFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PortabilityServiceTest {

    private SignPortabilityService signPortabilityService;
    private UUID portabilityId = UUID.fromString("b5e1a821-a637-4a3a-b207-01b9f09abc7a");

    @Mock
    private PortabilityRepository portabilityRepository;

    @Mock
    private PortabilityFeignClient portabilityFeignClient;

    @BeforeEach
    public void setup() {
        signPortabilityService = new SignPortabilityServiceImpl(portabilityRepository, portabilityFeignClient);
    }

    private Portability portabilityWrongStructure = Portability.builder()
            .status(StatusPortability.PORTED)
            .target(CellPhoneOperator.CLARO)
            .source(CellPhoneOperator.VIVO)
            .build();

    private Portability portabilityRightStructure = Portability.builder()
        .documentNumber("441558478995")
        .number("931313434")
        .target(CellPhoneOperator.VIVO)
        .portabilityId(portabilityId)
        .source(CellPhoneOperator.CLARO)
        .status(StatusPortability.UNPORTED)
        .build();
    
    @Test
    public void shouldNotSavePortabilityInfo() {
        when(portabilityRepository.savePortability(portabilityWrongStructure)).thenReturn(Mockito.any());
        HttpStatus httpStatus = signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), httpStatus.value());
    }

    @Test
    public void shouldSavePortabilityInfo() {
        when(portabilityRepository.savePortability(portabilityRightStructure)).thenReturn(Mockito.any());
        HttpStatus httpStatus = signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        assertEquals(HttpStatus.CREATED.value(), httpStatus.value());
    }

    @Test
    public void shouldNotSavePortabilityInfoWrongMessageKafka() {
        assertThrows(NullPointerException.class, () -> {
            signPortabilityService.savePortabilityInfo("{\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        });
    }

    @Test
    public void testRuleForTheGetTheStatusPortabilityWhenPassedWrongSource(){
        this.signPortabilityService.preparePortabilityForSaving("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        assertEquals("UNPORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedSourceEqualsToTarget(){
        this.signPortabilityService.preparePortabilityForSaving("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"VIVO\"}}");
        assertEquals("UNPORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedRightSourceAndRightTarget(){
        this.signPortabilityService.preparePortabilityForSaving("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"CLARO\"}}");
        assertEquals("PORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedRightSourceAndRightTargetAndWrongNumberSize(){
        this.signPortabilityService.preparePortabilityForSaving("{\"number\":\"931313434Y\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"CLARO\"}}");
        assertEquals("UNPORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedRightSourceAndRightTargetAndRightNumberSize(){
        this.signPortabilityService.preparePortabilityForSaving("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"CLARO\"}}");
        assertEquals("PORTED", this.signPortabilityService.getStatusPortability().toString());
    }
}

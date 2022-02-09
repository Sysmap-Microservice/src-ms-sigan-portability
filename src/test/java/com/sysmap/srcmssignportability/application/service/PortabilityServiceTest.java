package com.sysmap.srcmssignportability.application.service;

import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.domain.entities.Portability;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.HttpStatusSimulator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.interfaces.client.PortabilityFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PortabilityServiceTest {

    private SignPortabilityService signPortabilityService;
    private UUID portabilityId = UUID.fromString("b5e1a821-a637-4a3a-b207-01b9f09abc7a");
    private String validationForTests;

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

    private void insertIntoValidationForTestsCorrectValue(Boolean resp) {
        if (resp == true)
            this.validationForTests = HttpStatusSimulator.CREATED.toString();
        else
            this.validationForTests = HttpStatusSimulator.INTERNAL_SERVER_ERROR.toString();
    }

    @Test
    public void shouldNotSavePortabilityInfo() {
        when(portabilityRepository.savePortability(portabilityWrongStructure)).thenReturn(Mockito.any());
        Boolean resp = signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        insertIntoValidationForTestsCorrectValue(resp);
        assertEquals(this.validationForTests, HttpStatusSimulator.INTERNAL_SERVER_ERROR.toString());
    }

    @Test
    public void shouldSavePortabilityInfo() {
        when(portabilityRepository.savePortability(portabilityRightStructure)).thenReturn(Mockito.any());
        Boolean resp = signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        insertIntoValidationForTestsCorrectValue(resp);
        assertEquals(this.validationForTests, HttpStatusSimulator.CREATED.toString());
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

package com.sysmap.srcmssignportability.application.service;

import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.domain.enums.CellPhoneOperator;
import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.domain.exceptions.CallbackNotFound;
import com.sysmap.srcmssignportability.framework.interfaces.client.PortabilityFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SignPortabilityServiceTest {

    private SignPortabilityService signPortabilityService;

    @Mock
    private PortabilityRepository portabilityRepository;

    @Mock
    private PortabilityFeignClient portabilityFeignClient;

    @BeforeEach
    public void setup() {
        signPortabilityService = new SignPortabilityServiceImpl(portabilityRepository, portabilityFeignClient);
    }

    @Test
    public void shouldSavePortability() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenReturn(ResponseEntity.ok("Teste ok"));

        var message = getKafkaMessage("998765432", CellPhoneOperator.VIVO, CellPhoneOperator.OI);
        signPortabilityService.savePortabilityInfo(message);

        assertEquals(signPortabilityService.getStatusPortability(), StatusPortability.PORTED);
    }

    @Test
    public void shouldNotSavePortabilityBecauseNumberSizeBiggerThanNine() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenReturn(ResponseEntity.ok("Teste ok"));

        var message = getKafkaMessage("9987654320", CellPhoneOperator.VIVO, CellPhoneOperator.TIM);
        signPortabilityService.savePortabilityInfo(message);

        assertEquals(signPortabilityService.getStatusPortability(), StatusPortability.UNPORTED);
    }

    @Test
    public void shouldNotSavePortabilityBecauseNumberSizeLessThanNine() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenReturn(ResponseEntity.ok("Teste ok"));

        var message = getKafkaMessage("99876543", CellPhoneOperator.VIVO, CellPhoneOperator.TIM);
        signPortabilityService.savePortabilityInfo(message);

        assertEquals(signPortabilityService.getStatusPortability(), StatusPortability.UNPORTED);
    }

    @Test
    public void shouldNotSavePortabilityBecauseSourceIsNotVIVO() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenReturn(ResponseEntity.ok("Teste ok"));

        var message = getKafkaMessage("998765432", CellPhoneOperator.CLARO, CellPhoneOperator.NEXTEL);
        signPortabilityService.savePortabilityInfo(message);

        assertEquals(signPortabilityService.getStatusPortability(), StatusPortability.UNPORTED);
    }

    @Test
    public void shouldNotSavePortabilityBecauseTargetIsVIVO() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenReturn(ResponseEntity.ok("Teste ok"));

        var message = getKafkaMessage("998765432", CellPhoneOperator.CLARO, CellPhoneOperator.VIVO);
        signPortabilityService.savePortabilityInfo(message);

        assertEquals(signPortabilityService.getStatusPortability(), StatusPortability.UNPORTED);
    }

    @Test
    public void shouldNotSavePortabilityBecauseTargetAndSourceIsEquals() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenReturn(ResponseEntity.ok("Teste ok"));

        var message = getKafkaMessage("998765432", CellPhoneOperator.VIVO, CellPhoneOperator.VIVO);
        signPortabilityService.savePortabilityInfo(message);

        assertEquals(signPortabilityService.getStatusPortability(), StatusPortability.UNPORTED);
    }

    @Test
    public void shouldThrowCallbackNotFoundException() {
        when(portabilityFeignClient.putStatusPortability(any(), any(), any())).thenThrow(IllegalArgumentException.class);

        var message = getKafkaMessage("998765432", CellPhoneOperator.VIVO, CellPhoneOperator.OI);
        assertThrows(CallbackNotFound.class, () -> signPortabilityService.savePortabilityInfo(message));
    }

    private String getKafkaMessage(String phoneNumber, CellPhoneOperator source, CellPhoneOperator target) {
        return "{\"number\":\"" + phoneNumber + "\"," +
                "\"documentNumber\":\"441558478995\"," +
                "\"portability\":{" +
                "\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\"," +
                "\"source\":\"" + source + "\"," +
                "\"target\":\"" + target + "\"}}";
    }

}

package com.sysmap.srcmssignportability.application.service;

import com.sysmap.srcmssignportability.domain.enums.StatusPortability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class PortabilityServiceTest {

    SignPortabilityServiceImpl signPortabilityService = new SignPortabilityServiceImpl();

    @Test
    public void testRuleForTheGetTheStatusPortabilityWhenPassedWrongSource(){
        this.signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
        assertEquals("UNPORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedSourceEqualsToTarget(){
        this.signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"VIVO\"}}");
        assertEquals("UNPORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedRightSourceAndRightTarget(){
        this.signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"CLARO\"}}");
        assertEquals("PORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedRightSourceAndRightTargetAndWrongNumberSize(){
        this.signPortabilityService.savePortabilityInfo("{\"number\":\"931313434Y\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"CLARO\"}}");
        assertEquals("UNPORTED", this.signPortabilityService.getStatusPortability().toString());
    }

    @Test
    public void testRuleForTheGetStatusPortabilityWhenPassedRightSourceAndRightTargetAndRightNumberSize(){
        this.signPortabilityService.savePortabilityInfo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"VIVO\",\"target\":\"CLARO\"}}");
        assertEquals("PORTED", this.signPortabilityService.getStatusPortability().toString());
    }
}

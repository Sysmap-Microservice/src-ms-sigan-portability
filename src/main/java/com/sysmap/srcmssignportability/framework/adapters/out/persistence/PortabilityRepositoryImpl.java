package com.sysmap.srcmssignportability.framework.adapters.out.persistence;

import com.sysmap.srcmssignportability.application.ports.out.PortabilityRepository;
import com.sysmap.srcmssignportability.domain.entities.Portability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PortabilityRepositoryImpl implements PortabilityRepository {

    @Autowired
    PortabilityMongoRepository repository;

    @Override
    public Portability savePortability(Portability portability) {
        return repository.save(portability);
    }
}

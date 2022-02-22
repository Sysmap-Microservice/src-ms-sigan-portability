package com.sysmap.srcmssignportability.framework.adapters.out.persistence;

import com.sysmap.srcmssignportability.domain.entities.Portability;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PortabilityMongoRepository extends MongoRepository<Portability, UUID> {
}

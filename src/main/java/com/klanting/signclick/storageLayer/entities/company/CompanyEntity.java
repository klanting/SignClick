package com.klanting.signclick.storageLayer.entities.company;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    private UUID uuid;

    private int coins;

    @WhenCreated
    private Instant createdAt;

    public CompanyEntity(UUID uuid) {
        this.uuid = uuid;
    }

    // REQUIRED by Ebean
    public CompanyEntity() {}

    // getters / setters
}
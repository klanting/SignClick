package com.klanting.signclick.storageLayer.entities.company.research;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "researchOption")
public class ResearchOptionEntity implements com.klanting.signclick.storageLayer.entities.Entity {

    protected String name;

    @Id
    protected String stockName;

    // REQUIRED by Ebean
    public ResearchOptionEntity() {}

    @Override
    public Object getKey() {
        return null;
    }
}
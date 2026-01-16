package com.klanting.signclick.storageLayer.entities.company;

import com.klanting.signclick.logicLayer.companyLogic.LoggableSubject;
import com.klanting.signclick.logicLayer.companyLogic.research.Research;
import com.klanting.signclick.storageLayer.entities.company.research.ResearchEntity;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "company")
public class CompanyEntity {

    public String name;

    @Id
    public String stockName;

    // REQUIRED by Ebean
    public CompanyEntity() {}

    // getters / setters

    @OneToOne(mappedBy = "company")
    private ResearchEntity research;
}
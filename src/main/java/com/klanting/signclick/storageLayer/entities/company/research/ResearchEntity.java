package com.klanting.signclick.storageLayer.entities.company.research;

import com.klanting.signclick.logicLayer.companyLogic.Company;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.LoggableSubject;
import com.klanting.signclick.storageLayer.entities.company.CompanyEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "research")
public class ResearchEntity implements com.klanting.signclick.storageLayer.entities.Entity {

    @Id
    private String stockName;

    @OneToOne
    @MapsId
    @JoinColumn()
    private CompanyEntity company;

    // REQUIRED by Ebean
    public ResearchEntity() {}

    @Override
    public Object getKey() {
        return null;
    }

    // getters / setters
}
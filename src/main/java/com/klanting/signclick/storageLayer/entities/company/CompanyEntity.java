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
public class CompanyEntity implements com.klanting.signclick.storageLayer.entities.Entity {

    public String name;

    @Id
    public String stockName;

    public String type;

    /*
     * Represents the share base value
     * */
    public double shareBase = 0.0;

    /*
     * Represents the amount of money in the company back account
     * */
    public double bal = 0.0;

    public double shareBalance = 0.0;

    public double spendable = 0.0;

    public double lastValue = 0.0;



    @OneToOne(mappedBy = "company")
    private ResearchEntity research;

    // REQUIRED by Ebean
    public CompanyEntity() {}

    @Override
    public Object getKey() {
        return stockName;
    }
}
package com.klanting.signclick.storageLayer.entities.company.research;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import jakarta.persistence.*;
import org.bukkit.Material;

@Entity
@Table(name = "researchOption")
public class ResearchOptionEntity implements com.klanting.signclick.storageLayer.entities.Entity {

    @Id
    public long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "research_stockName")
    private ResearchEntity research;

    @Column(updatable = false)
    public Material material;

    @Column()
    public double progress;

    @Column()
    public int modifierIndex;


    // REQUIRED by Ebean
    public ResearchOptionEntity() {}

    @Override
    public Object getKey() {
        return id;
    }


}
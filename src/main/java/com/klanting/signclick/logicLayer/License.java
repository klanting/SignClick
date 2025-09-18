package com.klanting.signclick.logicLayer;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;

public class License extends Produceable{

    public CompanyI getFrom() {
        return from;
    }

    public CompanyI getTo() {
        return to;
    }

    public Product getProduct() {
        return product;
    }

    public Double getWeeklyCost() {
        return weeklyCost;
    }

    private CompanyI from;
    private CompanyI to;
    private Product product;

    private Double weeklyCost;

    public Double getCostIncrease() {
        return costIncrease;
    }

    public Double getRoyaltyFee() {
        return royaltyFee;
    }

    private Double costIncrease;
    private Double royaltyFee;

    public double frozenByLicenseCost = 0;
    public boolean isFrozenByLicenseCost() {
        return frozenByLicenseCost > 0;
    }

    public License(CompanyI from, CompanyI to, Product product, Double weeklyCost,
                   Double costIncrease, Double royaltyFee){
        this.from = from.getRef();
        this.to = to.getRef();
        this.product = product;
        this.weeklyCost = weeklyCost;
        this.costIncrease = costIncrease;
        this.royaltyFee = royaltyFee;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof License license){
            return (license.product.getMaterial().equals(getProduct().getMaterial()))
                    && (license.getFrom().equals(getFrom()))
                    && (license.getTo().equals(getTo()));
        }
        return false;
    }
}

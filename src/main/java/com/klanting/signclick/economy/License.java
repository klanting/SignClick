package com.klanting.signclick.economy;

public class License {

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

    public License(CompanyI from, CompanyI to, Product product, Double weeklyCost,
                   Double costIncrease, Double royaltyFee){
        this.from = from.getRef();
        this.to = to.getRef();
        this.product = product;
        this.weeklyCost = weeklyCost;
        this.costIncrease = costIncrease;
        this.royaltyFee = royaltyFee;
    }
}

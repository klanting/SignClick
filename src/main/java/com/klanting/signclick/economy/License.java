package com.klanting.signclick.economy;

public class License {

    public Company getFrom() {
        return from;
    }

    public Company getTo() {
        return to;
    }

    public Product getProduct() {
        return product;
    }

    public Double getWeeklyCost() {
        return weeklyCost;
    }

    private Company from;
    private Company to;
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

    public License(Company from, Company to, Product product, Double weeklyCost,
                   Double costIncrease, Double royaltyFee){
        this.from = from;
        this.to = to;
        this.product = product;
        this.weeklyCost = weeklyCost;
        this.costIncrease = costIncrease;
        this.royaltyFee = royaltyFee;
    }
}

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

    public Double getCost() {
        return cost;
    }

    private Company from;
    private Company to;
    private Product product;

    private Double cost;

    public License(Company from, Company to, Product product, Double cost){
        this.from = from;
        this.to = to;
        this.product = product;
        this.cost = cost;
    }
}

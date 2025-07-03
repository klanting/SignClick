package com.klanting.signclick.economy;

import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.contractRequests.ContractRequest;
import com.klanting.signclick.economy.logs.PluginLogs;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompanyRef implements CompanyI{

    private CompanyI comp;

    public CompanyRef(CompanyI comp){
        this.comp = comp;
    }

    @Override
    public Research getResearch() {
        return comp.getResearch();
    }

    @Override
    public boolean hasPendingContractRequest() {
        return comp.hasPendingContractRequest();
    }

    @Override
    public ContractRequest getPendingContractRequest() {
        return comp.getPendingContractRequest();
    }

    @Override
    public List<Product> getProducts() {
        return comp.getProducts();
    }

    @Override
    public void addProduct(Product product) {
        comp.addProduct(product);
    }

    @Override
    public CompanyOwnerManager getCOM() {
        return comp.getCOM();
    }

    @Override
    public double getSpendable() {
        return comp.getSpendable();
    }

    @Override
    public void setSpendable(double spendable) {
        comp.setSpendable(spendable);
    }

    @Override
    public void setMarketShares(Integer marketShares) {
        comp.setMarketShares(marketShares);
    }

    @Override
    public double getShareBalance() {
        return comp.getShareBalance();
    }

    @Override
    public Integer getTotalShares() {
        return comp.getTotalShares();
    }

    @Override
    public void setTotalShares(Integer totalShares) {
        comp.setTotalShares(totalShares);
    }

    @Override
    public double getValue() {
        return comp.getValue();
    }

    @Override
    public double getShareBase() {
        return comp.getShareBase();
    }

    @Override
    public double getBal() {
        return comp.getBal();
    }

    @Override
    public boolean removeBal(double amount) {
        return comp.removeBal(amount);
    }

    @Override
    public boolean removeBal(double amount, boolean skipSpendable) {
        return comp.removeBal(amount, skipSpendable);
    }

    @Override
    public boolean addBalNoPoint(double bal) {
        return comp.addBalNoPoint(bal);
    }

    @Override
    public void changeBase() {
        comp.changeBase();
    }

    @Override
    public double stockCompareGet() {
        return comp.stockCompareGet();
    }

    @Override
    public double stockCompare() {
        return comp.stockCompare();
    }

    @Override
    public Integer getMarketShares() {
        return comp.getMarketShares();
    }

    @Override
    public boolean addBal(double amount) {
        return comp.addBal(amount);
    }

    @Override
    public void supportUpdate(Account holder, UUID uuid) {
        comp.supportUpdate(holder, uuid);
    }

    @Override
    public void checkSupport() {
        comp.checkSupport();
    }

    @Override
    public void getShareTop(Player player) {
        comp.getShareTop(player);
    }

    @Override
    public void dividend() {
        comp.dividend();
    }

    @Override
    public void info(Player player) {
        comp.info(player);
    }

    @Override
    public void acceptOfferCompContract() {
        comp.acceptOfferCompContract();
    }

    @Override
    public void sendOfferCompContract(String stock_name, double amount, int weeks, String reason) {
        comp.sendOfferCompContract(stock_name, amount, weeks, reason);
    }

    @Override
    public void receiveOfferCompContract(String stock_name, double amount, int weeks, String reason) {
        comp.receiveOfferCompContract(stock_name, amount, weeks, reason);
    }

    @Override
    public void acceptOfferPlayerContract() {
        comp.acceptOfferPlayerContract();
    }

    @Override
    public void receiveOfferPlayerContract(String playerUUID, double amount, int weeks, String reason) {
        comp.receiveOfferPlayerContract(playerUUID, amount, weeks, reason);
    }

    @Override
    public boolean doUpgrade(Integer id) {
        return comp.doUpgrade(id);
    }

    @Override
    public void calculateCountry() {
        comp.calculateCountry();
    }

    @Override
    public String getCountry() {
        return comp.getCountry();
    }

    @Override
    public String getName() {
        return comp.getName();
    }

    @Override
    public String getStockName() {
        return comp.getStockName();
    }

    @Override
    public ArrayList<Upgrade> getUpgrades() {
        return comp.getUpgrades();
    }

    @Override
    public void update(String action, String message, UUID issuer) {
        comp.update(action, message, issuer);
    }

    @Override
    public String getType() {
        return comp.getType();
    }

    @Override
    public void addShareBal(Double amount) {
        comp.addShareBal(amount);
    }

    @Override
    public void removeShareBal(Double amount) {
        comp.removeShareBal(amount);
    }

    @Override
    public ArrayList<Patent> getPatent() {
        return comp.getPatent();
    }

    @Override
    public void addObserver(PluginLogs observer) {
        comp.addObserver(observer);
    }

    @Override
    public List<PluginLogs> getLogObservers() {
        return comp.getLogObservers();
    }

    @Override
    public void setType(String type) {
        comp.setType(type);
    }

    @Override
    public ArrayList<PatentUpgrade> getPatentUpgrades() {
        return comp.getPatentUpgrades();
    }

    @Override
    public HashMap<Block, Machine> getMachines() {
        return comp.getMachines();
    }

    @Override
    public String getPlayerNamePending() {
        return comp.getPlayerNamePending();
    }

    @Override
    public double getPlayerAmountPending() {
        return comp.getPlayerAmountPending();
    }

    @Override
    public int getPlayerWeeksPending() {
        return comp.getPlayerWeeksPending();
    }

    @Override
    public void setCountry(Country country) {
        comp.setCountry(country);
    }

    public CompanyI getRef(){
        return this;
    }
}

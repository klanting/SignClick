package com.klanting.signclick.economy;

import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.contractRequests.ContractRequest;
import com.klanting.signclick.economy.logs.PluginLogs;
import com.klanting.signclick.utils.BlockPosKey;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompanyRef implements CompanyI{

    private CompanyI comp;

    private transient String StockName = null;

    public CompanyRef(CompanyI comp){
        this.comp = comp;
    }

    public CompanyRef(String StockName){
        /*
        * Supporting lazy resolve
        * */

        this.StockName = StockName;
    }

    private void checkComp(){
        if(StockName != null){
            comp = Market.getCompany(StockName);
            if(comp != null){
                StockName = null;
            }
        }
    }

    private CompanyI getComp(){
        checkComp();
        return comp;
    }

    @Override
    public Research getResearch() {
        return getComp().getResearch();
    }

    @Override
    public boolean hasPendingContractRequest() {
        return getComp().hasPendingContractRequest();
    }

    @Override
    public ContractRequest getPendingContractRequest() {
        return getComp().getPendingContractRequest();
    }

    @Override
    public List<Product> getProducts() {
        return getComp().getProducts();
    }

    @Override
    public void addProduct(Product product) {
        comp.addProduct(product);
    }

    @Override
    public CompanyOwnerManager getCOM() {
        return getComp().getCOM();
    }

    @Override
    public double getSpendable() {
        return getComp().getSpendable();
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
        return getComp().getShareBalance();
    }

    @Override
    public Integer getTotalShares() {
        return getComp().getTotalShares();
    }

    @Override
    public void setTotalShares(Integer totalShares) {
        comp.setTotalShares(totalShares);
    }

    @Override
    public double getValue() {
        return getComp().getValue();
    }

    @Override
    public double getShareBase() {
        return getComp().getShareBase();
    }

    @Override
    public void removeBalVar(double amount) {
        getComp().removeBalVar(amount);
    }

    @Override
    public double getBal() {
        return getComp().getBal();
    }

    @Override
    public boolean removeBal(double amount) {
        return getComp().removeBal(amount);
    }

    @Override
    public boolean removeBal(double amount, boolean skipSpendable) {
        return getComp().removeBal(amount, skipSpendable);
    }

    @Override
    public boolean addBalNoPoint(double bal) {
        return getComp().addBalNoPoint(bal);
    }

    @Override
    public void changeBase() {
        comp.changeBase();
    }

    @Override
    public double stockCompareGet() {
        return getComp().stockCompareGet();
    }

    @Override
    public double stockCompare() {
        return getComp().stockCompare();
    }

    @Override
    public Integer getMarketShares() {
        return getComp().getMarketShares();
    }

    @Override
    public boolean addBal(double amount) {
        return getComp().addBal(amount);
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
        return getComp().doUpgrade(id);
    }

    @Override
    public void calculateCountry() {
        comp.calculateCountry();
    }

    @Override
    public String getCountry() {
        return getComp().getCountry();
    }

    @Override
    public String getName() {
        return getComp().getName();
    }

    @Override
    public String getStockName() {
        return getComp().getStockName();
    }

    @Override
    public ArrayList<Upgrade> getUpgrades() {
        return getComp().getUpgrades();
    }

    @Override
    public void update(String action, String message, UUID issuer) {
        comp.update(action, message, issuer);
    }

    @Override
    public String getType() {
        return getComp().getType();
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
        return getComp().getPatent();
    }

    @Override
    public void addObserver(PluginLogs observer) {
        comp.addObserver(observer);
    }

    @Override
    public List<PluginLogs> getLogObservers() {
        return getComp().getLogObservers();
    }

    @Override
    public void setType(String type) {
        comp.setType(type);
    }

    @Override
    public ArrayList<PatentUpgrade> getPatentUpgrades() {
        return getComp().getPatentUpgrades();
    }

    @Override
    public HashMap<BlockPosKey, Machine> getMachines() {
        return getComp().getMachines();
    }

    @Override
    public String getPlayerNamePending() {
        return getComp().getPlayerNamePending();
    }

    @Override
    public double getPlayerAmountPending() {
        return getComp().getPlayerAmountPending();
    }

    @Override
    public int getPlayerWeeksPending() {
        return getComp().getPlayerWeeksPending();
    }

    @Override
    public void setCountry(Country country) {
        comp.setCountry(country);
    }

    public CompanyI getRef(){
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompanyI companyI){
            return companyI.getStockName().equals(getStockName());
        }
        return false;
    }

    @Override
    public double getUpgradeModifier(){
        return this.comp.getUpgradeModifier();
    }

    @Override
    public void reCalcBalance() {
        comp.reCalcBalance();
    }
}

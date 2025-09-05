package com.klanting.signclick.migrations.mocks;

import com.klanting.signclick.economy.*;
import com.klanting.signclick.economy.companyPatent.Patent;
import com.klanting.signclick.economy.companyPatent.PatentUpgrade;
import com.klanting.signclick.economy.companyUpgrades.Upgrade;
import com.klanting.signclick.economy.contractRequests.ContractRequest;
import com.klanting.signclick.economy.logs.PluginLogs;
import com.klanting.signclick.utils.BlockPosKey;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompanyIv102v200 implements CompanyI {

    private final String type;

    public CompanyIv102v200(String type){
        this.type = type;
    }

    @Override
    public Research getResearch() {
        return null;
    }

    @Override
    public boolean hasPendingContractRequest() {
        return false;
    }

    @Override
    public ContractRequest getPendingContractRequest() {
        return null;
    }

    @Override
    public List<Product> getProducts() {
        return null;
    }

    @Override
    public void addProduct(Product product) {

    }

    @Override
    public CompanyOwnerManager getCOM() {
        return null;
    }

    @Override
    public double getSpendable() {
        return 0;
    }

    @Override
    public void setSpendable(double spendable) {

    }

    @Override
    public void setMarketShares(Integer marketShares) {

    }

    @Override
    public double getShareBalance() {
        return 0;
    }

    @Override
    public Integer getTotalShares() {
        return null;
    }

    @Override
    public void setTotalShares(Integer totalShares) {

    }

    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public double getShareBase() {
        return 0;
    }

    @Override
    public double getBal() {
        return 0;
    }

    @Override
    public boolean removeBal(double amount) {
        return false;
    }

    @Override
    public boolean removeBal(double amount, boolean skipSpendable) {
        return false;
    }

    @Override
    public boolean addBalNoPoint(double bal) {
        return false;
    }

    @Override
    public void changeBase() {

    }

    @Override
    public double stockCompareGet() {
        return 0;
    }

    @Override
    public double stockCompare() {
        return 0;
    }

    @Override
    public Integer getMarketShares() {
        return null;
    }

    @Override
    public boolean addBal(double amount) {
        return false;
    }

    @Override
    public void supportUpdate(Account holder, UUID uuid) {

    }

    @Override
    public void checkSupport() {

    }

    @Override
    public void getShareTop(Player player) {

    }

    @Override
    public void dividend() {

    }

    @Override
    public void info(Player player) {

    }

    @Override
    public void acceptOfferCompContract() {

    }

    @Override
    public void sendOfferCompContract(String stock_name, double amount, int weeks, String reason) {

    }

    @Override
    public void receiveOfferCompContract(String stock_name, double amount, int weeks, String reason) {

    }

    @Override
    public void acceptOfferPlayerContract() {

    }

    @Override
    public void receiveOfferPlayerContract(String playerUUID, double amount, int weeks, String reason) {

    }

    @Override
    public boolean doUpgrade(Integer id) {
        return false;
    }

    @Override
    public void calculateCountry() {

    }

    @Override
    public String getCountry() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStockName() {
        return null;
    }

    @Override
    public ArrayList<Upgrade> getUpgrades() {
        return null;
    }

    @Override
    public void update(String action, String message, UUID issuer) {

    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void addShareBal(Double amount) {

    }

    @Override
    public void removeShareBal(Double amount) {

    }

    @Override
    public ArrayList<Patent> getPatent() {
        return null;
    }

    @Override
    public void addObserver(PluginLogs observer) {

    }

    @Override
    public List<PluginLogs> getLogObservers() {
        return null;
    }

    @Override
    public void setType(String type) {

    }

    @Override
    public ArrayList<PatentUpgrade> getPatentUpgrades() {
        return null;
    }

    @Override
    public HashMap<BlockPosKey, Machine> getMachines() {
        return null;
    }

    @Override
    public String getPlayerNamePending() {
        return null;
    }

    @Override
    public double getPlayerAmountPending() {
        return 0;
    }

    @Override
    public int getPlayerWeeksPending() {
        return 0;
    }

    @Override
    public void setCountry(Country country) {

    }

    @Override
    public CompanyI getRef() {
        return null;
    }

    @Override
    public double getUpgradeModifier() {
        return 0;
    }

    @Override
    public void reCalcBalance() {

    }
}

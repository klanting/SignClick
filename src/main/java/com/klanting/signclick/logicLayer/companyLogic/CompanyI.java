package com.klanting.signclick.logicLayer.companyLogic;

import com.klanting.signclick.logicLayer.companyLogic.patent.Patent;
import com.klanting.signclick.logicLayer.companyLogic.patent.PatentUpgrade;
import com.klanting.signclick.logicLayer.companyLogic.producible.Product;
import com.klanting.signclick.logicLayer.companyLogic.research.Research;
import com.klanting.signclick.logicLayer.companyLogic.upgrades.Upgrade;
import com.klanting.signclick.logicLayer.companyLogic.contractRequests.ContractRequest;
import com.klanting.signclick.logicLayer.countryLogic.Country;
import com.klanting.signclick.logicLayer.companyLogic.logs.PluginLogs;
import com.klanting.signclick.utils.BlockPosKey;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import org.bukkit.entity.Player;

import java.util.*;

@ClassFlush
public interface CompanyI {
    Research getResearch();
    boolean hasPendingContractRequest();
    ContractRequest getPendingContractRequest();
    List<Product> getProducts();
    void addProduct(Product product);
    CompanyOwnerManager getCOM();
    double getSpendable();
    void setSpendable(double spendable);
    void setMarketShares(Integer marketShares);
    double getShareBalance();
    Integer getTotalShares();
    void setTotalShares(Integer totalShares);
    double getValue();
    double getShareBase();
    double getShareBase(boolean disableMin);
    void removeBalVar(double amount);
    double getBal();
    boolean removeBal(double amount);
    boolean removeBal(double amount, boolean skipSpendable);
    boolean addBalNoPoint(double bal);
    void changeBase();
    double stockCompareGet();
    double stockCompare();
    Integer getMarketShares();
    boolean addBal(double amount);
    void supportUpdate(Account holder, UUID uuid);
    void checkSupport();
    void getShareTop(Player player);
    void dividend();
    void info(Player player);
    void acceptOfferCompContract();
    void sendOfferCompContract(String stock_name, double amount, int weeks, String reason);
    void receiveOfferCompContract(String stock_name, double amount, int weeks, String reason);
    void acceptOfferPlayerContract();
    void receiveOfferPlayerContract(String playerUUID, double amount, int weeks, String reason);
    boolean doUpgrade(Integer id);
    void calculateCountry();
    String getCountry();
    String getName();
    String getStockName();
    List<Upgrade> getUpgrades();
    void update(String action, Object message, UUID issuer);
    String getType();
    void addShareBal(Double amount);
    void removeShareBal(Double amount);
    List<Patent> getPatent();
    void addObserver(PluginLogs observer);
    List<PluginLogs> getLogObservers();
    void setType(String type);
    List<PatentUpgrade> getPatentUpgrades();
    Map<BlockPosKey, Machine> getMachines();
    void setCountry(Country country);
    CompanyI getRef();

    double getUpgradeModifier();
    void reCalcBalance();
}

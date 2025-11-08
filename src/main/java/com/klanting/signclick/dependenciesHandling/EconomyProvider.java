package com.klanting.signclick.dependenciesHandling;

import com.klanting.signclick.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EconomyProvider implements Economy {
    /*
    * Own economy provider as fallback when no providers are provided to offer as a vault alternative
    * */

    private static Map<UUID, Double> balance = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "SignClick Internal Provider";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(v);
    }

    @Override
    public String currencyNamePlural() {
        return "SCoins";
    }

    @Override
    public String currencyNameSingular() {
        return "SCoin";
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return hasAccount(offlinePlayer);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {

        if (offlinePlayer == null){
            return false;
        }

        if (balance.containsKey(offlinePlayer.getUniqueId())){
            return true;
        }

        createPlayerAccount(offlinePlayer);
        return true;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return getBalance(offlinePlayer);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        assert hasAccount(offlinePlayer);
        return balance.get(offlinePlayer.getUniqueId());
    }

    @Override
    @Deprecated
    public double getBalance(String playerName, String s1) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return getBalance(offlinePlayer);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        assert hasAccount(offlinePlayer);
        return balance.get(offlinePlayer.getUniqueId());
    }

    @Override
    @Deprecated
    public boolean has(String playerName, double v) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return has(offlinePlayer, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        assert hasAccount(offlinePlayer);
        return balance.get(offlinePlayer.getUniqueId()) >= v;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, String s1, double v) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return has(offlinePlayer, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        assert hasAccount(offlinePlayer);
        return has(offlinePlayer, v);
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double v) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        if (has(offlinePlayer, v)){
            balance.put(offlinePlayer.getUniqueId(), getBalance(offlinePlayer)-v);
        }
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String s1, double v) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double v) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        assert hasAccount(offlinePlayer);
        balance.put(offlinePlayer.getUniqueId(), getBalance(offlinePlayer)+v);
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String s1, double v) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return createPlayerAccount(offlinePlayer);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        balance.put(offlinePlayer.getUniqueId(), 0.0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String s1) {
        OfflinePlayer offlinePlayer =  Bukkit.getOfflinePlayer(playerName);
        return createPlayerAccount(offlinePlayer);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }

    public void save(){
        Utils.writeSave("balance", this);
    }
}

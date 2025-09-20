package com.klanting.signclick.logicLayer.companyLogic;

import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class CompanyOwnerManager {
    /*
    * CompanyOwnerManager will manage the ownership and share control of the company
    * */

    private final Map<UUID, UUID> support = new HashMap<>();

    private final Map<UUID, Integer> shareHolders = new HashMap<>();

    private final List<UUID> employees = new ArrayList<>();

    public void fixBoard(){
        if (board != null){
            board.setCompanyOwnerManager(this);
        }

    }

    private boolean openTrade = false;

    private Integer marketShares = 0;

    private Integer totalShares = SignClick.getConfigManager().getConfig("companies.yml").getInt("companyStartShares");

    private Board board;

    public Board getBoard(){
        return board;
    }

    public boolean isOpenTrade() {
        return openTrade;
    }

    public void setOpenTrade(boolean openTrade) {
        this.openTrade = openTrade;
    }


    public Integer getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(Integer totalShares) {
        this.totalShares = totalShares;
    }

    public Integer getMarketShares() {
        return marketShares;
    }

    public void setMarketShares(Integer marketShares) {
        this.marketShares = marketShares;
    }

    public void addSupport(UUID key, UUID target) {
        support.put(key, target);
    }

    public UUID getSupport(UUID target) {
        return support.getOrDefault(target, null);
    }

    public Map<UUID, Integer> getShareHolders() {
        return shareHolders;
    }

    public CompanyOwnerManager(){

    }


    public CompanyOwnerManager(UUID owner){
        support.put(owner, owner);
        shareHolders.put(owner, totalShares);

        board = new Board(this);
    }

    public void changeShareHolder(Account holder, Integer amount){
        if (shareHolders.getOrDefault(holder.getUuid(), null) != null){
            Integer am = shareHolders.get(holder.getUuid());
            shareHolders.put(holder.getUuid(), am+amount);

        }else {
            shareHolders.put(holder.getUuid(), amount);
            support.put(holder.getUuid(), null);
        }

        if (shareHolders.getOrDefault(holder.getUuid(), 0) == 0){
            shareHolders.remove(holder.getUuid());
            support.remove(holder.getUuid());
        }
    }

    public void checkOwnerSupport(){
        getBoard().checkChiefVote();


    }

    public Boolean isOwner(UUID uuid){
        return getBoard().getChief("CEO").equals(uuid);
    }

    public void sendOwner(String message){
        Player p = Bukkit.getPlayer(getBoard().getChief("CEO"));
        if (p != null){
            p.sendMessage(message);
        }
    }

    public void getShareTop(Player player){

        ArrayList<Map.Entry<UUID, Integer>> entries = new ArrayList<>(shareHolders.entrySet());

        entries.sort(Comparator.comparing(item -> -item.getValue()));


        player.sendMessage("§bsharetop:");

        DecimalFormat df = new DecimalFormat("###,###,###");
        DecimalFormat df2 = new DecimalFormat("0.00");
        for (int i = 0; i < entries.size(); i++) {
            UUID uuid = entries.get(i).getKey();
            Integer value = entries.get(i).getValue();
            player.sendMessage("§9"+Bukkit.getOfflinePlayer(uuid).getName()+": §f"+df.format(value)+
                    " ("+df2.format(value/totalShares.doubleValue()*100.0)+"%)");
        }

        if (openTrade){
            player.sendMessage("§eMarket: §f"+"inf"+" ("+"inf"+"%)");
        }else{
            player.sendMessage("§eMarket: §f"+df.format(marketShares)+" ("+df2.format(marketShares/totalShares.doubleValue()*100.0)+"%)");
        }
    }

    public boolean isEmployee(UUID uuid){

        return employees.contains(uuid) || getBoard().isCurrentChief(uuid);
    }

    public List<UUID> getEmployees() {
        return employees;
    }

    public void addEmployee(UUID uuid){
        employees.add(uuid);
    }

    public void removeEmployee(UUID uuid){
        employees.remove(uuid);
    }

}

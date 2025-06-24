package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class CompanyOwnerManager {
    /*
    * CompanyOwnerManager will manage the ownership and share control of the company
    * */

    private ArrayList<UUID> owners = new ArrayList<>();

    private final Map<UUID, UUID> support = new HashMap<>();

    private final Map<UUID, Integer> shareHolders = new HashMap<>();

    public void fixBoard(){
        if (board != null){
            board.setCompanyOwnerManager(this);
        }

    }

    private boolean openTrade = false;

    private Integer marketShares = 0;

    private Integer totalShares = SignClick.getPlugin().getConfig().getInt("companyStartShares");

    private Board board;

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

    public ArrayList<UUID> getOwners() {
        return owners;
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

    public void testAddOwner(UUID uuid){
        /*
         * Test method to inject an owner
         * */
        owners.add(uuid);
    }

    public void checkOwnerSupport(){
        double neutral = 0.0;

        Map<UUID, Integer> s_dict = new HashMap<>();

        int highest = 0;
        UUID highest_uuid = null;

        for(Map.Entry<UUID, UUID> entry : support.entrySet()){
            UUID k = entry.getKey();
            UUID v = entry.getValue();

            Integer impact = shareHolders.getOrDefault(k, 0);
            if (v == null){
                neutral +=impact;
            }else{
                Integer bef = s_dict.getOrDefault(v, 0);
                s_dict.put(v, bef+impact);

                if (bef+impact > highest){
                    highest = bef+impact;
                    highest_uuid = v;
                }
            }

        }

        neutral = neutral/totalShares.doubleValue();
        ArrayList<UUID> new_owners = new ArrayList<UUID>();
        for(Map.Entry<UUID, Integer> entry : s_dict.entrySet()){
            UUID k = entry.getKey();
            double v = entry.getValue();

            v = v/totalShares.doubleValue();

            if (v >= 0.45){
                new_owners.add(k);
            }else if ((owners.contains(k)) & (v+neutral >= 0.5)){
                new_owners.add(k);
            }
        }
        if (new_owners.size() != 0){
            owners = new_owners;
        }else if (highest_uuid != null){
            new_owners.add(highest_uuid);
            owners = new_owners;
        }


    }

    public Boolean isOwner(UUID uuid){
        return owners.contains(uuid);
    }

    public void sendOwner(String message){
        for (int i = 0; i < owners.size(); i++){
            Player p = Bukkit.getPlayer(owners.get(i));
            if (p != null){
                p.sendMessage(message);
            }

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

}

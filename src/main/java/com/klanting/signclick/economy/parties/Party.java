package com.klanting.signclick.economy.parties;

import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    public List<UUID> owners = new ArrayList<>();
    public List<UUID> members = new ArrayList<>();
    public String name;
    public String country;
    public double PCT = 0.0;
    public Party(String party_name, String country, UUID owner){
        name = party_name;
        owners.add(owner);
        this.country = country;
    }

    public Party(String name, String country, double PCT, List<UUID> owners, List<UUID> members){
        this.name = name;
        this.country = country;
        this.PCT = PCT;
        this.owners = owners;
        this.members = members;

    }

    public boolean inParty(UUID uuid){
        return members.contains(uuid) || owners.contains(uuid);
    }

    public boolean isOwner(UUID uuid){
        return owners.contains(uuid);
    }

    public void addMember(UUID uuid){
        if (!members.contains(uuid)){
            members.add(uuid);
        }
    }

    public void removeMember(UUID uuid){
        if (members.contains(uuid)){
            members.remove(uuid);
        }
    }

    public void promote(UUID uuid){
        if (members.contains(uuid)){
            members.remove(uuid);
            owners.add(uuid);
        }
    }

    public void demote(UUID uuid){
        if (owners.contains(uuid)){
            owners.remove(uuid);
            members.add(uuid);
        }
    }

    public void info(Player player){

        List<String> o_list = new ArrayList<String>();
        for (UUID p: owners){
            o_list.add(Bukkit.getOfflinePlayer(p).getName());
        }

        List<String> m_list = new ArrayList<String>();
        for (UUID p: members){
            m_list.add(Bukkit.getOfflinePlayer(p).getName());
        }

        DecimalFormat df = new DecimalFormat("##0.00");
        player.sendMessage("§bName: §7"+name+"\n"
                              +"§bVotes: "+df.format(PCT*100.0)+"%\n"
                              +"§bOwners: "+o_list+"\n"
                              +"§bMembers: "+m_list+"\n");

    }

    public void Save(){
        String path = "parties." + country+"."+name+".";

        SignClick.getPlugin().getConfig().set(path+"PCT", PCT);

        List<String> m_list = new ArrayList<String>();
        for (UUID uuid: members){
            m_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set(path+"members", m_list);

        List<String> o_list = new ArrayList<String>();
        for (UUID uuid: owners){
            o_list.add(uuid.toString());
        }

        SignClick.getPlugin().getConfig().set(path+"owners", o_list);


    }
}

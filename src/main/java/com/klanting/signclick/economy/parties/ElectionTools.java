package com.klanting.signclick.economy.parties;

import com.klanting.signclick.economy.Country;
import com.klanting.signclick.economy.decisions.Decision;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class ElectionTools {
    public static void setupElectionDeadline(Country country, long time){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SignClick.getPlugin(), new Runnable() {
            public void run() {
                Election election = country.getCountryElection();
                country.setCountryElection(null);

                double total = 0.0;
                for (float f : election.vote_dict.values()) {
                    total += f;
                }

                if (total == 0.0){
                    return;
                }


                for (Party p: country.getParties()){
                    double pct = (double) election.vote_dict.getOrDefault(p.name, 0)/total;
                    p.PCT = pct;
                }

                double highest_pct = -0.1;
                Party highest_party = null;

                for (Party p: country.getParties()){
                    double pct = (double) election.vote_dict.getOrDefault(p.name, 0)/total;
                    p.PCT = pct;

                    if (pct > highest_pct){
                        highest_pct = pct;
                        highest_party = p;
                    }
                }

                if (highest_party != country.getRuling()){
                    double base = 2.0*(1.0- country.getPolicyBonus(2, 8));
                    country.addStability(-base);
                }


                List<UUID> old_owners = country.getOwners();
                for (UUID uuid: old_owners){
                    country.removeOwner(uuid);
                    country.addMember(uuid);

                }

                for (UUID uuid: highest_party.owners){
                    country.removeMember(uuid);
                    country.addOwner(uuid);

                }

                for (Decision d: country.getDecisions()){
                    d.checkApprove();
                }

            }
        }, time);

    }
}

package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;

import java.time.Duration;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Research {

    public List<ResearchOption> getResearchOptions() {
        return researchOptions.values().stream().toList();
    }

    private final Map<Material, ResearchOption> researchOptions = new HashMap<>();

    private long lastChecked;

    public Research(String companyType){

        lastChecked = getServer().getCurrentTick();

        Set<String> researchItems = SignClick.getPlugin().getConfig().getConfigurationSection("products").
                getConfigurationSection(companyType).getKeys(false);

        for (String researchItem: researchItems){
            Material m = Material.valueOf(researchItem);
            researchOptions.put(m, new ResearchOption(companyType, m));
        }

    }

    public ResearchOption get(Material material){
        return researchOptions.get(material);
    }

    public void checkProgress(Company company){
        long now = getServer().getCurrentTick();
        long delta = (now-lastChecked)/20;
        lastChecked = now;

        for (Map.Entry<Material, ResearchOption> researchOption: researchOptions.entrySet()){

            long realDelta = Math.min(Math.min(researchOption.getValue().canPayDelta(Math.min(company.getBal(), company.getSpendable())), delta),
                    (long) (researchOption.getValue().getCompleteTime()*(1-researchOption.getValue().getProgress())));
            company.removeBal(researchOption.getValue().getCost(realDelta));

            if (!researchOption.getValue().checkProgress(realDelta)){
                continue;
            }
            company.addProduct(ProductFactory.create(researchOption.getKey()));
        }
    }
}

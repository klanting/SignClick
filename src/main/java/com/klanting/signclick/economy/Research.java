package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Research {

    public List<ResearchOption> getResearchOptions() {
        return researchOptions;
    }

    private final List<ResearchOption> researchOptions = new ArrayList<>();

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }

    private long lastChecked;

    public Research(String companyType){

        lastChecked = getServer().getCurrentTick();

        ConfigurationSection productsSection = SignClick.getPlugin().getConfig().getConfigurationSection("products").
                getConfigurationSection(companyType);

        List<String> researchItems = new ArrayList<>(productsSection.getKeys(false).stream().toList());

        researchItems.sort(Comparator.comparingInt(s -> productsSection.getConfigurationSection(s).getInt("index")));

        for (String researchItem: researchItems){
            Material m = Material.valueOf(researchItem);
            researchOptions.add(new ResearchOption(companyType, m));
        }

    }

    public void checkProgress(Company company){
        long now = getServer().getCurrentTick();
        long delta = (now-lastChecked)/20;

        lastChecked = now;

        for (ResearchOption researchOption: researchOptions){

            long realDelta = Math.min(Math.min(researchOption.canPayDelta(Math.min(company.getBal(), company.getSpendable())), delta),
                    (long) (researchOption.getCompleteTime()*(1-researchOption.getProgress())));

            company.removeBal(researchOption.getCost(realDelta));

            if (!researchOption.checkProgress(realDelta)){
                continue;
            }
            company.addProduct(ProductFactory.create(researchOption.getMaterial(), researchOption.companyType));
        }
    }
}

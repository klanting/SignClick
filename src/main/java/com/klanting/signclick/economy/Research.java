package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import versionCompatibility.CompatibleLayer;

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

        lastChecked = CompatibleLayer.getCurrentTick();

        ConfigurationSection productsSection = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection("products").
                getConfigurationSection(companyType);

        List<String> researchItems = new ArrayList<>(productsSection.getKeys(false).stream().toList());

        researchItems.sort(Comparator.comparingInt(s -> productsSection.getConfigurationSection(s).getInt("index")));

        for (String researchItem: researchItems){
            Material m = Material.valueOf(researchItem);
            researchOptions.add(new ResearchOption(companyType, m));
        }

    }

    public void checkProgress(CompanyI company){
        long now = CompatibleLayer.getCurrentTick();
        long delta = (now-lastChecked)/20;

        lastChecked = now;

        for (ResearchOption researchOption: researchOptions){

            long realDelta = Math.min(Math.min(researchOption.canPayDelta(Math.min(company.getBal(), company.getSpendable())), delta),
                    (long) (researchOption.getCompleteTime()*(1-researchOption.getProgress())));

            company.removeBal(researchOption.getCost(realDelta));

            if (!researchOption.checkProgress(realDelta, (double) company.getUpgrades().get(5).getBonus()/100.0)){
                continue;
            }

            /*
            * Make log about completed research
            * */
            company.update("Research Completed",
                    "§aResearch for product "+researchOption.getMaterial().toString()+" COMPLETED"
                    , null);

            company.addProduct(ProductFactory.create(researchOption.getMaterial(), researchOption.companyType));
        }
    }
}

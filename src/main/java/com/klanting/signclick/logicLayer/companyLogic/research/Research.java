package com.klanting.signclick.logicLayer.companyLogic.research;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.configs.ConfigManager;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.producible.ProductFactory;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import versionCompatibility.CompatibleLayer;

import java.util.*;

import static com.klanting.signclick.utils.Utils.AssertMet;
import static org.bukkit.Bukkit.getServer;

@ClassFlush
public class Research {

    private List<ResearchOption> researchOptions = new ArrayList<>();

    private long lastChecked;

    public transient CompanyI company;


    public List<ResearchOption> getResearchOptions() {
        return researchOptions;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }

    public void loadMaterials(ConfigManager configManager){
        /*
        * ensure only the latest research materials form the config are shown
        * */
        List<ResearchOption> newResearchOptions = new ArrayList<>();

        /*
        * because some reason, the production is empty here else
        * */

        ConfigurationSection productsSection = configManager.getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection(company.getType());

        List<String> researchItems = new ArrayList<>(productsSection.getKeys(false).stream().toList());
        researchItems.sort(Comparator.comparingInt(s -> productsSection.getConfigurationSection(s).getInt("index")));

        Map<Material, ResearchOption> mapping = new HashMap<>();
        for(ResearchOption ro: researchOptions){
            mapping.put(ro.getMaterial(), ro);
        }

        for (String researchItem: researchItems){
            try {
                Material m = Material.valueOf(researchItem);

                if(mapping.containsKey(m)){
                    ResearchOption ro = mapping.get(m);
                    ro.company = this.company.getRef();
                    newResearchOptions.add(ro);
                }else{
                    newResearchOptions.add(new ResearchOption(this.company, m));
                }
            }catch (Exception e){
                getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "SignClick: "+researchItem+" INVALID ITEM, NOT LOADED");
            }


        }

        researchOptions = newResearchOptions;
    }

    public Research(CompanyI company){
        this.company = company;

        lastChecked = CompatibleLayer.getCurrentTick();

        ConfigurationSection productsSection = SignClick.getConfigManager().getConfig("production.yml").getConfigurationSection("products").
                getConfigurationSection(company.getType());

        List<String> researchItems = new ArrayList<>(productsSection.getKeys(false).stream().toList());

        researchItems.sort(Comparator.comparingInt(s -> productsSection.getConfigurationSection(s).getInt("index")));

        for (String researchItem: researchItems){
            try {
                Material m = Material.valueOf(researchItem);
                researchOptions.add(new ResearchOption(this.company, m));
            }catch (Exception e){
                getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "SignClick: "+researchItem+" INVALID");
            }
        }

    }

    public void checkProgress(){
        long now = CompatibleLayer.getCurrentTick();
        long delta = (now-lastChecked)/20L;

        if(delta == 0){
            return;
        }

        lastChecked = now;

        for (ResearchOption researchOption: researchOptions){

            long canPayDelta = researchOption.canPayDelta(Math.min(company.getBal(), company.getSpendable()));
            long remainingTime = Math.max((long) Math.ceil((researchOption.getCompleteTime()*(1-researchOption.getProgress()))), 0);

            AssertMet(canPayDelta >= 0, "Research: canPayDelta must be positive");
            AssertMet(delta >= 0, "Research: delta must be positive");
            AssertMet(remainingTime >= 0, "Research: remainingTime must be positive");

            long realDelta = Math.min(Math.min(canPayDelta, delta), remainingTime);

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

            company.addProduct(ProductFactory.create(researchOption.getMaterial(), researchOption.company));
        }
    }
}

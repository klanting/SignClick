package com.klanting.signclick.logicLayer.companyLogic.upgrades;

import com.klanting.signclick.SignClick;
import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.utils.statefulSQL.ClassFlush;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@ClassFlush
public class UpgradeBoardSize extends Upgrade{

    public CompanyI comp;

    public UpgradeBoardSize(Integer level, CompanyI comp) {
        super(level, 3);

        ConfigurationSection section = SignClick.getConfigManager().getConfig("companies.yml").getConfigurationSection(
                "upgrades").getConfigurationSection("boardSize");

        assert section != null;

        bonus = section.getIntegerList("bonus");
        upgradeCost = section.getIntegerList("upgradeCost");

        name = "Board Size";
        material = Material.CHEST;

        this.comp = comp.getRef();

    }

    @Override
    public List<String> description() {

        List<String> l = new ArrayList<>();
        l.add("§7Upgrade the amount of board seats");

        if (level < 5){
            l.add("§7From §f§n"+getBonus() +"§r§7->§f§n"+bonus.get(level+1));
        }

        return l;
    }

    @Override
    public void DoUpgrade(){
        super.DoUpgrade();
        comp.getCOM().getBoard().setBoardSeats(comp.getUpgrades().get(id).getBonus());
    }
}

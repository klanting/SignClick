package com.klanting.signclick.menus.company;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.menus.PagingMenu;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.klanting.signclick.economy.Market;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.klanting.signclick.utils.Utils.getCompanyTypeMaterial;

public class Selector extends PagingMenu {

    final private UUID uuid;

    public Function<Company, Void> funcType;

    public final Company allButThis;

    public Selector(UUID uuid, Function<Company, Void> funcType){
        this(uuid, funcType, null);
    }

    public Selector(UUID uuid, Function<Company, Void> funcType, Company allButThis){
        super(54, "Company Selector", allButThis != null? true: false);
        this.uuid = uuid;
        this.funcType = funcType;
        this.allButThis = allButThis;

        init();
    }

    public void init(){
        ItemStack item;

        clearItems();

        List<Company> companies = Market.getBusinessByDirector(uuid);
        if (allButThis != null){
            companies = Market.getBusinessExclude(allButThis);
        }

        for(Company c: companies){
            item = new ItemStack(getCompanyTypeMaterial(c.type),1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName("§6"+c.getName()+" ["+c.getStockName()+"]");

            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            lores.add("§7Type: "+c.type);
            lores.add("§7Value: "+df.format(c.getValue()));

            m.setLore(lores);
            item.setItemMeta(m);

            addItem(item);
        }

        super.init();


    }

}

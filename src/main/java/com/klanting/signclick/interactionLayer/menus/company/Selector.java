package com.klanting.signclick.interactionLayer.menus.company;

import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.interactionLayer.menus.PagingMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.klanting.signclick.logicLayer.Market;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.klanting.signclick.interactionLayer.events.MenuEvents.clearStack;
import static com.klanting.signclick.utils.Utils.getCompanyTypeMaterial;

public class Selector extends PagingMenu {

    final private UUID uuid;

    public Function<CompanyI, Void> funcType;

    public final CompanyI allButThis;

    private SelectionType selectionType;

    public Selector(UUID uuid, Function<CompanyI, Void> funcType){
        this(uuid, funcType, SelectionType.Chief);
    }

    public Selector(UUID uuid, Function<CompanyI, Void> funcType, SelectionType selectionType){

        this(uuid, funcType, selectionType, null);
        init();

    }

    public Selector(UUID uuid, Function<CompanyI, Void> funcType, CompanyI allButThis){

        this(uuid, funcType, SelectionType.AllButThis, allButThis);
        init();

    }

    public Selector(UUID uuid, Function<CompanyI, Void> funcType, SelectionType selectionType, CompanyI allButThis){
        super(54, "Company Selector", allButThis != null? true: false);
        this.uuid = uuid;
        this.funcType = funcType;
        this.allButThis = allButThis;
        this.selectionType = selectionType;

        init();
    }

    public void init(){
        ItemStack item;

        clearItems();

        List<CompanyI> companies;

        switch (selectionType){
            case Chief:
                companies = Market.getBusinessByChief(uuid);
                break;
            case Shares:
                companies = Market.getBusinessByShares(uuid);
                break;
            case AllButThis:
                companies = Market.getBusinessExclude(allButThis);
                break;
            case BoardMember:
            default:
                companies = Market.getBusinessByDirector(uuid);
                break;
        }

        for(CompanyI c: companies){
            item = new ItemStack(getCompanyTypeMaterial(c.getType()),1);
            ItemMeta m = item.getItemMeta();
            m.setDisplayName("§6"+c.getName()+" ["+c.getStockName()+"]");

            List<String> lores = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###,###,###");
            lores.add("§7Type: "+c.getType());
            lores.add("§7Value: "+df.format(c.getValue()));

            m.setLore(lores);
            item.setItemMeta(m);

            addItem(item);
        }

        super.init();


    }

    public boolean onClick(InventoryClickEvent event){
        if (!super.onClick(event)){
            return false;
        }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        if (allButThis == null){
            clearStack(player);
        }


        if (event.getCurrentItem().getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE){
            return false;
        }

        int startPos = event.getCurrentItem().getItemMeta().getDisplayName().indexOf("[");
        int endPos = event.getCurrentItem().getItemMeta().getDisplayName().length()-1;
        CompanyI company = Market.getCompany(event.getCurrentItem().getItemMeta().getDisplayName().substring(startPos+1, endPos));

        funcType.apply(company);

        return true;
    }

}

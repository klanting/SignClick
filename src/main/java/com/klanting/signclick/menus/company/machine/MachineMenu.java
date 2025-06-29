package com.klanting.signclick.menus.company.machine;

import com.klanting.signclick.economy.Company;
import com.klanting.signclick.menus.SelectionMenu;

import java.util.UUID;

public class MachineMenu extends SelectionMenu {
    public Company comp;

    public MachineMenu(UUID uuid, Company company){
        super(45, "Company Menu: "+ company.getStockName(), false);
        comp = company;

        assert comp.getCOM().isOwner(uuid);
        init();
    }

    public void init(){

        getInventory().clear();



        super.init();
    }
}

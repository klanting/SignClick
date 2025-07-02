package com.klanting.signclick.economy;

import com.klanting.signclick.economy.companyPatent.Auction;

import java.util.ArrayList;
import java.util.List;

public class LicenseManager {
    private List<License> licenses = new ArrayList<>();

    public List<License> getLicensesTo(Company comp) {
        return licenses.stream().filter(l -> l.getTo().equals(comp)).toList();
    }

    public void addLicense(License license){
        licenses.add(license);
    }

    public void removeLicense(License license){
        licenses.remove(license);
    }

    private static LicenseManager instance = null;

    public static LicenseManager getInstance(){
        if (instance == null){
            instance = new LicenseManager();
        }

        return instance;
    }
}

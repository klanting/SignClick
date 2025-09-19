package com.klanting.signclick.logicLayer.companyLogic;

import java.util.ArrayList;
import java.util.List;

public class LicenseManager {
    private List<License> licenses = new ArrayList<>();

    public List<License> getLicensesTo(CompanyI comp) {
        return licenses.stream().filter(l -> l.getTo().equals(comp)).toList();
    }

    public List<License> getLicensesFrom(CompanyI comp) {
        return licenses.stream().filter(l -> l.getFrom().equals(comp)).toList();
    }

    public void addLicense(License license){
        licenses.add(license);
    }

    public void removeLicense(License license){
        licenses.remove(license);
    }
}

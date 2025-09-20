package com.klanting.signclick.logicLayer.companyLogic;

import com.klanting.signclick.SignClick;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.UUID;

public class MergeNegotiation {

    private CompanyI comp1;
    private CompanyI comp2;

    private int totalShares;
    private int sharesToComp1;

    private boolean takeComp1Name;
    private double moneyToComp1;

    public MergeNegotiation(CompanyI comp1, CompanyI comp2){
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.takeComp1Name = true;

        this.totalShares = SignClick.getConfigManager().getConfig("companies.yml").getInt("companyStartShares");

        double valueComp1 = comp1.getValue();
        double valueComp2 = comp2.getValue();

        double comp1PCT = valueComp1/(valueComp1+valueComp2);

        sharesToComp1 = (int) Math.round(totalShares*comp1PCT);

        moneyToComp1 = 0;

    }

    private static Pair<Integer, Integer> getNewShares(CompanyI comp, int newSharesSubTotal, UUID uuid){
        /*
        * return shares, remaining
        * */
        double shareChange = newSharesSubTotal/(double) comp.getTotalShares();
        int originalShares = comp.getCOM().getShareHolders().get(uuid);
        double newSharesDouble = shareChange*originalShares;
        int newShares = (int) Math.floor(newSharesDouble);
        int remaining = (int) Math.round((newSharesDouble-newShares)/shareChange);

        return Pair.of(newShares, remaining);
    }

    public void changeShareDistribution(int shiftToComp2Amount){
        this.sharesToComp1 -= shiftToComp2Amount;
    }

    public Triple<Integer, Integer, Integer> getShareDistribution(){
        /*
        * return: shares to comp1, shares to comp2, shares to market
        * */
        int comp1Shares = 0;
        for(Map.Entry<UUID, Integer> shares: comp1.getCOM().getShareHolders().entrySet()){
            comp1Shares += getNewShares(comp1, sharesToComp1, shares.getKey()).getLeft();
        }

        int comp2Shares = 0;
        for(Map.Entry<UUID, Integer> shares: comp2.getCOM().getShareHolders().entrySet()){
            comp2Shares += getNewShares(comp2, totalShares-sharesToComp1, shares.getKey()).getLeft();
        }

        return Triple.of(comp1Shares, comp2Shares, totalShares-comp1Shares-comp2Shares);
    }

    public Pair<Double, Double> getPayoutDistribution(){
        double comp1Payout = 0;
        double comp1ValuePerShare = comp1.getValue()/(double) comp1.getTotalShares();
        for(Map.Entry<UUID, Integer> shares: comp1.getCOM().getShareHolders().entrySet()){
            comp1Payout += getNewShares(comp1, sharesToComp1, shares.getKey()).getRight()*comp1ValuePerShare;
        }

        double comp2Payout = 0;
        double comp2ValuePerShare = comp2.getValue()/(double) comp2.getTotalShares();
        for(Map.Entry<UUID, Integer> shares: comp2.getCOM().getShareHolders().entrySet()){
            comp2Payout += getNewShares(comp2, totalShares-sharesToComp1, shares.getKey()).getRight()*comp2ValuePerShare;
        }

        return Pair.of(comp1Payout, comp2Payout);
    }


}

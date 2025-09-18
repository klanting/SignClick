package com.klanting.signclick.logicLayer.contractRequests;

import com.klanting.signclick.logicLayer.CompanyI;
import com.klanting.signclick.logicLayer.Market;

public class ContractRequestCTC extends ContractRequest{

    private final CompanyI from;
    private final CompanyI to;

    public ContractRequestCTC(CompanyI from, CompanyI to, double amount, int weeks, String reason) {
        super(amount, weeks, reason);

        this.from = from;
        this.to = to;

    }


    @Override
    public boolean accept() {
        Market.setContractComptoComp(from.getStockName(), to.getStockName(), amount, weeks, reason);
        return true;
    }

    @Override
    public String to() {
        return to.getStockName();
    }
}

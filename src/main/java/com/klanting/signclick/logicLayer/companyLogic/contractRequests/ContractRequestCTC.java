package com.klanting.signclick.logicLayer.companyLogic.contractRequests;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.utils.statefulSQL.ClassFlush;

@ClassFlush
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

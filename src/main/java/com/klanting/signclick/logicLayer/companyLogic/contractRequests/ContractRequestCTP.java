package com.klanting.signclick.logicLayer.companyLogic.contractRequests;

import com.klanting.signclick.logicLayer.companyLogic.CompanyI;
import com.klanting.signclick.logicLayer.companyLogic.Market;
import com.klanting.signclick.utils.statefullSQL.ClassFlush;

import java.util.UUID;

@ClassFlush
public class ContractRequestCTP extends ContractRequest{
    private final CompanyI from;
    private final UUID to;

    public ContractRequestCTP(CompanyI from, UUID to, double amount, int weeks, String reason) {
        super(amount, weeks, reason);

        this.from = from;
        this.to = to;

    }


    @Override
    public boolean accept() {
        Market.setContractComptoPlayer(from.getStockName(), to.toString(), amount, weeks, reason);
        return true;
    }

    @Override
    public String to() {
        return to.toString();
    }
}

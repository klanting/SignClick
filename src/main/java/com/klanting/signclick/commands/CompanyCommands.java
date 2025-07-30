package com.klanting.signclick.commands;

import com.klanting.signclick.commands.companyHandelers.*;

import com.klanting.signclick.commands.companyHandelers.contractHandlers.*;
import com.klanting.signclick.commands.exceptions.CommandAssert;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;

import com.klanting.signclick.SignClick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class CompanyCommands implements CommandExecutor, TabCompleter {
    public static Map<Player, String> confirm = new HashMap<>();

    public static final List<String> whitelist = Arrays.asList("info", "sharetop", "give", "buy", "sell", "pay", "spendable",
            "transfer", "get_support", "send_contract_ctc", "sign_contract_ctc", "sharebal", "send_contract_ctp",
            "sign_contract_ctp", "send_contract_ptc", "get_buy_price", "get_sell_price", "get_contracts",
            "add_custom", "open_trade", "transact");


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players allowed");
            return true; }

        if (!cmd.getName().equalsIgnoreCase("company")){
            return true;
        }

        if (args.length == 0){
            player.sendMessage("§bplease enter /company <category>");
            confirm.put(player, "");
            return true;
        }

        String commando = args[0];

        HashMap<String, CompanyHandler> handlerTranslation = new HashMap<>();
        handlerTranslation.put("create", new CompanyHandlerCreate());
        handlerTranslation.put("info", new CompanyHandlerInfo());
        handlerTranslation.put("sharetop", new CompanyHandlerSharetop());
        handlerTranslation.put("give", new CompanyHandlerGive());
        handlerTranslation.put("baltop", new CompanyHandlerBalTop());
        handlerTranslation.put("buy", new CompanyHandlerBuy());
        handlerTranslation.put("sell", new CompanyHandlerSell());
        handlerTranslation.put("pay", new CompanyHandlerPay());
        handlerTranslation.put("transfer", new CompanyHandlerTransfer());

        handlerTranslation.put("get_support", new CompanyHandlerGetSupport());
        handlerTranslation.put("support", new CompanyHandlerSupport());
        handlerTranslation.put("open_trade", new CompanyHandlerOpenTrade());
        handlerTranslation.put("markettop", new CompanyHandlerMarketTop());
        handlerTranslation.put("market", new CompanyHandlerMarket());
        handlerTranslation.put("menu", new CompanyHandlerMenu());
        handlerTranslation.put("portfolio", new CompanyHandlerPortfolio());
        handlerTranslation.put("transact", new CompanyHandlerTransact());

        handlerTranslation.put("get_buy_price", new CompanyHandlerGetBuyPrice());
        handlerTranslation.put("get_sell_price", new CompanyHandlerGetSellPrice());

        handlerTranslation.put("send_contract_ctc", new ContractSendCTC());
        handlerTranslation.put("sign_contract_ctc", new ContractSignCTC());
        handlerTranslation.put("send_contract_ctp", new ContractSendCTP());
        handlerTranslation.put("sign_contract_ctp", new ContractSignCTP());
        handlerTranslation.put("send_contract_ptc", new ContractSendPTC());
        handlerTranslation.put("sign_contract_ptc", new ContractSignPTC());
        handlerTranslation.put("get_contracts", new CompanyHandlerGetContracts());

        handlerTranslation.put("sharebal", new CompanyHandlerShareBal());
        handlerTranslation.put("guide", new CompanyHandlerGuide());

        try{
            if (handlerTranslation.containsKey(commando)){
                CommandAssert.assertPerms(player, "company."+commando,
                        "§bYou do not have the permissions to execute this command");

                CompanyHandler ch = handlerTranslation.get(commando);

                /*
                * Check whether command is entered for the first time, so that the command needs to be repeated
                * if command confirmation is enabled
                * */
                boolean firstEnter = !confirm.getOrDefault(player, "").equals(commando);
                firstEnter = firstEnter && SignClick.getConfigManager().getConfig("companies.yml").getBoolean("companyConfirmation");

                boolean setConfirm = ch.handleCommand(player, args, firstEnter);

                if (setConfirm){
                    confirm.put(player, commando);
                }else{
                    confirm.remove(player);
                }

            }

        }catch (CommandException e){
            player.sendMessage(e.getMessage());
            confirm.put(player, "");
            return true;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return null; }
        Player player = (Player) sender;

        if (!command.getName().equalsIgnoreCase("company")) {
            return null;
        }

        List<String> autoCompletes = new ArrayList<>();
        if (args.length == 1) {

            autoCompletes.add("sharebal");

            autoCompletes.add("create");
            autoCompletes.add("info");
            autoCompletes.add("sharetop");
            autoCompletes.add("give");
            autoCompletes.add("baltop");
            autoCompletes.add("buy");
            autoCompletes.add("sell");
            autoCompletes.add("pay");
            autoCompletes.add("spendable");
            autoCompletes.add("support");
            autoCompletes.add("transfer");
            autoCompletes.add("transact");
            autoCompletes.add("portfolio");
            autoCompletes.add("markettop");
            autoCompletes.add("market");
            autoCompletes.add("guide");
            autoCompletes.add("get_support");
            autoCompletes.add("send_contract_ctc");
            autoCompletes.add("sign_contract_ctc");
            autoCompletes.add("send_contract_ctp");
            autoCompletes.add("sign_contract_ctp");
            autoCompletes.add("send_contract_ptc");
            autoCompletes.add("sign_contract_ptc");
            autoCompletes.add("get_buy_price");
            autoCompletes.add("get_sell_price");
            autoCompletes.add("get_contracts");
            autoCompletes.add("open_trade");

            List<String> newAutoCompletes = new ArrayList<>();
            for (String category: autoCompletes){
                if (player.hasPermission("signclick.company."+category)){
                    newAutoCompletes.add(category);
                }
            }

            return newAutoCompletes;
        }else if (args.length == 2){
            if (whitelist.contains(args[0])){
                return Market.getBusinesses();
            }
        }else if (args.length == 3){
            if (args[0].equals("send_contract_ctc") || args[0].equals("transact")){
                return Market.getBusinesses();
            }
        }

        return null;
    }
}

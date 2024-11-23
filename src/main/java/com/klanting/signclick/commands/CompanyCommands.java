package com.klanting.signclick.commands;

import com.klanting.signclick.commands.companyHandelers.*;
import com.klanting.signclick.commands.exceptions.CommandException;
import com.klanting.signclick.economy.Market;
import com.klanting.signclick.economy.Company;
import com.klanting.signclick.economy.Account;

import com.klanting.signclick.economy.companyPatent.PatentUpgradeCustom;
import com.klanting.signclick.Menus.CompanySelector;
import com.klanting.signclick.SignClick;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;


public class CompanyCommands implements CommandExecutor, TabCompleter {
    public static Map<Player, String> confirm = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players allowed");
            return true; }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("company")){
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
            handlerTranslation.put("spendable", new CompanyHandlerSpendable());
            handlerTranslation.put("transfer", new CompanyHandlerTransfer());
            handlerTranslation.put("get_support", new CompanyHandlerGetSupport());
            handlerTranslation.put("support", new CompanyHandlerSupport());
            handlerTranslation.put("open_trade", new CompanyHandlerOpenTrade());
            handlerTranslation.put("market", new CompanyHandlerMarket());

            try{

                if (handlerTranslation.containsKey(commando)){
                    CompanyHandler ch = handlerTranslation.get(commando);
                    boolean setConfirm = ch.handleCommand(player, args,
                            !confirm.getOrDefault(player, "").equals(commando));

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


            if (commando.equals("portfolio")){
                confirm.put(player, "");
                UUID target_uuid = null;
                if(args.length < 2){
                    target_uuid = player.getUniqueId();
                }else{
                    for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                        if (target.getName().equals(args[1])){
                            target_uuid = target.getUniqueId();
                        }
                    }
                }

                if (target_uuid == null){
                    player.sendMessage("§bplayer doesn't exist");
                    return true;
                }

                if (!Market.hasAccount(target_uuid)){
                    player.sendMessage("§bplayer doesn't have an account");
                    return true;
                }

                Market.getAccount(target_uuid).getPortfolio(player);

            }

            if (commando.equals("send_contract_ctc")){
                if (args.length < 5){
                    player.sendMessage("§bplease enter /company send_contract_ctc <owncompany> <othercompany> <amount> <weeks> [reason]");
                    confirm.put(player, "");
                    return true;
                }

                String reason;
                if (args.length < 6){
                    reason = "no_reason";
                }else{
                    reason = args[5];
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.getBusiness(stock_name).isOwner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to send that request");
                    confirm.put(player, "");
                    return true;
                }

                String target_stock_name = args[2].toUpperCase();
                target_stock_name = target_stock_name.toUpperCase();

                if (!Market.hasBusiness(target_stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                double amount = Double.parseDouble(args[3]);
                int weeks = Integer.parseInt(args[4]);

                if (confirm.getOrDefault(player, "").equals("send_contract_ctc")){
                    confirm.put(player, "");
                    if (Market.getBusiness(target_stock_name).compNamePending == null){
                        Market.getBusiness(stock_name).sendOfferCompContract(target_stock_name, amount, weeks, reason);
                    }else{
                        player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
                    }

                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                            +"\n§bfor an amount of §f"+ amount
                            +"\n§bfor a time of §f"+ weeks+
                            " weeks \n§c/company send_contract_ctc "+stock_name+" "+target_stock_name+" "+amount+ " "+ weeks);
                    confirm.put(player, "send_contract_ctc");
                }

            }

            if (commando.equals("sign_contract_ctc")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company sign_contract_ctc <owncompany>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.getBusiness(stock_name).isOwner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to sign that request");
                    confirm.put(player, "");
                    return true;
                }

                Company comp = Market.getBusiness(stock_name);
                if (comp.spendable < comp.compAmountPending){
                    player.sendMessage("§bcan't sign contract because lack of weekly spendable funds");
                    confirm.put(player, "");
                    return true;
                }


                if (confirm.getOrDefault(player, "").equals("sign_contract_ctc")){
                    confirm.put(player, "");
                    comp.acceptOfferCompContract();
                    player.sendMessage("§bcontract confirmed");

                }else{
                    DecimalFormat df = new DecimalFormat("###,###,###");
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" +comp.compNamePending
                            +"§b \nfor an amount of §f"+ df.format(comp.compAmountPending)
                            +"§b \nfor a time of §f"+ comp.compWeeksPending +
                            " weeks \n§c/company sign_contract_ctc "+stock_name);
                    confirm.put(player, "sign_contract_ctc");
                }


            }

            if (commando.equals("books")){
                confirm.put(player, "");
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company books <owncompany>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!player.hasPermission("signclick.staff")){
                    player.sendMessage("§byou must be staff");
                    confirm.put(player, "");
                    return true;
                }

                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                player.sendMessage("§b books money: "+df.format(Market.getBooks(stock_name)));
            }

            if (commando.equals("send_contract_ctp")){
                if (args.length < 4){
                    player.sendMessage("§bplease enter /company send_contract_ctp <othercompany> <amount> <weeks> [reason]");
                    confirm.put(player, "");
                    return true;
                }

                String reason;
                if (args.length < 5){
                    reason = "no_reason";
                }else{
                    reason = args[4];
                }

                String target_stock_name = args[1].toUpperCase();
                target_stock_name = target_stock_name.toUpperCase();

                if (!Market.hasBusiness(target_stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                double amount = Double.parseDouble(args[2]);
                int weeks = Integer.parseInt(args[3]);

                if (confirm.getOrDefault(player, "").equals("send_contract_ctp")){
                    confirm.put(player, "");
                    if (Market.getBusiness(target_stock_name).playerNamePending == null){
                        Market.getBusiness(target_stock_name).receiveOfferPlayerContract(player.getUniqueId().toString(), amount, weeks, reason);
                    }else{
                        player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
                    }

                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                            +"§b \n for an amount of §f"+ amount
                            +"§b \n for a time of §f"+ weeks+
                            " weeks \n§c/company send_contract_ctp "+target_stock_name+" "+amount+ " "+ weeks);
                    confirm.put(player, "send_contract_ctp");
                }

            }

            if (commando.equals("sign_contract_ctp")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company sign_contract_ctp <owncompany>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.getBusiness(stock_name).isOwner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to sign that request");
                    confirm.put(player, "");
                    return true;
                }

                Company comp = Market.getBusiness(stock_name);
                if (comp.spendable < comp.compAmountPending){
                    player.sendMessage("§bcan't sign contract because lack of weekly spendable funds");
                    confirm.put(player, "");
                    return true;
                }

                if (comp.isOwner(UUID.fromString(comp.playerNamePending)) && player.getUniqueId().equals(UUID.fromString(comp.playerNamePending))){
                    player.sendMessage("§byou can't' make a contract with yourself");
                    confirm.put(player, "");
                    return true;
                }


                if (confirm.getOrDefault(player, "").equals("sign_contract_ctp")){
                    confirm.put(player, "");
                    comp.acceptOfferPlayerContract();
                    player.sendMessage("§bcontract confirmed");

                }else{
                    DecimalFormat df = new DecimalFormat("###,###,###");

                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" + Bukkit.getOfflinePlayer(UUID.fromString(comp.playerNamePending)).getName()
                            +"§b \nfor an amount of §f"+ df.format(comp.playerAmountPending)
                            +"§b \nfor a time of §f"+ comp.playerWeeksPending +
                            " weeks \n§c/company sign_contract_ctp "+stock_name);
                    confirm.put(player, "sign_contract_ctp");
                }
            }

            if (commando.equals("send_contract_ptc")){
                if (args.length < 5){
                    player.sendMessage("§bplease enter /company send_contract_ptc <owncompany> <player> <amount> <weeks> [reason]");
                    confirm.put(player, "");
                    return true;
                }

                String reason;
                if (args.length < 6){
                    reason = "no_reason";
                }else{
                    reason = args[5];
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.getBusiness(stock_name).isOwner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to send that request");
                    confirm.put(player, "");
                    return true;
                }

                UUID target_uuid = null;
                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                    if (target.getName().equals(args[2])){
                        target_uuid = target.getUniqueId();
                    }
                }

                if (target_uuid == null){
                    player.sendMessage("§bplayer doesn't exist");
                    return true;
                }

                if (!Market.hasAccount(target_uuid)){
                    player.sendMessage("§bplayer doesn't have an account");
                    return true;
                }

                double amount = Double.parseDouble(args[3]);
                int weeks = Integer.parseInt(args[4]);

                if (confirm.getOrDefault(player, "").equals("send_contract_ptc")){
                    confirm.put(player, "");
                    if (Market.getAccount(target_uuid).compNamePending == null){
                        Market.getAccount(target_uuid).receive_offer_comp_contract(stock_name, amount, weeks, reason);
                    }else{
                        player.sendMessage("§cplayer still has another offer pending, try again in 2 minutes");
                    }


                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +args[2]
                            +"§b \n for an amount of §f"+ amount
                            +"§b \n for a time of §f"+ weeks+
                            " weeks \n§c/company send_contract_ptc "+stock_name+" "+args[2]+" "+amount+ " "+ weeks);
                    confirm.put(player, "send_contract_ptc");
                }

            }

            if (commando.equals("sign_contract_ptc")){

                Account acc = Market.getAccount(player);

                if (acc.getBal() < acc.compAmountPending){
                    player.sendMessage("§bcan't sign contract because lack of money");
                    confirm.put(player, "");
                    return true;
                }


                if (confirm.getOrDefault(player, "").equals("sign_contract_ptc")){
                    confirm.put(player, "");
                    acc.accept_offer_comp_contract();
                    player.sendMessage("§bcontract confirmed");

                }else{
                    DecimalFormat df = new DecimalFormat("###,###,###");

                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" + acc.compNamePending
                            +"§b \nfor an amount of §f"+ df.format(acc.compAmountPending)
                            +"§b \nfor a time of §f"+ acc.compWeeksPending +
                            " weeks \n§c/company sign_contract_ptc");
                    confirm.put(player, "sign_contract_ptc");
                }
            }

            if (commando.equals("get_buy_price")){
                confirm.put(player, "");
                int amount = 1;
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company get_buy_price <com.company> [amount]");
                    return true;
                }else if (args.length == 3){
                    amount = Integer.parseInt(args[2]);
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                player.sendMessage("§f"+amount+"§b share(s) costs §f"+ df.format(Market.getBuyPrice(stock_name, amount)));
            }

            if (commando.equals("get_sell_price")){
                confirm.put(player, "");
                int amount = 1;
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company get_sell_price <com.company> [amount]");
                    return true;
                }else if (args.length == 3){
                    amount = Integer.parseInt(args[2]);
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                player.sendMessage("§f"+amount+"§b share(s) costs §f"+ df.format(Market.getSellPrice(stock_name, amount)));
            }

            if (commando.equals("get_contracts")){
                confirm.put(player, "");
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company get_contracts <com.company>");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                Market.getContracts(stock_name, player);
            }

            if (commando.equals("menu")){
                CompanySelector screen = new CompanySelector(player.getUniqueId());
                player.openInventory(screen.getInventory());

            }

            else if (commando.equals("transact")) {
                if (args.length < 4){
                    player.sendMessage("§bplease enter /company transact <company> <target_company> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                String target_stock_name = args[2].toUpperCase();
                target_stock_name = target_stock_name.toUpperCase();

                if (!Market.hasBusiness(target_stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                double amount = Double.parseDouble(args[3]);

                if (!Market.getBusiness(stock_name).isOwner(player.getUniqueId())){
                    player.sendMessage("§byou must be a CEO of this com.company");
                    confirm.put(player, "");
                    return true;
                }

                if (confirm.getOrDefault(player, "").equals("transact")){
                    confirm.put(player, "");
                    //still need refresh weekly the 20 % cap
                    if (Market.getBusiness(stock_name).removeBal(amount)){
                        Company comp_target = Market.getBusiness(target_stock_name);
                        comp_target.addBal(amount);
                        player.sendMessage("§bsuccesfully paid §f"+target_stock_name+" "+amount);
                        comp_target.sendOwner("§bsuccesfully received §f"+amount+" §bfrom §f"+stock_name);

                    }else{
                        player.sendMessage("§bbusiness does not have enough money, or you reached your monthly spending limit\ndo §c/company spendable "+
                                stock_name+"§b to see monthly available money");
                    }
                }else{
                    player.sendMessage("§bplease re-enter the command to confirm");
                    confirm.put(player, "transact");
                }

            }

            if (commando.equals("add_custom")){
                confirm.put(player, "");
                if (args.length < 4){
                    player.sendMessage("§bplease enter /company add_custom <Company> <Texture> <Item>");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.hasBusiness(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    return true;
                }

                if (!player.hasPermission("signclick.staff")){
                    player.sendMessage("§byou must be staff");
                    return true;
                }

                Material item = Material.valueOf(args[3].toUpperCase());

                if (item == null){
                    player.sendMessage("§bitem not correctly specified");
                    return true;
                }

                Company comp = Market.getBusiness(stock_name);
                comp.patentUpgrades.add(new PatentUpgradeCustom(args[2], item));
            }

            if (commando.equals("particles")){
                confirm.put(player, "");

                if (!player.hasPermission("signclick.staff")){
                    player.sendMessage("§byou must be staff");
                    return true;
                }

                if (args.length < 2){
                    player.sendMessage("§bparticles are "+ Market.showParticles);
                    return true;
                }else{
                    Market.showParticles = Objects.equals(args[1], "TRUE");
                }


            }

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

        if (command.getName().equalsIgnoreCase("company")) {
            List<String> autoCompletes = new ArrayList<>();
            if (args.length == 1) {
                if (player.hasPermission("signclick.staff")){
                    autoCompletes.add("books");
                    autoCompletes.add("add_custom");
                    autoCompletes.add("particles");
                }
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
                autoCompletes.add("market");
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

                return autoCompletes;
            }else if (args.length == 2){
                if (args[0].equals("info") || args[0].equals("sharetop") || args[0].equals("give")
                        || args[0].equals("buy") || args[0].equals("sell") || args[0].equals("pay")
                        || args[0].equals("spendable") || args[0].equals("support") || args[0].equals("transfer")
                        || args[0].equals("get_support") || args[0].equals("send_contract_ctc") || args[0].equals("sign_contract_ctc")
                        || args[0].equals("books") || args[0].equals("send_contract_ctp") || args[0].equals("sign_contract_ctp")
                        || args[0].equals("send_contract_ptc") || args[0].equals("get_buy_price") || args[0].equals("get_sell_price")
                        || args[0].equals("get_contracts") || args[0].equals("add_custom") || args[0].equals("open_trade") || args[0].equals("transact")){
                    return Market.getBusinesses();
                }
            }else if (args.length == 3){
                if (args[0].equals("send_contract_ctc") || args[0].equals("transact")){
                    return Market.getBusinesses();
                }
            }


        }

        return null;
    }
}

package com.klanting.signclick.commands;

import com.klanting.signclick.commands.companyHandelers.*;

import com.klanting.signclick.commands.companyHandelers.contractHandlers.*;
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

import static com.klanting.signclick.economy.Market.getBusiness;


public class CompanyCommands implements CommandExecutor, TabCompleter {
    public static Map<Player, String> confirm = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players allowed");
            return true; }

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
            handlerTranslation.put("menu", new CompanyHandlerMenu());
            handlerTranslation.put("portfolio", new CompanyHandlerPortfolio());
            handlerTranslation.put("transact", new CompanyHandlerTransact());
            handlerTranslation.put("send_contract_ctc", new ContractSendCTC());
            handlerTranslation.put("sign_contract_ctc", new ContractSignCTC());
            handlerTranslation.put("send_contract_ctp", new ContractSendCTP());
            handlerTranslation.put("sign_contract_ctp", new ContractSignCTP());
            handlerTranslation.put("send_contract_ptc", new ContractSendPTC());
            handlerTranslation.put("sign_contract_ptc", new ContractSignPTC());

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
                Company comp = getBusiness(stock_name);
                player.sendMessage("§b books money: "+df.format(comp.getShareBalance()));
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

                Company comp = getBusiness(stock_name);
                comp.patentUpgrades.add(new PatentUpgradeCustom(args[2], item));
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

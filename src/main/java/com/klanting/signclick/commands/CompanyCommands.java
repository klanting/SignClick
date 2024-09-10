package com.klanting.signclick.commands;

import com.klanting.signclick.Economy.Account;
import com.klanting.signclick.Economy.Banking;
import com.klanting.signclick.Economy.Company;
import com.klanting.signclick.Economy.CompanyPatent.PatentUpgradeCustom;
import com.klanting.signclick.Economy.Market;
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
    private static Map<Player, String> confirm = new HashMap<Player, String>();

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

            if (commando.equals("create")){
                if (args.length < 3){
                    player.sendMessage("§bplease enter /company create <name> <stockname>");
                    confirm.put(player, "");
                    return true;
                }

                String company_name = args[1];
                String stock_name = args[2];

                if (stock_name.length() > 4){
                    player.sendMessage("§bstockname has a max length of 4");
                    confirm.put(player, "");
                    return true;
                }

                double discount_pct = (1.0-Banking.getPolicyBonus(Banking.Element(player), 1, 4));
                if (!SignClick.getEconomy().has(player, 40000000.0*discount_pct)){
                    player.sendMessage("§bmaking a company costs §c40 million (or discount policy)");
                    confirm.put(player, "");
                    return true;
                }

                if (confirm.getOrDefault(player, "").equals("create")){
                    confirm.put(player, "");
                    player.sendMessage("§byou succesfully found "+company_name+" good luck CEO "+player.getName());
                    stock_name = stock_name.toUpperCase();
                    Boolean succes = Market.add_business(company_name, stock_name, Market.get_account(player));

                    if (succes){
                        SignClick.getEconomy().withdrawPlayer(player, 40000000.0*discount_pct);
                        Company comp = Market.get_business(stock_name);
                        comp.add_bal(40000000.0*discount_pct);
                    }else{
                        player.sendMessage("§bcompany create failed: name/stockName already in use");
                    }


                }else{
                    player.sendMessage("§bplease re-enter your command to confirm that you want to start a company" +
                            " and want to auto-transfer §640 million §bto your business from your account"+
                            " If you agree, enter: §c/company create "+company_name+" "+stock_name);
                    confirm.put(player, "create");

                }

            }

            if (commando.equals("info")){
                confirm.put(player, "");
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company info <stockname>");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                if (Market.has_business(stock_name)){
                    Market.get_business(stock_name).info(player);;
                }else{
                    player.sendMessage("§bplease enter a valid company stockname");
                }
            }

            if (commando.equals("sharetop")){
                confirm.put(player, "");
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company sharetop <stockname>");
                    return true;
                }

                String stock_name = args[1].toUpperCase();

                if (Market.has_business(stock_name)){
                    Market.get_business(stock_name).get_share_top(player);
                }else{
                    player.sendMessage("§bplease enter a valid company stockname");
                }
            }

            if (commando.equals("give")){
                if (args.length < 3){
                    player.sendMessage("§bplease enter /company give <stockname> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();
                double amount = Double.parseDouble(args[2]);

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bplease enter a valid company stockname");
                    confirm.put(player, "");
                    return true;
                }

                if (!SignClick.getEconomy().has(player, amount)){
                    player.sendMessage("§byou do not have enough money");
                    confirm.put(player, "");
                    return true;
                }

                DecimalFormat df = new DecimalFormat("###,###,###");
                if (confirm.getOrDefault(player, "").equals("give")){
                    confirm.put(player, "");
                    player.sendMessage("§byou succesfully gave §f"+df.format(amount)+"§b to §f"+stock_name);

                    Market.get_business(stock_name).add_bal(amount);

                    SignClick.getEconomy().withdrawPlayer(player, amount);

                    Market.get_business(stock_name).send_owner("§byour business §f"+stock_name+" §b received §f"+amount+" §b from §f"+player.getName());

                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to give §f" +df.format(amount)+
                            "§b to §f"+ stock_name+"\n§c/company give "+stock_name+" "+amount);
                    confirm.put(player, "give");

                }
            }

            if (commando.equals("baltop")){
                confirm.put(player, "");
                Market.get_market_value_top(player);
            }

            if (commando.equals("buy")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company buy <stockname> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();
                int amount = Integer.parseInt(args[2]);

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bplease enter a valid company stockname");
                    confirm.put(player, "");
                    return true;
                }

                if (confirm.getOrDefault(player, "").equals("buy")){
                    confirm.put(player, "");
                    Account acc = Market.get_account(player);
                    acc.buy_share(stock_name, amount, player);

                }else{
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to buy §f" +amount+
                            "§b from §f"+ stock_name+" for a price of §6"+df.format(Market.get_buy_price(stock_name, amount))+" \n§c/company buy "+stock_name+" "+amount);
                    confirm.put(player, "buy");

                }

            }

            if (commando.equals("sell")){
                if (args.length < 3){
                    player.sendMessage("§bplease enter /company sell <stockname> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();
                int amount = Integer.parseInt(args[2]);

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bplease enter a valid company stockname");
                    confirm.put(player, "");
                    return true;
                }

                if (confirm.getOrDefault(player, "").equals("sell")){
                    confirm.put(player, "");
                    Account acc = Market.get_account(player);
                    acc.sell_share(stock_name, amount, player);

                }else{
                    double v = Market.get_sell_price(stock_name, amount);
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sell §f" +amount+
                            "§b from §f"+ stock_name+"§b for a price of §6"+df.format(v)+" \n§c/company sell "+stock_name+" "+amount);
                    confirm.put(player, "sell");

                }

            }

            if (commando.equals("pay")){
                if (args.length < 4){
                    player.sendMessage("§bplease enter /company pay <company> <player_name> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                String player_name = args[2];
                double amount = Double.parseDouble(args[3]);
                OfflinePlayer player_offline = null;

                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                    if (target.getName().equals(player_name)){
                        player_offline = target;
                    }
                }

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
                    player.sendMessage("§byou must be a CEO of this company");
                    confirm.put(player, "");
                    return true;
                }

                if (player_offline == null){
                    player.sendMessage("§bplayer doesn't exist");
                    confirm.put(player, "");
                    return true;
                }

                if (player.getName().equals(player_name)){
                    player.sendMessage("§byou can't pay out yourself");
                    confirm.put(player, "");
                    return true;
                }

                if (confirm.getOrDefault(player, "").equals("pay")){
                    confirm.put(player, "");
                    //still need refresh weekly the 20 % cap
                    if (Market.get_business(stock_name).remove_bal(amount)){
                        SignClick.getEconomy().depositPlayer(player_offline, amount);
                        player.sendMessage("§bsuccesfully paid §f"+player_name+" "+amount);
                        Player target = Bukkit.getPlayer(player_offline.getUniqueId());
                        if (target != null){
                            target.sendMessage("§bsuccesfully received §f"+amount+" §bfrom §f"+stock_name);
                        }

                    }else{
                        player.sendMessage("§bbusiness does not have enough money, or you reached your monthly spending limit\ndo §c/company spendable "+
                                stock_name+"§b to see monthly available money");
                    }

                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to pay §f" +amount+
                            "§b to §f"+ player_name+"\n§c/company pay "+stock_name+" "+player_name+" "+amount);
                    confirm.put(player, "pay");

                }

            }

            if (commando.equals("spendable")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company spendable <company>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                player.sendMessage("§b spendable money: "+df.format(Market.get_business(stock_name).get_spendable()));
            }

            if (commando.equals("support")){
                if (args.length < 3){
                    player.sendMessage("§bplease enter /company support <company> <player_name>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                String player_name = args[2];
                OfflinePlayer player_offline = null;

                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                    if (target.getName().equals(player_name)){
                        player_offline = target;
                    }
                }

                if (player_name.equals("neutral")){
                    Account acc = Market.get_account(player);
                    Market.get_business(stock_name).support_update(acc, null);
                    confirm.put(player, "");
                    player.sendMessage("§bsupport changed to §e"+player_name);
                    return true;
                }

                if (player_offline == null){
                    player.sendMessage("§bplayer doesn't exist");
                    confirm.put(player, "");
                    return true;
                }
                Account acc = Market.get_account(player);
                Market.get_business(stock_name).support_update(acc, player_offline.getUniqueId());
                player.sendMessage("§bsupport changed to §f"+player_name);

            }

            if (commando.equals("transfer")){
                if (args.length < 4){
                    player.sendMessage("§bplease enter /company transfer <company> <player_name> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                String player_name = args[2];
                OfflinePlayer player_offline = null;

                for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                    if (target.getName().equals(player_name)){
                        player_offline = target;
                    }
                }

                if (player_offline == null){
                    player.sendMessage("§bplayer doesn't exist");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.has_account(player_offline.getUniqueId())){
                    player.sendMessage("§bplayer doesn't have an account");
                    confirm.put(player, "");
                    return true;
                }

                int amount = Integer.parseInt(args[3]);

                if (confirm.getOrDefault(player, "").equals("transfer")){
                    confirm.put(player, "");
                    Account target = Market.get_account(player_offline.getUniqueId());
                    boolean suc6 = Market.get_account(player).transfer(stock_name, amount, target, player);
                    if (suc6){
                        Market.get_business(stock_name).change_share_holder(target, amount);
                        Market.get_business(stock_name).change_share_holder(Market.get_account(player), -amount);
                    }

                    //target.send_player("§byou got §f"+amount+" §bshares from §f"+player.getName());


                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to transfer §f" +amount+"§b shares to §f"+ player_name+
                            "\n§c/company transfer "+stock_name+" "+player_name+" "+amount);
                    confirm.put(player, "transfer");
                }
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

                if (!Market.has_account(target_uuid)){
                    player.sendMessage("§bplayer doesn't have an account");
                    return true;
                }

                Market.get_account(target_uuid).get_portfolio(player);

            }

            if (commando.equals("market")){
                confirm.put(player, "");
                Market.market_available(player);
            }

            if (commando.equals("get_support")){
                confirm.put(player, "");
                UUID target_uuid = null;
                if (args.length < 2){
                    player.sendMessage("§bplease enter /company get_support <company> [player_name]");
                    return true;

                }else if(args.length < 3){
                    target_uuid = player.getUniqueId();
                }else{
                    for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                        if (target.getName().equals(args[2])){
                            target_uuid = target.getUniqueId();
                        }
                    }
                }

                if (target_uuid == null){
                    player.sendMessage("§bplayer doesn't exist");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                UUID result = Market.get_business(stock_name).support.getOrDefault(target_uuid, null);
                String name;
                if (result == null){
                    name = "neutral";
                }else{
                    name = Bukkit.getOfflinePlayer(result).getName();
                }

                player.sendMessage("§bplayer supports §7"+name);
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

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to send that request");
                    confirm.put(player, "");
                    return true;
                }

                String target_stock_name = args[2].toUpperCase();
                target_stock_name = target_stock_name.toUpperCase();

                if (!Market.has_business(target_stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                double amount = Double.parseDouble(args[3]);
                int weeks = Integer.parseInt(args[4]);

                if (confirm.getOrDefault(player, "").equals("send_contract_ctc")){
                    confirm.put(player, "");
                    if (Market.get_business(target_stock_name).comp_name_pending == null){
                        Market.get_business(stock_name).send_offer_comp_contract(target_stock_name, amount, weeks, reason);
                    }else{
                        player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
                    }

                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                            +"§b \n for an amount of §f"+ amount
                            +"§b \n for a time of §f"+ weeks+
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

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to sign that request");
                    confirm.put(player, "");
                    return true;
                }

                Company comp = Market.get_business(stock_name);
                if (comp.spendable < comp.comp_amount_pending){
                    player.sendMessage("§bcan't sign contract because lack of weekly spendable funds");
                    confirm.put(player, "");
                    return true;
                }


                if (confirm.getOrDefault(player, "").equals("sign_contract_ctc")){
                    confirm.put(player, "");
                    comp.accept_offer_comp_contract();
                    player.sendMessage("§bcontract confirmed");

                }else{
                    DecimalFormat df = new DecimalFormat("###,###,###");
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" +comp.comp_name_pending
                            +"§b \nfor an amount of §f"+ df.format(comp.comp_amount_pending)
                            +"§b \nfor a time of §f"+ comp.comp_weeks_pending+
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

                if (!Market.has_business(stock_name)){
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
                player.sendMessage("§b books money: "+df.format(Market.get_books(stock_name)));
            }

            if (commando.equals("send_contract_ctp")){
                if (args.length < 4){
                    player.sendMessage("§bplease enter /com.company send_contract_ctp <othercompany> <amount> <weeks> [reason]");
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

                if (!Market.has_business(target_stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                double amount = Double.parseDouble(args[2]);
                int weeks = Integer.parseInt(args[3]);

                if (confirm.getOrDefault(player, "").equals("send_contract_ctp")){
                    confirm.put(player, "");
                    if (Market.get_business(target_stock_name).player_name_pending == null){
                        Market.get_business(target_stock_name).receive_offer_player_contract(player.getUniqueId().toString(), amount, weeks, reason);
                    }else{
                        player.sendMessage("§ccompany still has another offer pending, try again in 2 minutes");
                    }

                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +target_stock_name
                            +"§b \n for an amount of §f"+ amount
                            +"§b \n for a time of §f"+ weeks+
                            " weeks \n§c/com.company send_contract_ctp "+target_stock_name+" "+amount+ " "+ weeks);
                    confirm.put(player, "send_contract_ctp");
                }

            }

            if (commando.equals("sign_contract_ctp")){
                if (args.length < 2){
                    player.sendMessage("§bplease enter /com.company sign_contract_ctp <owncompany>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
                    player.sendMessage("§byou must be CEO to sign that request");
                    confirm.put(player, "");
                    return true;
                }

                Company comp = Market.get_business(stock_name);
                if (comp.spendable < comp.comp_amount_pending){
                    player.sendMessage("§bcan't sign contract because lack of weekly spendable funds");
                    confirm.put(player, "");
                    return true;
                }

                if (comp.is_owner(UUID.fromString(comp.player_name_pending)) && player.getUniqueId().equals(UUID.fromString(comp.player_name_pending))){
                    player.sendMessage("§byou can't' make a contract with yourself");
                    confirm.put(player, "");
                    return true;
                }


                if (confirm.getOrDefault(player, "").equals("sign_contract_ctp")){
                    confirm.put(player, "");
                    comp.accept_offer_player_contract();
                    player.sendMessage("§bcontract confirmed");

                }else{
                    DecimalFormat df = new DecimalFormat("###,###,###");

                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" + Bukkit.getOfflinePlayer(UUID.fromString(comp.player_name_pending)).getName()
                            +"§b \nfor an amount of §f"+ df.format(comp.player_amount_pending)
                            +"§b \nfor a time of §f"+ comp.player_weeks_pending+
                            " weeks \n§c/com.company sign_contract_ctp "+stock_name);
                    confirm.put(player, "sign_contract_ctp");
                }
            }

            if (commando.equals("send_contract_ptc")){
                if (args.length < 5){
                    player.sendMessage("§bplease enter /com.company send_contract_ptc <owncompany> <player> <amount> <weeks> [reason]");
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

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
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

                if (!Market.has_account(target_uuid)){
                    player.sendMessage("§bplayer doesn't have an account");
                    return true;
                }

                double amount = Double.parseDouble(args[3]);
                int weeks = Integer.parseInt(args[4]);

                if (confirm.getOrDefault(player, "").equals("send_contract_ptc")){
                    confirm.put(player, "");
                    if (Market.get_account(target_uuid).comp_name_pending == null){
                        Market.get_account(target_uuid).receive_offer_comp_contract(stock_name, amount, weeks, reason);
                    }else{
                        player.sendMessage("§cplayer still has another offer pending, try again in 2 minutes");
                    }


                }else{
                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to send a contract request to §f" +args[2]
                            +"§b \n for an amount of §f"+ amount
                            +"§b \n for a time of §f"+ weeks+
                            " weeks \n§c/com.company send_contract_ptc "+stock_name+" "+args[2]+" "+amount+ " "+ weeks);
                    confirm.put(player, "send_contract_ptc");
                }

            }

            if (commando.equals("sign_contract_ptc")){

                Account acc = Market.get_account(player);

                if (acc.get_bal() < acc.comp_amount_pending){
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

                    player.sendMessage("§bplease re-enter your command to confirm\nthat you want to sign a contract (§cYOU PAY THEM§b) requested from §f" + acc.comp_name_pending
                            +"§b \nfor an amount of §f"+ df.format(acc.comp_amount_pending)
                            +"§b \nfor a time of §f"+ acc.comp_weeks_pending+
                            " weeks \n§c/com.company sign_contract_ptc");
                    confirm.put(player, "sign_contract_ptc");
                }
            }

            if (commando.equals("get_buy_price")){
                confirm.put(player, "");
                int amount = 1;
                if (args.length < 2){
                    player.sendMessage("§bplease enter /com.company get_buy_price <com.company> [amount]");
                    return true;
                }else if (args.length == 3){
                    amount = Integer.parseInt(args[2]);
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                player.sendMessage("§f"+amount+"§b share(s) costs §f"+ df.format(Market.get_buy_price(stock_name, amount)));
            }

            if (commando.equals("get_sell_price")){
                confirm.put(player, "");
                int amount = 1;
                if (args.length < 2){
                    player.sendMessage("§bplease enter /com.company get_sell_price <com.company> [amount]");
                    return true;
                }else if (args.length == 3){
                    amount = Integer.parseInt(args[2]);
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                player.sendMessage("§f"+amount+"§b share(s) costs §f"+ df.format(Market.get_sell_price(stock_name, amount)));
            }

            if (commando.equals("get_contracts")){
                confirm.put(player, "");
                if (args.length < 2){
                    player.sendMessage("§bplease enter /com.company get_contracts <com.company>");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
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
                    player.sendMessage("§bplease enter /com.company transact <com.company> <target_company> <amount>");
                    confirm.put(player, "");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                String target_stock_name = args[2].toUpperCase();
                target_stock_name = target_stock_name.toUpperCase();

                if (!Market.has_business(target_stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                double amount = Double.parseDouble(args[3]);

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
                    player.sendMessage("§byou must be a CEO of this com.company");
                    confirm.put(player, "");
                    return true;
                }

                if (confirm.getOrDefault(player, "").equals("transact")){
                    confirm.put(player, "");
                    //still need refresh weekly the 20 % cap
                    if (Market.get_business(stock_name).remove_bal(amount)){
                        Company comp_target = Market.get_business(target_stock_name);
                        comp_target.add_bal(amount);
                        player.sendMessage("§bsuccesfully paid §f"+target_stock_name+" "+amount);
                        comp_target.send_owner("§bsuccesfully received §f"+amount+" §bfrom §f"+stock_name);

                    }else{
                        player.sendMessage("§bbusiness does not have enough money, or you reached your monthly spending limit\ndo §c/com.company spendable "+
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
                    player.sendMessage("§bplease enter /com.company add_custom <Company> <Texture> <Item>");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
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

                Company comp = Market.get_business(stock_name);
                comp.patent_upgrades.add(new PatentUpgradeCustom(args[2], item));
            }

            if (commando.equals("particles")){
                confirm.put(player, "");

                if (!player.hasPermission("signclick.staff")){
                    player.sendMessage("§byou must be staff");
                    return true;
                }

                if (args.length < 2){
                    player.sendMessage("§bparticles are "+ Market.show_particles);
                    return true;
                }else{
                    Market.show_particles = Objects.equals(args[1], "TRUE");
                }


            }

            if (commando.equals("open_trade")){
                confirm.put(player, "");

                if (args.length < 2){
                    player.sendMessage("§bplease enter /com.company open_trade <Company> [TRUE/FALSE]");
                    return true;
                }

                String stock_name = args[1].toUpperCase();
                stock_name = stock_name.toUpperCase();

                if (!Market.has_business(stock_name)){
                    player.sendMessage("§bbusiness name is invalid");
                    confirm.put(player, "");
                    return true;
                }

                if (args.length < 3){
                    player.sendMessage("§bopen trade is "+ Market.get_business(stock_name).open_trade);
                    return true;
                }

                if (!Market.get_business(stock_name).is_owner(player.getUniqueId())){
                    player.sendMessage("§byou must be a CEO of this com.company");
                    confirm.put(player, "");
                    return true;
                }

                boolean to_open = Objects.equals(args[2], "TRUE");
                Market.get_business(stock_name).open_trade = to_open;

                if (!to_open){
                    Market.set_market_amount(stock_name, 0);
                }else{

                    Market.set_total(stock_name, Market.get_total(stock_name)-Market.get_market_amount(stock_name));
                    Market.set_market_amount(stock_name, 0);
                }

                player.sendMessage("§bopen trade set yo "+ Market.get_business(stock_name).open_trade);

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

package com.klanting.signclick.economy;

import com.klanting.signclick.SignClick;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public class Board {
    /**
    * Keep track of the board of a given company
    * */

    private static final List<String> rankingOrder = List.of("CEO", "CFO", "CTO");

    public int getBoardSeats() {
        return boardSeats;
    }

    /*
    * Stores how many seats are on the company board
    * */
    private int boardSeats;

    /*
    * reference to company ownership information
    * */
    private transient CompanyOwnerManager companyOwnerManager = null;

    public Double getSalaryMap(UUID boardMember, String position) {
        return salaryMap.get(position).getOrDefault(boardMember, 0.0);
    }

    private Map<String, Map<UUID, Double>> salaryMap = new HashMap<>();

    public List<UUID> getBoardSupport(UUID shareholder) {
        return boardSupport.getOrDefault(shareholder, new ArrayList<>());
    }

    /*
    * Keep track of which board members are supported by a given shareholder
    * */
    private Map<UUID, List<UUID>> boardSupport = new HashMap<>();

    public UUID getChiefSupport(String position, UUID boardMember) {
        return chiefSupport.get(position).get(boardMember);
    }

    /*
    * Keep track for each Chief position, which board member supports which person
    * */
    private Map<String, Map<UUID, UUID>> chiefSupport = new HashMap<>();

    public boolean isCurrentChief(UUID uuid) {
        return currentChief.containsValue(uuid);
    }

    /*
    * Store last chief position, to resolve support changes, when tied occurs
    * */
    private Map<String, UUID> currentChief = new HashMap<>();

    public void addBoardSupport(UUID shareHolder, UUID boardMember){
        /*
        * Add a person to be supported by the shareholder as board member.
        * Each shareholder can choose who he/she wants to support as board member,
        * and the board influence will be evenly spread among the people being supported
        * */
        List<UUID> boardMembers = boardSupport.getOrDefault(shareHolder, new ArrayList<>());
        boardMembers.add(boardMember);
        boardSupport.put(shareHolder, boardMembers);
        checkChiefVote();
    }

    public void removeBoardSupport(UUID shareHolder, UUID boardMember){
        /*
        * Remove a person from being supported by the shareholder as board member.
        * */
        List<UUID> boardMembers = boardSupport.getOrDefault(shareHolder, new ArrayList<>());
        boardMembers.remove(boardMember);
        boardSupport.put(shareHolder, boardMembers);
        checkChiefVote();
    }


    public UUID getChief(String position){
        /*
        * Get the user in the provided chief position
        * */
        return currentChief.get(position);
    }

    public UUID getChiefPermission(String position){
        /*
         * Get the user in the provided chief position
         * */

        UUID uuid = currentChief.get(position);

        if (uuid == null){
            uuid = currentChief.get("CEO");
        }

        return uuid;
    }

    public void setCompanyOwnerManager(CompanyOwnerManager companyOwnerManager){
        /*
        * Needed for gson add right reference
        * */
        this.companyOwnerManager = companyOwnerManager;
    }

    public Board() {
    }


    public Board(CompanyOwnerManager companyOwnerManager){
        /*
        * Create a company board
        * */
        this.companyOwnerManager = companyOwnerManager;

        if (companyOwnerManager == null){
            return;
        }

        boardSeats = 2;

        chiefSupport.put("CEO", new HashMap<>());
        chiefSupport.put("CTO", new HashMap<>());
        chiefSupport.put("CFO", new HashMap<>());

        currentChief.put("CEO", null);
        currentChief.put("CTO", null);
        currentChief.put("CFO", null);

        salaryMap.put("CEO", new HashMap<>());
        salaryMap.put("CTO", new HashMap<>());
        salaryMap.put("CFO", new HashMap<>());

        UUID owner = companyOwnerManager.getShareHolders().keySet().iterator().next();
        /*
         * Make owner only board member with support
         * */
        addBoardSupport(owner, owner);
        /*
         * Make owner CEO
         * */
        boardChiefVote(owner, "CEO", owner);
    }

    public void boardChiefVote(UUID boardMember, String position, UUID chiefTarget){
        /*
        * As a board member, change who you vote for to have the given Chief position
        * */
        assert List.of("CEO", "CTO", "CFO").contains(position);

        chiefSupport.get(position).put(boardMember, chiefTarget);
        checkChiefVote(position);
    }

    public void paySalaries(CompanyI comp){
        for(String position: rankingOrder){
            UUID user = currentChief.get(position);
            if (user == null){
                continue;
            }
            Double amount = getSalary(position);
            boolean suc6 = comp.removeBal(amount);
            if (suc6){
                SignClick.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(user), amount);
            }
        }
    }

    public void boardChangeSalary(UUID boardMember, String position, Double amount){
        Double newAmount = salaryMap.get(position).getOrDefault(boardMember, 0.0)+amount;
        newAmount = Math.max(0, newAmount);
        newAmount = Math.min(SignClick.getConfigManager().getConfig("companies.yml").getDouble("maxChiefSalary"), newAmount);


        salaryMap.get(position).put(boardMember, newAmount);
    }

    public Double getSalary(String position){

        List<UUID> boardMembers = getBoardMembers();

        Double avg = 0.0;
        for (Map.Entry<UUID, Double> chiefVote: salaryMap.get(position).entrySet()){
            if (!boardMembers.contains(chiefVote.getKey())){
                continue;
            }
            avg += salaryMap.get(position).get(chiefVote.getKey());
        }

        return avg/boardMembers.size();

    }

    public void checkChiefVote(){
        /*
        * Check Board votes for each chief position
        * */
        for (String position: List.of("CEO", "CTO", "CFO")){
            checkChiefVote(position);
        }
    }

    public List<Pair<UUID, Double>> chiefRanking(String position){
        /*
        * Make a ranking for the chief position
        * */
        Map<UUID, Double> chiefRanking = new HashMap<>();

        /*
         * Add small stat to current chief so in tied he wins
         * */
        if (currentChief.get(position) != null){
            chiefRanking.put(currentChief.get(position), 0.5);
        }

        List<UUID> boardMembers = getBoardMembers();

        for (Map.Entry<UUID, UUID> chiefVote: chiefSupport.get(position).entrySet()){
            if (!boardMembers.contains(chiefVote.getKey())){
                continue;
            }
            chiefRanking.put(chiefVote.getValue(), chiefRanking.getOrDefault(chiefVote.getValue(), 0.0)+1.0);
        }

        if (chiefRanking.entrySet().isEmpty()){
            return new ArrayList<>();
        }

        return chiefRanking.entrySet().stream().
                map(e -> Pair.of(e.getKey(), e.getValue())).
                sorted(Comparator.comparingDouble((Pair<UUID, Double> e) -> e.getRight()).reversed()).
                collect(Collectors.toList());

    }

    private void checkChiefVote(String position){
        /*
         * Check Board votes for the given chief position
         * The person with the most votes becomes the new chief, but in case of a tied (between user and current chief),
         * the current chief remains.
         * */
        List<Pair<UUID, Double>> newChiefPairList = chiefRanking(position);

        /*
        * Remove higher ranking from consideration
        * */
        int chiefPos = rankingOrder.indexOf(position);
        for (int i=0; i<chiefPos; i++){
            int finalI = i;
            newChiefPairList = newChiefPairList.stream().filter(s -> !s.getLeft().equals(
                    currentChief.get(rankingOrder.get(finalI)))
                    ).toList();
        }

        if (newChiefPairList.isEmpty()){
            currentChief.put(position, null);
            return;
        }

        Pair<UUID, Double> newChiefPair = newChiefPairList.get(0);

        UUID newChief = newChiefPair.getKey();

        currentChief.put(position, newChief);

        /*
        * Check that person has not lower position, if so, remove this user from this position
        * */
        for (int i=chiefPos+1; i<rankingOrder.size(); i++){
            if (currentChief.get(rankingOrder.get(i)) == null || currentChief.get(rankingOrder.get(i)).equals(newChief)){
                currentChief.put(rankingOrder.get(i), null);
                checkChiefVote(rankingOrder.get(i));
            }
        }
    }

    public void setBoardSeats(int seats){
        /*
        * Change the total amount of board seats that exists
        * */
        boardSeats = seats;
    }

    public List<UUID> getBoardMembers(){
        return getBoardMembersWeight().stream().map(Pair::getLeft).toList();
    }

    public List<Pair<UUID, Double>> getBoardMembersWeight(){
        /*
         * Get the board members best supported by the shareholders
         * */

        /*
         * Make mapping board Member -> amount of shares support this board member has.
         * */
        Map<UUID, Double> potentialBoardMemberMap = new HashMap<>();

        for (Map.Entry<UUID, List<UUID>> boardSupportEntry: boardSupport.entrySet()){
            for (UUID potentialBoardMember : boardSupportEntry.getValue()){

                /*
                 * Check the influence of a shareholder
                 * */
                double shareHolderWeight = ((double) companyOwnerManager.getShareHolders().
                        getOrDefault(boardSupportEntry.getKey(), 0))/boardSupportEntry.getValue().size();

                /*
                 * Determine the shareholder impact and add this impact to the current votes for the given board
                 * members
                 * */
                double newValue = potentialBoardMemberMap.getOrDefault(potentialBoardMember, 0.0)+shareHolderWeight;
                potentialBoardMemberMap.put(potentialBoardMember, newValue);
            }
        }

        return potentialBoardMemberMap.entrySet().
                stream()
                .filter(e -> e.getValue() > 0.0)
                .map(e -> Pair.of(e.getKey(), e.getValue()/companyOwnerManager.getTotalShares())).
                sorted(Comparator.comparingDouble((Pair<UUID, Double> p) -> p.getRight()).reversed()).
                collect(Collectors.toList()).subList(0, Math.min(boardSeats, potentialBoardMemberMap.keySet().size()));
    }
}

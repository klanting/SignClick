package com.klanting.signclick.economy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Board {
    /**
    * Keep track of the board of a given company
    * */

    /*
    * Stores how many seats are on the company board
    * */
    private int boardSeats;

    /*
    * reference to company ownership information
    * */
    private final CompanyOwnerManager companyOwnerManager;

    /*
    * Keep track of which board members are supported by a given shareholder
    * */
    private final Map<UUID, List<UUID>> boardSupport = new HashMap<>();

    /*
    * Keep track for each Chief position, which board member supports which person
    * */
    private final Map<String, Map<UUID, UUID>> chiefSupport = new HashMap<>();

    /*
    * Store last chief position, to resolve support changes, when tied occurs
    * */
    private final Map<String, UUID> currentChief = new HashMap<>();

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


    public Board(CompanyOwnerManager companyOwnerManager){
        /*
        * Create a company board
        * */
        this.companyOwnerManager = companyOwnerManager;

        assert companyOwnerManager.getShareHolders().keySet().size() == 1;

        boardSeats = 2;

        chiefSupport.put("CEO", new HashMap<>());
        chiefSupport.put("CTO", new HashMap<>());
        chiefSupport.put("CFO", new HashMap<>());

        currentChief.put("CEO", null);
        currentChief.put("CTO", null);
        currentChief.put("CFO", null);

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

    private void checkChiefVote(){
        /*
        * Check Board votes for each chief position
        * */
        for (String position: List.of("CEO", "CTO", "CFO")){
            checkChiefVote(position);
        }
    }

    private void checkChiefVote(String position){
        /*
         * Check Board votes for the given chief position
         * The person with the most votes becomes the new chief, but in case of a tied (between user and current chief),
         * the current chief remains.
         * */
        Map<UUID, Double> chiefRanking = new HashMap<>();

        /*
         * Add small stat to current chief so in tied he wins
         * */
        if (currentChief.get(position) != null){
            chiefRanking.put(currentChief.get(position), 0.5);
        }

        List<UUID> boardMembers = getBoardMembers();

        for (UUID chiefVote: chiefSupport.get(position).values()){
            if (!boardMembers.contains(chiefVote)){
                continue;
            }
            chiefRanking.put(chiefVote, chiefRanking.getOrDefault(chiefVote, 0.0)+1.0);
        }

        if (chiefRanking.entrySet().isEmpty()){
            return;
        }

        UUID newChief = chiefRanking.entrySet().stream().
                map(e -> Pair.of(e.getKey(), e.getValue())).
                sorted(Comparator.comparingDouble((Pair<UUID, Double> e) -> e.getRight()).reversed()).
                collect(Collectors.toList()).get(0).getKey();

        currentChief.put(position, newChief);
    }

    public void setBoardSeats(int seats){
        /*
        * Change the total amount of board seats that exists
        * */
        boardSeats = seats;
    }

    public List<UUID> getBoardMembers(){
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
                stream().map(e -> Pair.of(e.getKey(), e.getValue())).
                sorted(Comparator.comparingDouble((Pair<UUID, Double> p) -> p.getRight()).reversed()).
                collect(Collectors.toList()).subList(0, Math.min(boardSeats, potentialBoardMemberMap.keySet().size())).
                stream().map(Pair::getLeft).toList();
    }
}

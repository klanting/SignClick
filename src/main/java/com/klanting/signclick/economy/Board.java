package com.klanting.signclick.economy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private int boardSeats;

    private final CompanyOwnerManager companyOwnerManager;

    private final Map<UUID, List<UUID>> boardSupport = new HashMap<>();

    private final Map<String, Map<UUID, UUID>> chiefSupport = new HashMap<>();

    /*
    * Store last chief position, to resolve support changes, when tied occurs
    * */
    private final Map<String, UUID> currentChief = new HashMap<>();

    public void addBoardSupport(UUID shareHolder, UUID boardMember){
        List<UUID> boardMembers = boardSupport.getOrDefault(shareHolder, new ArrayList<>());
        boardMembers.add(boardMember);
        boardSupport.put(shareHolder, boardMembers);
        checkChiefVote();
    }

    public void removeBoardSupport(UUID shareHolder, UUID boardMember){
        List<UUID> boardMembers = boardSupport.getOrDefault(shareHolder, new ArrayList<>());
        boardMembers.remove(boardMember);
        boardSupport.put(shareHolder, boardMembers);
        checkChiefVote();
    }


    public UUID getChief(String position){
        return currentChief.get(position);
    }


    public Board(CompanyOwnerManager companyOwnerManager){

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

        assert List.of("CEO", "CTO", "CFO").contains(position);

        chiefSupport.get(position).put(boardMember, chiefTarget);
        checkChiefVote(position);
    }

    private void checkChiefVote(){
        for (String position: List.of("CEO", "CTO", "CFO")){
            checkChiefVote(position);
        }
    }

    private void checkChiefVote(String position){
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
        boardSeats = seats;
    }

    public List<UUID> getBoardMembers(){

        /*
        * Make mapping board Member -> amount of shares support this board member has.
        * This makes sure we can get the
        * */
        Map<UUID, Double> potentialBoardMemberMap = new HashMap<>();

        for (Map.Entry<UUID, List<UUID>> boardSupportEntry: boardSupport.entrySet()){
            for (UUID potentialBoardMember : boardSupportEntry.getValue()){

                double shareHolderWeight = ((double) companyOwnerManager.getShareHolders().
                        getOrDefault(boardSupportEntry.getKey(), 0))/boardSupportEntry.getValue().size();

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

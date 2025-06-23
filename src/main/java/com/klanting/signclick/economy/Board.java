package com.klanting.signclick.economy;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private int boardSeats;

    private final CompanyOwnerManager companyOwnerManager;

    private final Map<UUID, List<UUID>> boardSupport = new HashMap<>();


    public Board(CompanyOwnerManager companyOwnerManager){

        this.companyOwnerManager = companyOwnerManager;
        boardSeats = 2;
    }

    public void setBoardSeats(int seats){
        boardSeats = seats;
    }

    public List<Pair<UUID, Double>> getBoardMembers(){

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
                collect(Collectors.toList()).subList(0, boardSeats);
    }
}

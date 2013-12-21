package com.stackexchange.codereview.rolfl.sudoku_wec3;


public class StrategyHiddenTwinElimination implements SudokuStrategy {

    private final Grid grid;
    public StrategyHiddenTwinElimination(Grid grid) {
        this.grid = grid;
    }


    @Override
    public boolean strategise() {
        // if two numbers appear in only two cells in a region,
        // then any other numbers in those cells can be eliminated.
        for (GroupRelated group : grid.getRelatedGroups()) {
            for (int a = 0; a < grid.getDimension(); a++) {
                if (group.groupContains(a) == null) {
                    for (int b = a + 1; b < grid.getDimension(); b++) {
                        if (group.groupContains(b) == null) {
                            if(processGroupPair(group, a, b)) {
                                // twin....
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private boolean processGroupPair(GroupRelated group, int a, int b) {
        Cell[] cells = new Cell[2];
        int cnt = 0;
        for (Cell c : group.getCells()) {
            if (!c.isSet() && c.canBe(a)) {
                if (cnt >= cells.length) {
                    return false;
                }
                cells[cnt++] = c;
            }
        }
        if (cnt < 2) {
            // should never happen....
            return false;
        }
        for (Cell c : group.getCells()) {
            if (!c.isSet() && c.canBe(b)) {
                if (!(cells[0] == c || cells[1] == c) ) {
                    return false;
                }
            }
        }
        boolean affected = false;
        for (Cell cell : cells) {
            for (int digit : cell.getDigitSet().getRemaining()) {
                if (!(digit == a || digit == b)) {
                    System.out.println("StrategyHiddenTwinElimination eliminated " + digit + " from " + cell);
                    cell.eliminate(Source.Strategy, digit);
                    
                    affected = true;
                }
            }
        }
        return affected;
    }


}

package com.stackexchange.codereview.rolfl.sudoku_wec3;

public class StrategyOnlyPlaceInGroup implements SudokuStrategy {

    private final Grid grid;
    public StrategyOnlyPlaceInGroup(final Grid grid) {
        this.grid = grid;
    }

    @Override
    public boolean strategise() {
        for (GroupRelated related : grid.getRelatedGroups()) {
            if (findPotentialDigit(related)) {
                return true;
            }
        }
        return false;
   }

    private boolean findPotentialDigit(GroupRelated related) {
        for (int digit = 0; digit < grid.getDimension(); digit++) {
            Cell cell = findPotentialCell(related, digit);
            if (cell != null) {
                cell.setValue(Source.Strategy, digit);
                System.out.println("OnlyOneCanBe solved " + cell);
                return true;
            }
        }
        return false;
    }

    private Cell findPotentialCell(GroupRelated related, int digit) {
        Cell candidate = null;
        for (Cell c : related.getCells()) {
            if (c.isSet() && c.getValue() == digit) {
                return null;
            }
            if (c.canBe(digit)) {
                if (candidate != null) {
                    // cannot have two candidates with the same solution.
                    return null;
                }
                candidate = c;
            }
        }
        return candidate;
    }

}

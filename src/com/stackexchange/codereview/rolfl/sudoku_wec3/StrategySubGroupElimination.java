package com.stackexchange.codereview.rolfl.sudoku_wec3;

import java.util.Arrays;

public class StrategySubGroupElimination implements SudokuStrategy {

    private final Grid grid;
    public StrategySubGroupElimination(Grid grid) {
        this.grid = grid;
    }


    @Override
    public boolean strategise() {
        // every block has a set of rows and columns....
        // if we can prove that, in a block, that a number can only appear in one
        // of these rows, or columns, then we can eliminate that number from the rest
        // of the row (or column);
        for (int b = 0; b < grid.getDimension(); b++) {
            Block block = grid.getBlock(b);
            for (int digit = 0; digit < grid.getDimension(); digit++) {
                if (processBlock(block, digit)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean processBlock(Block block, int digit) {
        if (block.groupContains(digit) != null) {
            // already a full solution for this digit.
            return false;
        }
        Cell[] possibles = new Cell[grid.getDimension()];
        int cnt = 0;
        for  (Cell c : block.getCells()) {
            if (!c.isSet() && c.canBe(digit)) {
                possibles[cnt++] = c;
            }
        }
        if (cnt <= 0) {
            return false;
        }
        
        GroupRelated sameroworcol = sameRowOrCol(Arrays.copyOf(possibles, cnt));
        if (sameroworcol == null) {
            return false;
        }
        boolean affect = false;
        for (Cell src : sameroworcol.getCells()) {
            if (!src.isSet() && src.getBlock() != block) {
                if (src.eliminate(Source.Strategy, digit)) {
                    affect = true;
                    System.out.println("SubGroupElimination Eliminated " + digit + " on " + src);
                }
            }
        }
        return affect;
    }


    private GroupRelated sameRowOrCol(Cell[] cells) {
        if (cells.length <= 1) {
            return null;
        }
        Column col = cells[0].getCol();
        Row row = cells[0].getRow();
        for (Cell c : cells) {
            if (row != null && c.getRow() != row) {
                row = null;
            }
            if (col != null && c.getCol() != col) {
                col = null;
            }
        }
        return row == null ? col : row;
    }

}

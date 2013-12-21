package com.stackexchange.codreview.rolfl.sudoku_wec3;

import java.util.Arrays;

public class GroupRelated {
    private final Cell[] cells;
    private final int id;
    private final Grouping grouping;

    public GroupRelated(Grouping grouping, int id, int dimension) {
        cells = new Cell[dimension];
        this.grouping = grouping;
        this.id = id;
    }

    public final int getId() {
        return id;
    }
    
    void setCell(final int pos, final Cell cell) {
        if (cells[pos] != null) {
            throw new IllegalStateException("Unable to set previoulsy-set cell " + pos);
        }
        cells[pos] = cell;
    }

    public final Cell getCell(int i) {
        return cells[i];
    }
    
    public final Cell groupContains(final int digit) {
        for (Cell c : cells) {
            if (c.isSet() && digit == c.getValue()) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public final String toString() {
        int[] vals = new int[cells.length];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = cells[i].isSet() ? cells[i].getValue() : SudokuRules.BLANK;
        }
        return String.format("%s %d %s", grouping, id, SudokuRules.getSudokuDigits(vals));
    }
    
    public Cell[] getCells() {
        return Arrays.copyOf(cells, cells.length);
    }

}

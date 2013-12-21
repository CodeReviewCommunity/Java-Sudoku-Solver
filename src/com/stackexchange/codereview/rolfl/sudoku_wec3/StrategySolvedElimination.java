package com.stackexchange.codereview.rolfl.sudoku_wec3;

import java.util.HashSet;
import java.util.Set;

public class StrategySolvedElimination extends SolvedListeningStrategy {

    public StrategySolvedElimination(Grid grid) {
        super(grid);
    }

    @Override
    public boolean strategise() {
        Cell cell = null;
        Set<Cell> affects = new HashSet<>();
        
        while( (cell = retrieveSolvedSince()) != null) {
            affects.clear();
            for (Cell r : cell.getRow().getCells()) {
                if (!r.isSet()) affects.add(r);
            }
            for (Cell c : cell.getCol().getCells()) {
                if (!c.isSet()) affects.add(c);
            }
            for (Cell b : cell.getBlock().getCells()) {
                if (!b.isSet()) affects.add(b);
            }
            affects.remove(cell);
            boolean anything = false;
            for (Cell c : affects) {
                if (c.eliminate(Source.Strategy, cell.getValue())) {
                    anything = true;
                    System.out.println("Elimination solved " + c);
                }
            }
            if (anything) {
                return true;
            }
        }
        return false;
    }

}

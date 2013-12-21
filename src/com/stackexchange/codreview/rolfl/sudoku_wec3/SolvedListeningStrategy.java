package com.stackexchange.codreview.rolfl.sudoku_wec3;

import java.util.LinkedList;
import java.util.Queue;

public abstract class SolvedListeningStrategy implements SolutionListener, SudokuStrategy {
    
    private final Grid grid;
    
    private final Queue<Cell> pending = new LinkedList<>();
    

    public SolvedListeningStrategy(Grid grid) {
        this.grid = grid;
        grid.addSolutionListener(this);
    }

    @Override
    public void solved(Cell cell) {
        pending.add(cell);
    }
    
    protected Cell retrieveSolvedSince() {
        return pending.poll();
    }

    public Grid getGrid() {
        return grid;
    }
}

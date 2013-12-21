package com.stackexchange.codereview.rolfl.sudoku_wec3;

public class SudokuSolver {

    final Grid grid;
    final SudokuStrategy[] strategies;
    public SudokuSolver(Grid grid) {
        this.grid = grid;
        strategies = new SudokuStrategy[] {
                new StrategySolvedElimination(grid),
                new StrategyOnlyPlaceInGroup(grid),
                new StrategySubGroupElimination(grid),
                new StrategyHiddenTwinElimination(grid),
                new StrategyBruteForce(grid),
        };
    }
    
    public boolean solveAll() {
        boolean progress = true;
        while (progress && !grid.isSolved()) {
            progress = false;
            for (SudokuStrategy strategy : strategies) {
                if (strategy.strategise()) {
                    progress = true;
                    break;
                }
            }
        }
        return grid.isSolved();
    }

}

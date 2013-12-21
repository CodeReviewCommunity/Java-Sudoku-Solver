package com.stackexchange.codereview.rolfl.sudoku_wec3;

import com.stackexchange.codereview.rolfl.sudoku_wec3.brute.RecursiveBruteSolver;

public class StrategyBruteForce  implements SudokuStrategy {

    private final Grid grid;
    public StrategyBruteForce(final Grid grid) {
        this.grid = grid;
    }

    @Override
    public boolean strategise() {
        System.out.println("BruteForcing Solution!!!!");
        // ensure all references/solved things are sorted out:
        
        // make sure the puzzle's elimination system is up to date...
        StrategySolvedElimination strat = new StrategySolvedElimination(grid);
        while (strat.strategise());
        
        System.out.println(GridToText.displayAsString(grid));
        final int dim = grid.getDimension();
        int[][][] digits = new int[dim][dim][];
        System.out.println("    int[][][] puzzle = new int[][][] {");
        for (int r = 0; r < dim; r++) {
            StringBuilder sb = new StringBuilder("                    { ");
            for (int c = 0; c < dim; c++) {
                digits[r][c] = grid.get(r, c).getDigitSet().getRemaining();
                sb.append("{ ");
                for (int d : digits[r][c]) {
                    sb.append(d + ", ");
                }
                sb.append("},");
            }
            sb.append("  },");
            System.out.println(sb.toString());
        }
        System.out.println("            };");
        
        RecursiveBruteSolver solver = new RecursiveBruteSolver(dim, digits);
        int[][][] sols = solver.solve();
        if (sols.length == 0) {
            throw new InvalidatesSudokuExcpeption("No solutions were forced!");
        }
        if (sols.length > 1) {
            for (int[][] sol : sols) {
                Grid tmp = new Grid(sol.length);
                for (int r = 0; r < sol.length; r++) {
                    for (int c = 0; c < sol[r].length; c++) {
                        tmp.get(r, c).setValue(Source.Player, sol[r][c]);
                    }
                }
                System.out.println(GridToText.displayAsString(tmp));
            }
            throw new InvalidatesSudokuExcpeption("Multiple solutions were forced!");
        }
        
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim; c++) {
                if (!grid.get(r, c).isSet()) {
                    grid.get(r, c).setValue(Source.Force, sols[0][r][c]);
                    return true;
                }
            }
        }
        
           
        return false;
    }

}

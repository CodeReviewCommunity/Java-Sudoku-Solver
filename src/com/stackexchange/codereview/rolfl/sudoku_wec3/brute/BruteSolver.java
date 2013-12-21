package com.stackexchange.codereview.rolfl.sudoku_wec3.brute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.stackexchange.codereview.rolfl.sudoku_wec3.DigitSet;

public class BruteSolver {
    
    private final DigitSet[][] digits;
    private final int dimension;

    public BruteSolver(int dimension, DigitSet[][] digits) {
        this.digits = digits;
        this.dimension = dimension;
    }
    
    public Solution[] solve() {
        List<Solution> solutions = new ArrayList<>();
        final int flatsize = dimension * dimension;
        final int[][] followings = new int[flatsize][];
        
        final DigitSet[] flattened = new DigitSet[flatsize];
        final DigitSet[] stacksets = new DigitSet[flatsize];
        
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                int flat = r * dimension + c;
                followings[flat] = buildFollowing(r, c);
                flattened[flat] = digits[r][c];
                stacksets[flat] = digits[r][c].duplicate();
            }
        }
        
        final int[][] remaining = new int[flatsize][];
        final int[] rempos      = new int[flatsize];
        rempos[0] = -1;
        remaining[0] = flattened[0].getRemaining();
        
        int depth = 0;
        while (depth >= 0) {
            if (depth == stacksets.length) {
                // solution....
                solutions.add(buildSolution(stacksets));
                depth--;
            } else {
                if (rempos[depth] >= 0) {
                    // undo all stack mods
                    final int digit = remaining[depth][rempos[depth]];
                    for (int f = 0; f < followings[depth].length; f++) {
                        if (flattened[f].isPossible(digit)) {
                            stacksets[f].restore(digit);
                        }
                    }
                }
                int nxt = ++rempos[depth];
                if (nxt >= remaining[depth].length) {
                    // run out of things at this level
                    for (int digit : remaining[depth]) {
                        stacksets[depth].restore(digit);
                    }
                    depth--;
                } else {
                    int digit = remaining[depth][nxt];
                    stacksets[depth].coerce(digit);
                    boolean ok = true;
                    for (int f : followings[depth]) {
                        stacksets[f].eliminate(digit);
                        if (stacksets[f].isEmpty()) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        depth++;
                        rempos[depth] = -1;
                        remaining[depth] = stacksets[depth].getRemaining();
                    }
                    
                }
            }
        }
        return solutions.toArray(new Solution[solutions.size()]);
    }

    private Solution buildSolution(DigitSet[] stacksets) {
        int[][] grid = new int[dimension][dimension];
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                int flat = r * dimension + c;
                if (stacksets[flat].isJustOne()) {
                    grid[r][c] = stacksets[flat].getRemaining()[0];
                } else {
                    throw new IllegalStateException("Expecting position (" + r + "," + c + ") to have just one value but got " + stacksets[flat]);
                }
            }
        }
        return new Solution(grid);
    }

    private int[] buildFollowing(int row, int col) {
        int[] folls = new int[dimension * 3];
        final int innerbound = dimension / 3;
        int cnt = 0;
        int cb = ((1 + col / innerbound) * innerbound);
        int rb = ((1 + row / innerbound) * innerbound);
        for (int c = col + 1; c < dimension; c++) {
            // rest of row.
            folls[cnt++] = row * dimension + c;
        }
        for (int r = row + 1; r < dimension; r++) {
            folls[cnt++] = r * dimension + col;
            if (r < rb) {
                for (int c = col + 1; c < cb; c++) {
                    folls[cnt++] = r * dimension + c;
                }
            }
        }
        return Arrays.copyOf(folls, cnt);
    }

}

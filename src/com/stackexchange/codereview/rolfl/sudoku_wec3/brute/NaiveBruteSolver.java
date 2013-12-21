package com.stackexchange.codereview.rolfl.sudoku_wec3.brute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.stackexchange.codereview.rolfl.sudoku_wec3.DigitSet;

public class NaiveBruteSolver {

    private final DigitSet[][] digits;
    private final int dimension;

    public NaiveBruteSolver(int dimension, DigitSet[][] digits) {
        this.digits = digits;
        this.dimension = dimension;
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

    public Solution[] solve() {
        List<Solution> solutions = new ArrayList<>();
        final int flatsize = dimension * dimension;
        final int[][] available = new int[flatsize][];
        final int[]   combination = new int[flatsize];
        final int[][] followings  = new int[flatsize][];

        long combs = 1L;
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                int flat = r * dimension + c;
                followings[flat] = buildFollowing(r, c);
                available[flat] = digits[r][c].getRemaining();
                combs *= available[flat].length;
                System.out.printf("Flat %d rem %d combs %d\n", flat, available[flat].length, combs);
            }
        }
        
        System.out.println("Bruting " + combs + " combinations");
        
        boolean done = false;
        long tries = 0L;
        do {
            if (++tries % 1000000 == 0) {
                System.out.printf("Trying combination %d of %d (%.3f%%) with %d solutions so far\n", tries, combs, (100.0 * tries)/combs, solutions.size());
            }
            if (isValid(available, combination, followings)) {
                solutions.add(buildSolution(available, combination));
                System.out.println("Solved one way!");
            }
            // increment combination;
            done = true;
            for (int i = combination.length - 1; i >= 0; i--) {
                if (available[i].length > 1) {
                    if (++combination[i] >= available[i].length) {
                        combination[i] = 0;
                    } else {
                        done = false;
                        break;
                    }
                }
            }
        } while (!done);
        
        return solutions.toArray(new Solution[solutions.size()]);
    }

    private Solution buildSolution(int[][] available, int[] combination) {
        int[][] grid = new int[dimension][dimension];
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                int flat = r * dimension + c;
                grid[r][c] = available[flat][combination[flat]];
            }
        }
        return new Solution(grid);
    }

    private boolean isValid(int[][] available, int[] combination, int[][] related) {
        for (int i = 0; i < available.length; i++) {
            for (int f : related[i]) {
                if (available[i][combination[i]] == available[f][combination[f]]) {
                    return false;
                }
            }
        }
        return true;
    }

}

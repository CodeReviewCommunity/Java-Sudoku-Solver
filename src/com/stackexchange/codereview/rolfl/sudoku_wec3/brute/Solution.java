package com.stackexchange.codereview.rolfl.sudoku_wec3.brute;

import java.util.Arrays;

public class Solution {
    
    private final int[][] solution;

    public Solution(int[][] solution) {
        this.solution = new int[solution.length][];
        for (int i = 0; i < solution.length; i++) {
            this.solution[i] = Arrays.copyOf(solution[i], solution[i].length);
        }
    }

    public int[][] getSolution() {
        return solution;
    }
}

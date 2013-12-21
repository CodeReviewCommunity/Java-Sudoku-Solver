package com.stackexchange.codreview.rolfl.sudoku_wec3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Sudoku {
    
    public static void main(String[] args) throws IOException {
        
        final Grid grid = buildGrid(args.length == 0 ? "input/SudokuExample.txt" : args[0]);
        
        System.out.println(GridToText.displayAsString(grid));
        
        SudokuSolver solver = new SudokuSolver(grid);
        boolean solved = solver.solveAll();
        
        if (solved) {
            System.out.println("SOLVED!!!!!");
        } else {
            System.out.println("UN-SOLVED!!!!!");
        }
        
        System.out.println(GridToText.displayAsString(grid));
        
        

    }

    private static Grid buildGrid(String fname) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fname), StandardCharsets.UTF_8);
        int dimension = lines.size();
        int blocksize = SudokuRules.getBlockSize(dimension);
        if ((blocksize * blocksize) != dimension) {
            throw new IllegalArgumentException("Content width must be a square number (");
        }
        final char[] validchars = Arrays.copyOf(SudokuRules.DIGITCHARS, dimension);
        Grid grid = new Grid(dimension);
        int row = 0;
        for (String line : lines) {
            char[] chars = line.toCharArray();
            if (chars.length != dimension) {
                throw new IllegalStateException("Expect square grid, '" + line + "' is not valid (" + dimension + ")");
            }
            for (int c = 0; c < chars.length; c++) {
                int digit = isValidChar(validchars, chars[c]); 
                if (digit >= 0) {
                    grid.get(row, c).setValue(Source.Puzzle, digit);
                }
            }
            row++;
        }
        return grid;
    }
    
    public static final int isValidChar(char[] validchars, char c) {
        for (int i = 0; i < validchars.length; i++) {
            if (c == validchars[i]) {
                return i;
            }
        }
        return -1;
    }

}

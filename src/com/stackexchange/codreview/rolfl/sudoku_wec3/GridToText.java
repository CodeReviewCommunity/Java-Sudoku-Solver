package com.stackexchange.codreview.rolfl.sudoku_wec3;


public final class GridToText {
    
    private static final char[][] symbols = {
        "╔═╤╦╗".toCharArray(),
        "║ │║║".toCharArray(),
        "╟─┼╫╢".toCharArray(),
        "╠═╪╬╣".toCharArray(),
        "╚═╧╩╝".toCharArray(),
    };
    
    public static final String displayAsString(Grid grid) {
        final int dimension = grid.getDimension();
        final int blocksize = grid.getBlocksize();
        final int[][] rows = new int[dimension][dimension];
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                rows[r][c] = grid.get(r, c).getValue();
            }
        }
        return buildStringForGrid(dimension, blocksize, rows);
    }
    
    public static final String displayAsString(int[][]data) {
        return buildStringForGrid(data.length, SudokuRules.getBlockSize(data.length), data);
    }
    
    private static final String buildStringForGrid(final int dimension, final int blocksize, final int[][]rows) {
        final StringBuilder sb = new StringBuilder();
        for (int r = 0; r < dimension; r++) {
            if (r == 0) {
                sb.append(printSymbolLine(dimension, blocksize, null, symbols[0]));
            } else if (r % blocksize == 0) {
                sb.append(printSymbolLine(dimension, blocksize, null, symbols[3]));
            } else {
                sb.append(printSymbolLine(dimension, blocksize, null, symbols[2]));
            }
            sb.append(printSymbolLine(dimension, blocksize, rows[r], symbols[1]));
        }
        sb.append(printSymbolLine(dimension, blocksize, null, symbols[4]));
        return sb.toString();
    }
    
    private static String printSymbolLine(int dimension, int blocksize, int[] values, char[] symbols) {
        StringBuilder sb = new StringBuilder();
        sb.append(symbols[0]);
        int vc = 0;
        for (int b = 0; b < blocksize; b++) {
            for (int c = 0; c < blocksize; c++) {
                if (values == null) {
                    sb.append(symbols[1]).append(symbols[1]).append(symbols[1]).append(symbols[2]);
                } else {
                    final int val = values[vc++];
                    char ch = SudokuRules.getSudokuDigit(val);
                    sb.append(symbols[1]).append(ch).append(symbols[1]).append(symbols[2]);
                }
            }
            sb.setCharAt(sb.length() - 1, symbols[3]);
        }
        sb.setCharAt(sb.length() - 1, symbols[4]);
        sb.append("\n");
        return sb.toString();
    }
    
}

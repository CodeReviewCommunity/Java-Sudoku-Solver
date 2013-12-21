package com.stackexchange.codereview.rolfl.sudoku_wec3;

public class SudokuRules {
    public static final char[] DIGITCHARS = "123456789ABCFEDGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final int BLANK = -1;


    public static final char getSudokuDigit(final int value) {
        if (value < 0) {
            return ' ';
        }
        return SudokuRules.DIGITCHARS[value];
    }

    public static final char[] getSudokuDigits(final int[] values) {
        final char[] translation = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            translation[i] = getSudokuDigit(values[i]);
        }
        return translation;
    }

    public static int getBlockSize(int dimension) {
        final int blocksize = (int)Math.sqrt(dimension);
        if ((blocksize * blocksize) == dimension) {
            return blocksize;
        }
        throw new IllegalStateException("Illegal dimension size " + dimension);
    }
    
    public static boolean isValid(int[][] data) {
        final int dim = data.length;
        final int bs  = getBlockSize(dim);
        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c< data[r].length; c++) {
                final int rlim = (1 + r / bs) * bs;
                final int clim = (1 + c / bs) * bs;
                for (int rc = r + 1; rc < dim; rc++) {
                    if (data[r][c] == data[rc][c]) {
                        throw new IllegalStateException(format(r,c,rc,c,data));
                    }
                }
                for (int cc = c + 1; cc < dim; cc++) {
                    if (data[r][c] == data[r][cc]) {
                        throw new IllegalStateException(format(r,c,r,cc,data));
                    }
                    if (cc < clim) {
                        for (int rc = r + 1; rc < rlim; rc++) {
                            if (data[r][c] == data[rc][cc]) {
                                throw new IllegalStateException(format(r,c,rc,cc,data));
                            }
                        }
                    }
                }
                
            }
        }
        return true;
    }

    private static final String format(int r, int c, int rc, int cc, int[][]data) {
        return String.format("Data at (%d,%d) is same as data at (%d,%d)\n%s", r, c, rc, cc, GridToText.displayAsString(data));        
    }
}

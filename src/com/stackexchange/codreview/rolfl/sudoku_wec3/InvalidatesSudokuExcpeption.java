package com.stackexchange.codreview.rolfl.sudoku_wec3;

public class InvalidatesSudokuExcpeption extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidatesSudokuExcpeption(String arg0) {
        super(arg0);
    }

    public InvalidatesSudokuExcpeption(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}

package com.stackexchange.codreview.rolfl.sudoku_wec3;

public enum Source {
    Puzzle(true), Unknown(false), Player(false), Strategy(false), Guess(false), Force(false); 
    
    private boolean fixed;

    Source(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean isFixed() {
        return fixed;
    }
}

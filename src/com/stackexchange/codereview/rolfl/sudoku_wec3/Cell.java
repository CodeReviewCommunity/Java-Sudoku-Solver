package com.stackexchange.codereview.rolfl.sudoku_wec3;

import java.util.Arrays;

public class Cell {
    private final Column col;
    private final Row row;
    private final Block block;
    private final int rowpos, colpos, blockpos;
    private Source source = Source.Unknown;
    private int value = SudokuRules.BLANK;
    private final DigitSet digset;
    private final Grid grid;

    public Cell(Grid grid, int dimension, Column col, int colpos, Row row,
            int rowpos, Block block, int blockpos) {
        super();
        this.grid = grid;
        this.digset = new DigitSet(dimension);
        this.col = col;
        this.row = row;
        this.block = block;
        this.colpos = colpos;
        this.rowpos = rowpos;
        this.blockpos = blockpos;
    }

    public Column getCol() {
        return col;
    }

    public Row getRow() {
        return row;
    }

    public Block getBlock() {
        return block;
    }

    public int getRowPos() {
        return rowpos;
    }

    public int getColPos() {
        return colpos;
    }

    public int getBlockPos() {
        return blockpos;
    }

    public void setValue(Source source, int digit) {
        if (isSet()) {
            if (digit != this.value) {
                throw new InvalidatesSudokuExcpeption("Cannot re-set " + this
                        + " to value [" + SudokuRules.getSudokuDigit(digit) + "]");
            }
            return;
        }
        if (!digset.isPossible(digit)) {
            throw new InvalidatesSudokuExcpeption("Cannot set " + this
                    + " to value [" + SudokuRules.getSudokuDigit(digit) + "]. Possible values are "
                    + Arrays.toString(SudokuRules.getSudokuDigits(digset.getRemaining())));
        }
        this.value = digit;
        this.source = source;
        digset.coerce(digit);
        grid.solved(this);
    }

    public int getValue() {
        return value;
    }

    public Source getSource() {
        return source;
    }

    public boolean isSet() {
        return source != Source.Unknown;
    }

    public boolean eliminate(Source src, int digit) {
        if (isSet()) {
            if (digit == value) {
                throw new InvalidatesSudokuExcpeption("Cannot eliminate the value we have already been set to " + this);
            }
            return true;
        }
        if (digset.eliminate(digit)) {
            if (!digset.isJustOne()) {
                throw new IllegalStateException(
                        "Unexpected accounting in the DigSet: " + digset);
            }
            setValue(src, digset.getRemaining()[0]);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Cell (%d,%d)=>[%s]", colpos, rowpos,
                SudokuRules.getSudokuDigit(value));
    }

    @Override
    public int hashCode() {
        return rowpos * digset.getDimension() + colpos;
    }
    
    @Override
    public boolean equals(Object obj) {
        // use identity equals always....
        return obj == this;
    }

    public boolean canBe(int digit) {
        return digset.isPossible(digit);
    }

    public DigitSet getDigitSet() {
        return digset.duplicate();
    }
}

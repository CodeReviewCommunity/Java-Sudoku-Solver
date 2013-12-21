package com.stackexchange.codreview.rolfl.sudoku_wec3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Grid {
    private final int dimension;
    private final int blocksize;
    private final Row[] rows;
    private final Column[] columns;
    private final Block[] blocks;
    private final GroupRelated[] related;
    private final Cell[][] grid;
    private final HashSet<Cell> unsolved = new HashSet<>();
    private final HashSet<Cell> solved = new HashSet<>();
    private final List<SolutionListener> listeners = new LinkedList<>();

    public Grid(final int dim) {
        dimension = dim;
        blocksize = SudokuRules.getBlockSize(dimension); 
        grid = new Cell[dimension][dimension];
        rows = new Row[dimension];
        columns = new Column[dimension];
        blocks = new Block[dimension];
        related = new GroupRelated[dim * 3];

        for (int i = 0; i < dimension; i++) {
            rows[i] = new Row(i, dimension);
            columns[i] = new Column(i, dimension);
            blocks[i] = new Block(i, dimension);
            related[i * 3 + 0] = rows[i];
            related[i * 3 + 1] = columns[i];
            related[i * 3 + 2] = blocks[i];
        }

        
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                final int b = (r / blocksize) * blocksize + (c / blocksize);
                final int bpos = (r % blocksize) * blocksize + (c % blocksize);
                final Cell cell = new Cell(this, dimension, columns[c], r, rows[r], c, blocks[b], bpos);
                rows[r].setCell(c, cell);
                columns[c].setCell(r, cell);
                blocks[b].setCell(bpos, cell);
                grid[r][c] = cell;
                unsolved.add(cell);
                if (c != cell.getRowPos() || r != cell.getColPos()) {
                    throw new IllegalStateException("cell " + cell + " expected at (" + r + "," + c + ")");
                }
            }
        }

    }

    public int getDimension() {
        return dimension;
    }

    public int getBlocksize() {
        return blocksize;
    }
    
    public Cell get(int row, int col) {
        return grid[row][col];
    }
    
    public final Row getRow(int row) {
        return rows[row];
    }

    public final Column getColumn(int col) {
        return columns[col];
    }

    public final Block getBlock(int block) {
        return blocks[block];
    }
    
    public final GroupRelated[] getRelatedGroups() {
        return Arrays.copyOf(related, related.length);
    }

    public void solved(Cell cell) {
        if (!unsolved.remove(cell)) {
            throw new InvalidatesSudokuExcpeption("Expected " + cell + " to be in the unsolved set, but it was not.");
        }
        if (!solved.add(cell)) {
            throw new InvalidatesSudokuExcpeption("Expected " + cell + " not to be in the solved set, but it was.");
        }
        for (SolutionListener listener : listeners) {
            listener.solved(cell);
        }
    }

    public void addSolutionListener(SolutionListener listener) {
        listeners.add(listener);
        // play catch-up for the listener
        for (Cell c : solved) {
            listener.solved(c);
        }
    }

    public boolean isSolved() {
        return unsolved.isEmpty();
    }

    @Override
    public String toString() {
        return GridToText.displayAsString(this);
    }


}

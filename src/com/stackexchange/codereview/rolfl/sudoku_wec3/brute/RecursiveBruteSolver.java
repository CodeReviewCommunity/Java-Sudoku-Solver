    package com.stackexchange.codereview.rolfl.sudoku_wec3.brute;
    
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    
//    import org.tuis.sudoku.DigitSet;
    
    /**
     * A class that is able to brute-force a solution for a Sudoku-like puzzle.
     * <br>
     * Due to the internal use of bit-shifting the largest sudoku puzzle this can solve is 25x25
     * which should be more than enough. Sudoku-like puzzles are always square-number sized. 1x1, 4x4, 9x9, 16x16, 25x25, etc.
     * 
     * @author rolfl
     *
     */
    public class RecursiveBruteSolver {
    
        // dimension - the width of the grid
        private final int dimension;
        // a simple bit-mask with 1 bit set for each possible value in each square.
        // for a 4-size sudoku it will look like:
        // 0b0001 0b0010 0b0100 0b1000
        // for a 9-size sudoku like:
        // 0b000000001 0b000000010 0b000000100 .....
        private final int[] bitmask;
        // if we flatten the grid, this is how big it is in 1 dimension
        private final int flatsize;
        // the array of flattened cell available digits
        private final int[][] available;
        // a complicated concept - the index into the flattened array of all
        // squares that are in the same row/column/block, but also have a larger
        // index in the flattened array.
        private final int[][] followings;
        // Some squares will have just one possible value, they do not need to be
        // solved.
        // this unknown array is the indices of the unsolved squares in the
        // flattened array
        private final int[] unknown;
        
        // keep track of all recursive call counts
        private long statistics = 0L;
    
        /**
         * Create a Brute-force solver that will determine all the solutions (if
         * any) for a Sudoku grid (when you call the solve() method).
         * 
         * @param dimension
         *            the side-size of the sudoku grid. The digits 2D array is
         *            expected to match the same dimensions.
         * @param digits
         *            a 3D array containing the digits that are allowed at that
         *            position in the array. The actual digits available are
         *            expected to be 0-based. i.e. a regular 9-size Sudoku has the
         *            digits 1-9, this solver expects them to be represented as 0-8.
         */
        public RecursiveBruteSolver(int dimension, int[][][] digits) {
            this.dimension = dimension;
            
            // if we flatten the array....
            this.flatsize = dimension * dimension;
            available = new int[flatsize][];
            followings = new int[flatsize][];
            
            // how many digits are there...and what will the bit-mask be for each digit.
            bitmask = new int[dimension];
            for (int i = 0; i < dimension; i++) {
                bitmask[i] = 1 << i;
            }
    
            // keep track of the unknown cells.
            // make a generous-size array, we will trim it later.
            int[] tmpunknown = new int[flatsize];
            int tmpunknowncnt = 0;
    
            // flatten out the grid.
            for (int r = 0; r < dimension; r++) {
                for (int c = 0; c < dimension; c++) {
                    int flatid = r * dimension + c;
                    // gets an array of digits that can be put in each square.
                    // for example, if a Sudoku square can have 3,5 or 6 this will return
                    //    [2,4,5]
                    available[flatid] = Arrays.copyOf(digits[r][c], digits[r][c].length);
                    if (available[flatid].length > 1) {
                        tmpunknown[tmpunknowncnt++] = flatid;
                    }
                }
            }
    
    //        System.out.println("There are " + tmpunknowncnt + " unknown cells");
    
            // Special note about `followings`
            // A square in a Sudoku puzzle affects all other squares in the same row, column, and block.
            // Only one square in the same row/block/column can have particular value.
            // For this recursion we 'flatten' the grid, and process the grid in order.
            // For each unsolved cell, we set the cell value, and then move on to the next unsolved cell
            // but, we need to 'remove' our value from the possible values of other cells in the same row/column/block.
            // Because of the way this solver progresses along the flattened array, we only need to remove it
            // from cells that come **after** us in the flattened array (values before us already have a fixed value).
            // So, buildFollowing() builds an array of indexes in the flattened array that are in the same row/column/block
            // but also have a higher index in the flattened array.
            for (int flat = 0; flat < available.length; flat++) {
                if (available[flat] != null) {
                    followings[flat] = buildFollowing(flat / dimension, flat
                            % dimension);
                }
            }
    
            // create the final copy of the unknown cells (cells which need to be solved).
            unknown = Arrays.copyOf(tmpunknown, tmpunknowncnt);
        }
    
        public int[][][] solve() {
    
            // following points to unknown subsequent values.....
            final int[] combination = new int[flatsize];
            // where to store the possible solutions we find
            final List<int[][]> solutions = new ArrayList<>();
            
            // this freedoms is an integer for each value.
            // bits in the integer are set for each of the values the
            // position can be. This is essentially a record of the state
            // inside the recursive routine. For example, if setting some other
            // cell in the same row/column/block to 5, and our 5-bit is set,
            // then we will unset it here because we no longer have the freedom
            // to be 5.
            final int[] freedoms = new int[flatsize]; //Arrays.copyOf(resetmask, resetmask.length);
            for (int flatid = 0; flatid < flatsize; flatid++) {
                for (int b : available[flatid]) {
                    // set the degrees-of-freedom mask of this...
                    // what values can this cell take?
                    freedoms[flatid] |= bitmask[b];
                }
            }
    
            // Do the actual recursion.
            // combination contains pointers to which actual values are being used.
            // freedom contans the possible states for all subsequent cells.
            recurse(solutions, 0, combination, freedoms);
            
            System.out.println("There were " + statistics + " recursive calls...");
    
            // convert the list of Solutions back to an array
            return solutions.toArray(new int[solutions.size()][][]);
        }
    
        /**
         * Recursively solve the Sudoku puzzle.
         * @param solutions where to store any found solutions.
         * @param index The index in the 'unknown' array that points to the flat-based cell we need to solve.
         * @param combination What the current combination of cell values is.
         * @param freedoms The state of what potential values all cells can have.
         */
        private void recurse(final List<int[][]> solutions, final int index,
                final int[] combination, final int[] freedoms) {
            statistics++;
            if (index >= unknown.length) {
                // solution!
                solutions.add(buildSolution(combination));
                return;
            }
            
            // The basic algorithm here is: for our unsolved square, we set it to each of it's possible values in turn.
            // then, for each of the values we can be:
            // 1. we also find all 'related' squares, and remove our value
            //      from the degrees-of-freedom for the related squares
            //      (If I am 'x' then they cannot be). See special not about 'followings'
            // 3. we keep track of which other squares we actually change the freedoms for.
            // 4. we recurse to the next unsolved square.
            // 5. when the recursion returns, we restore the freedoms we previously 'revoked'
            // 6. we move on to the next value we can be (back to 1).
            // 7. when we have run out of possible values, we return.
            final int flat = unknown[index];
            for (int a = available[flat].length - 1; a >= 0; a--) {
                final int attempt = available[flat][a];
                if ((freedoms[flat] & bitmask[attempt]) == 0) {
                    // this option excluded by previous restrictions....
                    // the original unsolved puzzle says we can be 'attempt', but
                    // higher levels of recursion have removed 'attempt' from our
                    // degrees-of-freedom.
                    continue;
                }
                // ok, is used to track whether we are still creating a valid Sudoku.
                boolean ok = true;
                // progress is used to forward, and then backtrack which following cells
                // have been impacted.
                // start at -1 because we pre-increment the progress.
                int progress = -1;
                // act has 1 bit representing each follower we act on.
                long act = 0;
                while (++progress < followings[flat].length) {
                    if (freedoms[followings[flat][progress]] == bitmask[attempt]) {
                        // we intend to remove the attempt from this follower's freedom's
                        // but that will leave it with nothing, so this is not possible to do.
                        // ok is false, so we will start a back-up
                        ok = false;
                        break;
                    }
                    // we **can** remove the value from this follower's freedoms.
                    // indicate that this follower is being 'touched'.
                    // act will have 1 bit available for each follower we touch.
                    act <<= 1;
                    // record the pre-state of the follower's freedoms.
                    final int pre = freedoms[followings[flat][progress]];
                    // if the follower's freedoms contained the value we are revoking, then set the bit.
                    act |= (freedoms[followings[flat][progress]] &= ~bitmask[attempt]) == pre ? 0 : 1;
                }
                if (ok) {
                    // we have removed our digit from all followers, and the puzzle is still valid.
                    // indicate our combination digit....
                    combination[flat] = a;
                    // find the next unsolved.
                    recurse(solutions, index + 1, combination, freedoms);
                }
                while (--progress >= 0) {
                    // restore all previously revoked freedoms.
                    if ((act & 0x1) == 1) {
                        freedoms[followings[flat][progress]] |= bitmask[attempt]; // & resetmask[flat]);
                    }
                    act >>= 1;
                }
            }
    
        }
    
        /**
         * buildFollowing creates an array of references to other cells in the same
         * row/column/block that also have an index **after** us in the flattened array system. 
         *
         * @param row our row index
         * @param col our column index
         * @return an array of flattened indices that are in the same row/column/block as us.
         */
        private int[] buildFollowing(int row, int col) {
            int[] folls = new int[dimension * 3]; // possible rows/columns/blocks - 3 sets of values.
            final int innerbound = (int)Math.sqrt(dimension); // 3 for size 9, 2 for size 4, 4 for size 16, etc.
            // cnt is used to count the valid following indices.
            int cnt = 0;
            // column-bound - last column in the same block as us.
            int cb = ((1 + col / innerbound) * innerbound);
            // row-bound - last row in the same block as us.
            int rb = ((1 + row / innerbound) * innerbound);
            // get all (unsolved) indices that follow us in the same row
            for (int c = col + 1; c < dimension; c++) {
                // rest of row.
                if (available[row * dimension + c].length > 1) {
                    // only need to worry about unsolved followers.
                    folls[cnt++] = row * dimension + c;
                }
            }
            // get all (unsolved) indices that follow us in the same column
            for (int r = row + 1; r < dimension; r++) {
                if (available[r * dimension + col].length > 1) {
                    // only need to worry about unsolved followers.
                    folls[cnt++] = r * dimension + col;
                }
                if (r < rb) {
                    // if we have not 'escaped' our block, we also find other cells in
                    // the same block, but not our row/column.
                    for (int c = col + 1; c < cb; c++) {
                        if (available[r * dimension + c].length > 1) {
                            // only need to worry about unsolved followers.
                            folls[cnt++] = r * dimension + c;
                        }
                    }
                }
            }
            // return just the values that were needed as followers.
            return Arrays.copyOf(folls, cnt);
        }
    
        /**
         * Convert the valid combination of values back to a simple int[] grid.
         * @param combination the combination of unsolved values that is a valid puzzle.
         * @return A Solution object representing the solution.
         */
        private int[][] buildSolution(int[] combination) {
            int[][] grid = new int[dimension][dimension];
            for (int f = 0; f < combination.length; f++) {
                grid[f / dimension][f % dimension] = available[f][combination[f]];
            }
            // double-check the validity of this solution (all sudoku basic rules are followed.
            // throws exception if not.
    //        SudokuRules.isValid(grid);
            // mechanism for printing out a grid.
    //        System.out.println("BruteForceRecursive found Solution:\n"
    //                + GridToText.displayAsString(grid));
            return grid;
        }
    
    }

    package com.stackexchange.codereview.rolfl.sudoku_wec3.brute;
    
    public class BruteMain {
        
        /**
         * http://codereview.stackexchange.com/questions?sort=newest
         * See above link
         * @param args
         */
        public static void main(String[] args) {
            int[][][] puzzle = new int[][][] {
                    { { 1, 4, 5, },{ 2, 5, 6, },{ 1, 2, 4, 5, 6, },{ 7, },{ 3, },{ 2, 4, 5, 6, },{ 0, 1, 6, },{ 0, 1, 6, },{ 8, },  },
                    { { 1, 3, 5, },{ 2, 3, 5, 6, 8, },{ 0, },{ 2, 5, 6, },{ 2, 5, 6, 8, },{ 2, 5, 6, 8, },{ 1, 6, 7, },{ 1, 6, 7, },{ 4, },  },
                    { { 7, },{ 6, 8, },{ 4, 6, 8, },{ 4, 6, },{ 1, },{ 0, },{ 3, },{ 5, },{ 2, },  },
                    { { 6, },{ 0, 2, 3, 5, },{ 7, },{ 0, 1, 2, 3, 4, 5, },{ 2, 4, 5, },{ 1, 2, 4, 5, },{ 1, 5, },{ 8, },{ 1, 3, 5, },  },
                    { { 0, 1, 3, 5, },{ 0, 2, 3, 5, 8, },{ 1, 2, 5, 8, },{ 0, 1, 2, 3, 4, 5, 6, },{ 2, 4, 5, 6, 8, },{ 1, 2, 4, 5, 6, 7, 8, },{ 1, 5, 6, 7, },{ 1, 3, 4, 6, 7, },{ 1, 3, 5, 7, },  },
                    { { 1, 3, 5, },{ 4, },{ 1, 5, 8, },{ 1, 3, 5, 6, },{ 5, 6, 8, },{ 1, 5, 6, 7, 8, },{ 2, },{ 1, 3, 6, 7, },{ 0, },  },
                    { { 4, 5, },{ 1, },{ 3, },{ 8, },{ 0, },{ 2, 4, 5, },{ 5, 7, },{ 2, 7, },{ 6, },  },
                    { { 8, },{ 0, 5, 6, 7, },{ 5, 6, },{ 1, 2, 5, 6, },{ 2, 5, 6, },{ 1, 2, 5, 6, },{ 4, },{ 0, 1, 2, 3, 7, },{ 1, 3, 5, 7, },  },
                    { { 2, },{ 0, 5, 6, },{ 4, 5, 6, },{ 1, 4, 5, 6, },{ 7, },{ 3, },{ 0, 1, 5, 8, },{ 0, 1, },{ 1, 5, },  },
            };
    
            RecursiveBruteSolver solver = new RecursiveBruteSolver(9, puzzle);
            for (int[][] sol : solver.solve()) {
                System.out.println("Solution:");
                System.out.println(displayAsString(sol));
            }
        }
    
        private static final char[][] symbols = {
            "╔═╤╦╗".toCharArray(),
            "║ │║║".toCharArray(),
            "╟─┼╫╢".toCharArray(),
            "╠═╪╬╣".toCharArray(),
            "╚═╧╩╝".toCharArray(),
        };
        
        private static final char[] DIGITCHARS = "123456789ABCFEDGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        
        public static final char getSudokuDigit(final int value) {
            if (value < 0) {
                return ' ';
            }
            return DIGITCHARS[value];
        }
        
        public static final String displayAsString(int[][]data) {
            return buildStringForGrid(data.length, (int)Math.sqrt(data.length), data);
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
                        char ch = getSudokuDigit(val);
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

package com.stackexchange.codreview.rolfl.sudoku_wec3;

import java.util.Arrays;

public class DigitSet {
    
    private final int[] digits;
    private int size;
    
    public DigitSet(final int dimension) {
        this.digits = new int[dimension];
        this.size = dimension;
        for (int i = 0; i < dimension; i++) {
            digits[i] = i;
        }
    }
    
    private int position(final int digit) {
        return Arrays.binarySearch(digits, 0, size, digit);
    }
    
    public boolean isPossible(int digit) {
        return position(digit) >= 0;
    }
    
    public boolean eliminate(final int digit) {
        final int pos = position(digit);
        if (pos >= 0) {
            System.arraycopy(digits, pos + 1, digits, pos, size - pos - 1);
            size--;
        }
        return size <= 1;
    }
    
    public void restore(final int digit) {
        final int pos = position(digit);
        if (pos < 0) {
            int p = -pos -1;
            System.arraycopy(digits, p, digits, p + 1, size - p);
            size++;
            digits[p] = digit;
        }
    }
    
    public void coerce(final int digit) {
        size = 1;
        digits[0] = digit;
    }
    
    public int[] getRemaining() {
        return Arrays.copyOf(digits, size);
    }
    
    public int getRemainingSize() {
        return size;
    }
    
    public boolean isJustOne() {
        return size == 1;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int getDimension() {
        return digits.length;
    }

    @Override
    public String toString() {
        return "DigitSet: " + size + " remaining " + Arrays.toString(getRemaining());
    }
    
    public DigitSet duplicate() {
        DigitSet ret = new DigitSet(digits.length);
        ret.size = size;
        System.arraycopy(digits, 0, ret.digits, 0, digits.length);
        return ret;
    }
}

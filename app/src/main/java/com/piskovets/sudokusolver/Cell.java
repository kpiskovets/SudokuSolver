package com.piskovets.sudokusolver;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class Cell implements Comparable<Cell> {
	public static final int DEF = 0;
	int val;
	boolean preFilled = false;
	Set<Integer> constraints = new HashSet<>();
	int row, col;

	public Cell(int val) {
		this.val = val;
	}

	public boolean isValid() {

		return isValid;
	}

	boolean isValid;

	int row() {
		return row;
	}
	
	int col() {
		return col;
	}
	Set<Integer> constraints() {
		return constraints;
	}
	
	boolean solved() {
		return val != DEF;
	}
	
	int constraintSize() {
		return constraints.size();
	}

	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}

	private Cell(int val, int row, int col, boolean preFilled) {
		this.val = val;
		this.preFilled = preFilled;
		this.row = row;
		this.col = col;
	}


	public static Cell getCell(int c, int row, int col) {
		if(c==0)
			return new Cell(DEF, row, col, false);
		else
			return new Cell(c, row, col, true);
	}

	int val() {
		return val;
	}

	void setVal(int m_val) {
		this.val = m_val;
	}

	@Override public String toString() {
		return "" + val + "[" + row + "," + col + "]";
	}

	@Override
	public int compareTo(@NonNull Cell cell2) {
		if(this.constraintSize() == cell2.constraintSize()) 
			return 1;
		return (this.constraintSize() - cell2.constraintSize()) * 10000;
	}
	
	@Override public boolean equals(Object cellObj) {
		if(!(cellObj instanceof Cell)) return false;
		Cell cell = (Cell)cellObj;
		return cell.row() == row && cell.col() == col;
	}
}

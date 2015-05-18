package com.piskovets.sudokusolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public  class SudokuGrid {
	int size;
	List<Cell> populateList =new ArrayList<>();
	Cell[][] grid;
	protected Set<Cell> constrainedCells = new HashSet<>(200);

	public SudokuGrid(Cell[][] grid) {
		size = grid.length;
		this.grid = grid;
		for(Cell[] row: grid)
			for(Cell cell: row) {
				if(!cell.solved())
					this.constrainedCells.add(cell);
				else 
						this.setVal(cell, cell.val());
			}

	}

	public  static SudokuGrid getGrid(List<List<Cell>> gridInput) {

		int n = gridInput.size();
		Cell[][] grid = new Cell[n][n];
		for(int i = 0; i < gridInput.size(); i++) {
			for(int j = 0; j < gridInput.get(0).size(); j++) {
				grid[i][j] = Cell.getCell(gridInput.get(i).get(j).val(), i, j);
			}
		}
		return new SudokuGrid(grid);
	}
	public boolean solve() {
		Cell cell = this.getNextUnoccupiedCell();
		if(cell == null) return true;

		for(int val = 1; val <= size; val++) {
			if(cell.constraints().contains(val)) continue;
			if(this.valid(cell.row(), cell.col(), val)) {
				int prevVal = cell.val();
				this.constrainedCells.remove(cell);
				List<Cell> modifiedCells = setVal(cell, val);
				this.populateList.add(cell);
				if(this.solve())
					return true;
				cell.setVal(prevVal);
				this.constrainedCells.add(cell);
				this.populateList.remove(cell);
				resetVal(modifiedCells, val);
			}
		}
		return false;
	}

	protected void resetVal(List<Cell> modifiedCells, int k) {
		for(Cell cell: modifiedCells) {
			cell.constraints().remove(k);
		}
	}
	protected boolean valid(int row, int col, int k) {
		
		for(int ind = 0; ind < size; ind++) {
			if((row != ind && grid[ind][col].val() == k) ||
					(col != ind && grid[row][ind].val() == k))
				return false;
		}
		int m = (row/3) * 3;
		int n = (col/3) * 3;
		for(int i = m; i < m + 3; i++)
			for(int j = n; j < n + 3; j++) {
				if(i == row && j == col) continue;
				if(this.grid[i][j].val() == k)
				return false;
			}
		return true;
	}
	protected String getValue(int row,int col){
		return String.valueOf(grid[row][col].val());
	}
	
	protected List<Cell> addConstraints(Cell cell, int k) {
		
		List<Cell> updatedCells = new ArrayList<>(size);
		for(int ind = 0; ind < size; ind++) {
			Cell colCell = grid[ind][cell.col()];
			if(!colCell.solved() && cell.row() != ind && !colCell.constraints().contains(k)) {
				colCell.constraints().add(k);
				if(!updatedCells.contains(colCell))
						updatedCells.add(colCell);
			}
			Cell rowCell = grid[cell.row()][ind];
			if(!rowCell.solved() && cell.col() != ind && !rowCell.constraints().contains(k)) {
				rowCell.constraints().add(k);
				if(!updatedCells.contains(rowCell))
				updatedCells.add(rowCell);
			}
		}
		int m = (cell.row()/3) * 3;
		int n = (cell.col()/3) * 3;
		for(int i = m; i < m + 3; i++)
			for(int j = n; j < n + 3; j++) {
				if(grid[i][j].solved() || (i == cell.row() && j == cell.col())) continue;
				Cell groupCell = grid[i][j];
				if(!groupCell.constraints().contains(k)) {
					groupCell.constraints().add(k);
					if(!updatedCells.contains(groupCell))
					updatedCells.add(groupCell);
				}
			}
		return updatedCells;
	}

	protected List<Cell> setVal(Cell cell, int val) {
		cell.setVal(val);

		return addConstraints(cell, val);
	}

	
	protected Cell getNextUnoccupiedCell() {
		
		if(this.constrainedCells.size() == 0) return null;

		return Collections.max(this.constrainedCells);
	}

	@Override public String toString() {
		StringBuilder buff = new StringBuilder();
		for(Cell[] row: grid) {
			for(Cell cell: row)
				buff.append(cell.val()).append(",");
			buff.append("\n");
		}
		return buff.toString();
		
	}


}

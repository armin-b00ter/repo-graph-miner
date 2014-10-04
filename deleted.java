package sameGame.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Game {
	// properties of class Game
	private Board board;
	private long playerScore = 0;
	private int numRow;
	private int numCol;
	private int remainCell;

	/**
	 * Game Constructor
	 * 
	 * @param numRow
	 *            : number of board's row
	 * @param numCol
	 *            : number of board's column
	 * @param numColor
	 *            : number of colors
	 */
	public Game(int numRow, int numCol, int numColor) {
		board = new Board(numRow, numCol, numColor);
		this.numRow = numRow;
		this.numCol = numCol;
		this.setRemainCell(numRow * numCol);
	}

	public void setRemainCell(int remainCell) {
		this.remainCell = remainCell;
	}

	/**
	 * it get a cell and change the color of the cells one bye one
	 * 
	 * @param x
	 *            : horizontal position of a cell
	 * @param y
	 *            : vertical position of a cell
	 */
	private void changeUpCell(int x, int y) {
		int a = board.getEmptyCellsNum(y);
		for (int i = x; i >= a; i--) {
			if (i == 0) {
				board.getBoard()[i][y].setColor("isEmpty");
			} else {
				String color = board.getBoard()[i - 1][y].getColor();
				board.getBoard()[i][y].setColor(color);
			}
		}
	}

	/**
	 * 
	 * @return true if a cell exist that has some same neighbor and false
	 *         otherwise
	 */
	private boolean checkTable() {
		int flag = 0;
		outer: for (int i = 0; i < numRow; i++)
			for (int j = 0; j < numCol; j++) {
				if (board.hasSameNeighbor(i, j)) {
					flag = 1;
					break outer;
				}
			}
		if (flag == 1)
			return true;
		else
			return false;
	}

	/**
	 * it invokes the method getSameCell of class board it changes the colors of
	 * cell that there are above of a cell
	 * 
	 * @param i
	 *            : horizontal position of a cell
	 * @param j
	 *            : vertical position of a cell
	 */
	public void play(int i, int j) {
		int[][] arr = new int[numRow][numCol];
		ArrayList<Cell> neighbors = board.getSameCell(board.getBoard()[i][j],
				arr);
		// sorting the neighbors according to their horizontal position
		Collections.sort(neighbors, new Comparator<Cell>() {
			// comparator according to horizontal position
			public int compare(Cell o1, Cell o2) {
				if (o1.getPosition()[0] < o2.getPosition()[0])
					return -1;
				else if (o1.getPosition()[0] > o2.getPosition()[0])
					return 1;
				else
					return 0;
			}
		});

		for (int k = 0; k < neighbors.size(); k++) {
			if (neighbors.size() > 1
					|| (neighbors.size() == 1 && !checkTable())) {
				if (k == 0) {
					playerScore += neighbors.size() * neighbors.size();
					remainCell -= neighbors.size();
				}
				int x = neighbors.get(k).getPosition()[0];
				int y = neighbors.get(k).getPosition()[1];
				changeUpCell(x, y);
			}
		}

		Collections.sort(neighbors, new Comparator<Cell>() {
			public int compare(Cell o1, Cell o2) {
				if (o1.getPosition()[1] < o2.getPosition()[1])
					return -1;
				else if (o1.getPosition()[1] > o2.getPosition()[1])
					return 1;
				else
					return 0;
			}

		});

		for (int k = neighbors.size() - 1; k >= 0; k--) {
			int col = neighbors.get(k).getPosition()[1];
			System.out.println("col is" + col);
			System.out.println("empty cells" + board.getEmptyCellsNum(col));
			if (board.getEmptyCellsNum(col) == numRow) {
				System.out.println("dfgdfgdfgdgsdg");
				for (int t = col; t < numCol; t++) {
					for (int r = 0; r < numRow; r++) {
						if (t == numCol - 1) {
							board.getBoard()[r][t].setColor("isEmpty");
						} else {
							String color = board.getBoard()[r][t + 1]
									.getColor();
							board.getBoard()[r][t].setColor(color);
						}
					}
				}
			}
		}
	}

	/**
	 * getter for PlayerScore
	 */
	public long getPlayerScore() {
		return playerScore;
	}

	/**
	 * 
	 * @return board
	 */
	public Board getBoard() {
		return board;
	}

	public int getNumRows() {
		return numRow;
	}

	public int getNUmCols() {
		return numCol;
	}

	public int getRemainCell() {
		return remainCell;
	}

}

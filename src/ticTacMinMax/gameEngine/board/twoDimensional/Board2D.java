package ticTacMinMax.gameEngine.board.twoDimensional;

import java.util.Arrays;

import ticTacMinMax.gameEngine.board.GameBoard;
import ticTacMinMax.stream.StreamManager;
import ticTacMinMax.userInterface.SwingManager;
import ticTacMinMax.userInterface.contentPanes.TicTacToePane;

public class Board2D extends GameBoard {
	// Board constants
	// This must be a positive integer. It is usually three.
	public static final int BOARD_DIMENSION = Integer.parseInt(StreamManager
			.getInstance().getRawConfig("Board_Size"));
	public static final int BOARD_SIZE = BOARD_DIMENSION * BOARD_DIMENSION;
	/* TODO Get rid of tokens, board is graphical and don't need none of this
	 * token business. */
	public static final char PLAYER_1_TOKEN = 'X';
	public static final char PLAYER_2_TOKEN = 'O';
	public static final char BLANK_SPACE = '_';

	private static TicTacToePane boardGUI = SwingManager.getInstance()
			.getFrame().getContentPain();

	public static String TIE_GAME_TEXT = "YOU BOTH LOSE!!!";

	// The variable to store the board in.
	// TODO Use byte array to save space and faster calculations.
	public char[][] board;

	/** A TicTacToe board.
	 * 
	 * @throws InvalidBoardToken
	 *             Throws an exception if the board tokens initialize
	 *             incorrectly. */
	public Board2D() {
		/* create board. */
		board = new char[BOARD_DIMENSION][BOARD_DIMENSION];
		for (int i = 0; i < BOARD_DIMENSION; i++)
			for (int j = 0; j < BOARD_DIMENSION; j++)
				board[i][j] = BLANK_SPACE;
	}

	/** place a piece at the given location on the board.
	 * 
	 * @param playerNumber
	 *            Either player one or player two. This determines the symbol to
	 *            place in the board array.
	 * @throws InvalidMoveExeption
	 *             If the location is already taken. */
	public void placePiece(BoardLocation2D loc, int playerNumber) {
		int row = loc.row();
		int column = loc.column();
		if (board[column][row] == BLANK_SPACE) {
			if (playerNumber == 1)
				board[column][row] = PLAYER_1_TOKEN;
			else
				board[column][row] = PLAYER_2_TOKEN;
		} else
			throw new UnsupportedOperationException("Location not empty."
					+ board[column][row] + " placed at " + column + ", " + row);

		boardGUI.repaint();
	}

	/** Determine if a piece is placed at a given location.
	 * 
	 * @param column
	 *            The column number to place the piece at. The columns start at
	 *            one.
	 * @param row
	 *            The row number to place the piece at. The rows start at one.
	 * @return True if there is already a piece placed at the given location. */
	public boolean isPiecePlacedAt(int column, int row) {
		return board[column][row] != BLANK_SPACE;
	}

	/** Determine if a specific player's piece is placed at the given location.
	 * 
	 * @param column
	 *            The column number to place the piece at. The columns start at
	 *            one.
	 * @param row
	 *            The row number to place the piece at. The rows start at one.
	 * @param piece
	 *            The players piece to check for.
	 * @return True if the given piece is at the given location. */
	public boolean getPieceAt(int column, int row, char piece) {
		return board[column][row] == piece;
	}

	/** @return True if there is a winner. This can easily be used to determine
	 *         the winner by running this check immediately when a player places
	 *         piece because only that play can be the winner. */
	public boolean isGameWon() {
		// Use smaller methods to check each of the winning conditions.
		return (checkColumns() || checkRows() || checkDiagonals());
	}

	/** Check the each column, called by the isGameWon() method.
	 * 
	 * @return True if any of the columns have all of the same player tokens. */
	private boolean checkColumns() {
		// Win defaults to false, row stores the number of consecutive tokens, i
		// is the column number in the board array and j is the column in the
		// board array. playerToken holds the token that is being checked with
		// the board character array for a winning condition.
		boolean win = false;
		int row, i, j;
		char playerToken;

		for (j = 0; j < BOARD_DIMENSION && !win; j++)
			if (board[0][j] != BLANK_SPACE) {
				playerToken = board[0][j];
				row = 0;
				for (i = 0; i < BOARD_DIMENSION && board[i][j] == playerToken; i++)
					row++;
				win = (row == BOARD_DIMENSION);
			}

		return win;
	}

	/** Check the each row, called by the isGameWon() method.
	 * 
	 * @return True if any of the rows have all of the same player tokens. */
	private boolean checkRows() {
		// Win defaults to false, row stores the number of consecutive tokens, i
		// is the column number in the board array and j is the column in the
		// board array. playerToken holds the token that is being checked with
		// the board character array for a winning condition.
		boolean win = false;
		int row, i, j;
		char playerToken;

		for (i = 0; i < BOARD_DIMENSION && !win; i++)
			if (board[i][0] != BLANK_SPACE) {
				playerToken = board[i][0];
				row = 0;
				for (j = 0; j < BOARD_DIMENSION && board[i][j] == playerToken; j++)
					row++;
				win = (row == BOARD_DIMENSION);
			}

		return win;
	}

	/** Check the each diagonal, called by the isGameWon() method.
	 * 
	 * @return True if either of the diagonals have all the same player tokens. */
	private boolean checkDiagonals() {
		// Win defaults to false, row stores the number of consecutive tokens, i
		// is the column number in the board array and j is the column in the
		// board array. playerToken holds the token that is being checked with
		// the board character array for a winning condition.
		boolean win = false;
		int row, i, j;
		char playerToken;

		// Top left to bottom right.
		if (board[0][0] != BLANK_SPACE) {
			playerToken = board[0][0];
			// First location already checked.
			row = 1;
			for (i = 1; i < BOARD_DIMENSION; i++)
				// If j were used, i and j would always be the same.
				if (board[i][i] == playerToken)
					row++;
			win = (row == BOARD_DIMENSION);
		}

		// Bottom left to top right.
		if (!win && board[0][BOARD_DIMENSION - 1] != BLANK_SPACE) {
			playerToken = board[0][BOARD_DIMENSION - 1];
			// First location already checked.
			row = 1;
			for (i = 1, j = BOARD_DIMENSION - 2; i < BOARD_DIMENSION && j >= 0
					&& playerToken == board[i][j]; i++, j--)
				row++;
			win = (row == BOARD_DIMENSION);
		}

		return win;
	}

	/** @return True if every space on the board has a non-empty value. */
	public boolean isFull() {
		// Default to true.
		boolean full = true;

		// Loop through the board array until a blank space is found, if one is
		// never found, the board is full.
		for (int i = 0; i < BOARD_DIMENSION && full; i++)
			for (int j = 0; j < BOARD_DIMENSION && full; j++)
				full = !(board[i][j] == BLANK_SPACE);

		return full;
	}

	/** @return A copy of the board. */
	public char[][] getCopyOfBoard() {
		char[][] clone = new char[BOARD_DIMENSION][BOARD_DIMENSION];

		for (int i = 0; i < BOARD_DIMENSION; i++) {
			clone[i] = Arrays.copyOf(board[i], board[i].length);
		}

		return clone;
	}
}
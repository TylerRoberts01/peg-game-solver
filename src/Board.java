import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
	public static final int NUM_ROWS = 5;
	public static final int NUM_COLS = 9;
	private static final int NUM_PEGS = 15;
	private static final int[][] DIRECTIONS = new int[][] { { 0, 0, -2, -2, 2, 2 }, { -4, 4, -2, 2, -2, 2 } };
	private static final int[][] PEG_COORDS = new int[2][NUM_PEGS];

	// 0 indicates this space is not a hole
	// -1 indicates this space is a hole and is empty
	// 1 indicates this space is a hole and is filled with a peg
	private int[][] pegs;

	// initializes a new Board that is blank
	public Board() {
		pegs = new int[NUM_ROWS][NUM_COLS];

		// initialize PEG_COORDS
		int i = 0;
		for (int r = 0; r < NUM_ROWS; r++) {
			for (int c = NUM_ROWS - 1 - r; c <= NUM_COLS - (NUM_ROWS - 1 - r); c += 2) {
				PEG_COORDS[0][i] = r;
				PEG_COORDS[1][i] = c;
				i++;
			}
		}
	}

	// pre: emptyRow and emptyCol correspond to holes
	// post: this Board is reset with the specified empty hole
	public void setup(int emptyRow, int emptyCol) {
		boolean foundHole = false;
		for (int r = 0; r < NUM_ROWS; r++) {
			// create a triangle shape and skip a peg each time
			for (int c = NUM_ROWS - 1 - r; c <= NUM_COLS - (NUM_ROWS - 1 - r); c += 2) {
				// check if we found the specified empty hole
				if (r == emptyRow && c == emptyCol) {
					pegs[r][c] = -1;
					foundHole = true;
				} else {
					pegs[r][c] = 1;
				}
			}
		}

		// check if we ever found the empty hole
		if (!foundHole) {
			throw new IllegalArgumentException("The specified coordinate is not a hole");
		}
	}

	// pre: setup has been called
	// post: the Board is solved and all solutions are returned in the form of
	// BoardSolution objects
	public List<BoardSolution> solve() {
		// list of solutions for the current starting empty peg
		List<BoardSolution> solutions = new ArrayList<BoardSolution>();
		solutions.add(new BoardSolution());
		solutions.get(solutions.size() - 1).addStep(copyPegs());

		// recursively get all solutions
		solveHelper(solutions);

		// the last solution is empty or not a solution
		solutions.remove(solutions.size() - 1);

		return solutions;
	}

	// private helper method
	private void solveHelper(List<BoardSolution> solutions) {
		// base case: board is solved
		if (solved()) {
			solutions.add(new BoardSolution(solutions.get(solutions.size() - 1)));
		}
		// get coordinates of this peg coord
		for (int i = 0; i < NUM_PEGS; i++) {
			int r = PEG_COORDS[0][i];
			int c = PEG_COORDS[1][i];

			// check if peg is occupied
			if (pegs[r][c] == 1) {
				// try all possible moves here
				for (int d = 0; d < DIRECTIONS[0].length; d++) {
					int r2 = r + DIRECTIONS[0][d];
					int c2 = c + DIRECTIONS[1][d];

					// if we can jump pegs, do so
					boolean jumped = tryJumpPeg(r, c, r2, c2);
					if (jumped) {
						// add this step to the current solution
						solutions.get(solutions.size() - 1).addStep(copyPegs());

						// recurse
						solveHelper(solutions);

						// undo the jump
						reverseJumpPeg(r, c, r2, c2);
						// remove this step from the current solution
						solutions.get(solutions.size() - 1).removeStep();
					}
				}
			}
		}
	}

	// pre: none
	// post: checks if the board is solved by having only 1 peg remaining
	private boolean solved() {
		boolean foundPeg = false;
		for (int r = 0; r < NUM_ROWS; r++) {
			for (int c = NUM_ROWS - 1 - r; c <= NUM_COLS - (NUM_ROWS - 1 - r); c += 2) {
				// check if we found a peg
				if (pegs[r][c] == 1) {
					// if we have already found a peg, the board is not solved
					if (foundPeg) {
						return false;
					} else {
						foundPeg = true;
					}
				}
			}
		}

		// we never found a second peg, meaning the board is solved
		return true;
	}

	// pre: tryJumpPeg has been called with the same coordinates (handled by calling
	// method)
	// post: the jump is undone
	private void reverseJumpPeg(int r1, int c1, int r2, int c2) {
		// make start peg occupied
		pegs[r1][c1] = 1;
		// make destination peg empty
		pegs[r2][c2] = -1;
		// make middle peg occupied
		pegs[r1 + (r2 - r1) / 2][c1 + (c2 - c1) / 2] = 1;
	}

	// pre: none
	// post: if the coordinates are valid for a jump, makes the jump from [r1][c1]
	// to [r2][c2]
	private boolean tryJumpPeg(int r1, int c1, int r2, int c2) {
		if (validJump(r1, c1, r2, c2)) {
			// make start peg empty
			pegs[r1][c1] = -1;
			// make destination peg occupied
			pegs[r2][c2] = 1;
			// make middle peg empty
			pegs[r1 + (r2 - r1) / 2][c1 + (c2 - c1) / 2] = -1;

			return true;
		} else {
			return false;
		}
	}

	// pre: start peg is occupied (handled by calling method)
	// post: if the coordinates are valid for a jump, return true
	private boolean validJump(int r1, int c1, int r2, int c2) {
		// check if coords are valid
		if (r1 < 0 || r1 >= NUM_ROWS || r2 < 0 || r2 >= NUM_ROWS || c1 < 0 || c1 >= NUM_COLS || c2 < 0
				|| c2 >= NUM_COLS) {
			return false;
		}

		// 1. destination peg is empty
		// 2. middle peg is occupied
		if (pegs[r2][c2] == -1 && pegs[r1 + (r2 - r1) / 2][c1 + (c2 - c1) / 2] == 1) {
			return true;
		} else {
			return false;
		}
	}

	// pre: none
	// post: returns a copy of pegs
	private int[][] copyPegs() {
		int[][] copy = new int[NUM_ROWS][NUM_COLS];
		for (int r = 0; r < NUM_ROWS; r++) {
			for (int c = NUM_ROWS - 1 - r; c <= NUM_COLS - (NUM_ROWS - 1 - r); c += 2) {
				// copy value
				copy[r][c] = pegs[r][c];
			}
		}
		return copy;
	}
}

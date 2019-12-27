import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.print.ServiceUIFactory;

public class PegGameSolverRunner {
	private static final int[][] BOARD_IMG = new int[Board.NUM_ROWS][Board.NUM_COLS];
	private static final Board BOARD = new Board();

	public static void main(String[] args) {
		// generate board image
		int i = 1;
		for (int r = 0; r < Board.NUM_ROWS; r++) {
			// create a triangle shape and skip a peg each time
			for (int c = Board.NUM_ROWS - 1 - r; c <= BOARD.NUM_COLS - (BOARD.NUM_ROWS - 1 - r); c += 2) {
				// set the peg to the current i
				BOARD_IMG[r][c] = i++;
			}
		}

		Scanner sc = new Scanner(System.in);
		boolean run = true;

		while (run) {
			printBoardImg();

			System.out.println("Enter the starting empty peg number to see solutions.");
			System.out.println("Enter anything else to quit.");

			String input = sc.nextLine();

			// if input is a number, try printing its solutions
			if (input.matches("[0-9]+")) {
				printSolutions(Integer.parseInt(input));
			} else {
				run = false;
			}
		}
	}

	// pre: none
	// post: prints solutions for the removal of peg num
	private static void printSolutions(int num) {
		// find coordinates of num
		int i = 1;
		int emptyR = -1;
		int emptyC = -1;
		for (int r = 0; r < Board.NUM_ROWS; r++) {
			for (int c = Board.NUM_ROWS - 1 - r; c <= BOARD.NUM_COLS - (BOARD.NUM_ROWS - 1 - r); c += 2) {
				// check if current peg is num
				if (i++ == num) {
					emptyR = r;
					emptyC = c;
				}
			}
		}

		BOARD.setup(emptyR, emptyC);
		List<BoardSolution> solutions = BOARD.solve();

		Path p = Paths.get("solutions.txt");
		PrintWriter pw;
		try {
			pw = new PrintWriter(Files.newBufferedWriter(p));
		} catch (IOException io) {
			throw new RuntimeException("newBufferedWriter threw IO Exception");
		}
		pw.println("Number of solutions: " + solutions.size());
		for (int s = 0; s < solutions.size(); s++) {
			pw.println("Solution " + s + ":");
			pw.println(solutions.get(s));
		}
		pw.close();

		// check if there are duplicates

		Scanner pr = new Scanner("solutions.txt");
		pr.nextLine();
		HashSet<String> set = new HashSet<>();
		boolean duplicates = false;
		while (pr.hasNext()) {
			pr.nextLine();
			StringBuilder sb = new StringBuilder();

			for (int l = 0; l < Board.NUM_ROWS; l++) {
				sb.append(pr.nextLine());
			}

			if (!set.add(sb.toString())) {
				duplicates = true;
			}
		}
		if (duplicates) {
			System.out.println("FOUND DUPLICATE SOLUTION");
		} else {
			System.out.println("No duplicate solutions found");
		}
		
		pr.close();
	}

	// pre: none
	// post: print the numbered board image (empty = space)
	private static void printBoardImg() {
		for (int r = 0; r < Board.NUM_ROWS; r++) {
			for (int c = 0; c < Board.NUM_COLS; c++) {
				if (BOARD_IMG[r][c] == 0) {
					System.out.print("  ");
				} else {
					System.out.print(BOARD_IMG[r][c] < 10 ? " " + BOARD_IMG[r][c] : BOARD_IMG[r][c]);
				}
			}
			System.out.println("\n");
		}
	}
}

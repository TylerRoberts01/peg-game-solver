import java.util.ArrayList;
import java.util.List;

public class BoardSolution {
	private List<int[][]> steps;

	// initializes a new BoardSolution
	public BoardSolution() {
		steps = new ArrayList<int[][]>();
	}

	// initializes a new BoardSolution with the same steps as bs
	public BoardSolution(BoardSolution bs) {
		this();

		for (int[][] step : bs.getSteps()) {
			addStep(step);
		}
	}

	// pre: none
	// post: returns a shallow copy of steps
	public List<int[][]> getSteps() {
		return steps;
	}

	// pre: none
	// post: adds this board as a step
	public void addStep(int[][] step) {
		steps.add(step);
	}

	// pre: none
	// post: removes the last step
	public void removeStep() {
		steps.remove(steps.size() - 1);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (int r = 0; r < Board.NUM_ROWS; r++) {
			for (int s = 0; s < steps.size(); s++) {
				int[][] step = steps.get(s);
				for (int c = 0; c < step[r].length; c++) {
					// 0 = hole, 1 = peg, [SPACE] = nothing
					if (step[r][c] == -1) {
						result.append("0");
					} else if (step[r][c] == 1) {
						result.append("1");
					} else {
						result.append(" ");
					}
					result.append(" ");
				}
				result.append(" | ");
			}
			result.append("\n");
		}

		return result.toString();
	}
}

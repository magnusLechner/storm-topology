package at.testing.quick;

import java.text.DecimalFormat;

import at.testing.util.BasicUtil;

public class GridSearchParser {

	public static void main(String[] args) {
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/3042/pos/rbf/grob_2";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/2359/pos/rbf/grob_2";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/709/pos/rbf/grob_2";

		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/3042/pos/rbf/fein_1";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/3042/pos/rbf/fein_2";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/2359/pos/rbf/fein_1";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/709/pos/rbf/fein_1";

		
		//LINEAR
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/3042/pos/linear/grob";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/2359/pos/linear/grob";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/709/pos/linear/grob";
		
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/3042/pos/linear/fein";;
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/2359/pos/linear/fein";
//		String path = "/home/magnus/workspace/storm-topology/src/main/evaluation/parameter_search_results/709/pos/linear/fein";

		String[] lines = BasicUtil.readLines(path);

		int cLenght = 11;
		int gammaLength = 10;
		double[][] grid = new double[cLenght][gammaLength];

//		i;j;C;gamma;accuracy;time_ms
		for (String line : lines) {
			String[] parts = line.split("\t");
			int c = -100;
			int gamma = -100;

			try {
				c = Double.valueOf(parts[0]).intValue();
				gamma = Double.valueOf(parts[1]).intValue();
				grid[c][gamma] = Double.valueOf(parts[4]);
			} catch (Exception e) {
				continue;
			}
		}

		// grob
//		double[] values = { 0.6000, 0.6125, 0.6250, 0.6375, 0.6500, 0.6625, 0.6750, 0.6875, 0.7000, 0.7125, 0.7250,
//				0.7375, 0.7500, 0.7625, 0.7750, 0.7875, 1.0 };
		// fein
		double[] values = { 0.7375, 0.7400, 0.7425, 0.7450, 0.7475, 0.7500, 0.7525, 0.7550, 0.7575, 0.7600, 0.7625,
				0.7650, 0.7675, 0.7700, 0.7725, 0.7750, 0.7775, 0.7800, 0.7825, 1.0 };

		double max = -100.0;
		double min = 10000.0;
		DecimalFormat df = new DecimalFormat("0.000");
		for (int i = cLenght - 1; i >= 0; i--) {
			String line = "";
			for (int j = 0; j < gammaLength; j++) {

				int c = 0;
				boolean isSmaller = false;
				while (!isSmaller) {
					if (grid[i][j] >= values[c]) {
						c++;
					} else {
						isSmaller = true;
					}
				}
				

				String val = df.format(grid[i][j]).replace(".", ",") + "(" + (c-1) + ")" + "\t";
				if (grid[i][j] > max) {
					max = grid[i][j];
				}
				if (grid[i][j] < min && grid[i][j] != 0) {

					min = grid[i][j];
				}

				line += val;
			}
			System.out.println(line);
		}

		System.out.println();
		System.out.println("MAX: " + max);
		System.out.println("MIN: " + min);

//		C
//		|
//		|
//		|
//		|_ _ _ _gamma
//		0,0 bottom left
	}

}

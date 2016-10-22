package at.testing.quick;

import java.text.DecimalFormat;

public class ColorMaker {

	public static void main(String[] args) {
		int numTiles = 18;
		
		int startRed = 225;
		int startGreen = 240;
		int startBlue = 255;

		int endRed = 0;
		int endGreen = 124;
		int endBlue = 255;

		int diffRed = startRed - endRed;
		int diffGreen = startGreen - endGreen;

		int stepsRed = diffRed / numTiles;
		int stepsGreen = diffGreen / numTiles;

		for (int i = 0; i < numTiles; i++) {
			int currRed = startRed - i * stepsRed;
			int currGreen = startGreen - i * stepsGreen;
			int currBlue = startBlue;
			DecimalFormat df = new DecimalFormat("0.000");
			System.out.println("<color name=\"b" + i + "\" value=\"" + df.format(calc(currRed)).replace(",", ".") + " "
					+ df.format(calc(currGreen)).replace(",", ".") + " " + df.format(calc(currBlue)).replace(",", ".") + "\"/>");
		}
		
		DecimalFormat df = new DecimalFormat("0.0000");
		for(int i = 0; i < 19; i++) {
			System.out.print(df.format(0.7375 + (i * 0.0025)).replace(",", ".") + ", ");
		}
	}

	private static double calc(int i) {
		return (double) i / 255;
	}
}

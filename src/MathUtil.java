import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

public class MathUtil {

	/**
	 * Return a mod b
	 *
	 * @param a
	 *            The value
	 * @param b
	 *            The value to be modded by
	 * @return a mod b
	 */
	public static int mod(int a, int b) {
		int val = a % b;
		if (val < 0) {
			val = b + val;
		}
		return val;
	}

	public static Vector2D drawBezier(double t, List<Vector2D> points) {
		int n = points.size() - 1;
		Vector2D sum = new Vector2D(0,0);
		for(int i =0 ; i < points.size(); i++){
			double coeff = ArithmeticUtils.binomialCoefficient(n, i)*FastMath.pow(1-t, n-i)*FastMath.pow(t,i);
			Vector2D thisPoint = points.get(i).scalarMultiply(coeff);
			sum = sum.add(thisPoint);
		}
		return sum;
	}
}

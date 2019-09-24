package render;

public class point3d {
	double x;
	double y;
	double z;

	public point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;

	}

	public void transform(double[][] matrix) {
		point3d buffer = new point3d(
				x * matrix[0][0] + y * matrix[0][1] + z * matrix[0][2],
				x * matrix[1][0] + y * matrix[1][1] + z * matrix[1][2],
				x * matrix[2][0] + y * matrix[2][1] + z * matrix[2][2]);
		this.x=buffer.x;
		this.y=buffer.y;
		this.z=buffer.z;
	}

}

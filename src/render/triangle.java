package render;

public class triangle {
point3d a;
point3d b;
point3d c;
	public triangle(point3d a, point3d b, point3d c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	public void transform(double[][] matrix){
		a.transform(matrix);b.transform(matrix);
		c.transform(matrix);
	}

}

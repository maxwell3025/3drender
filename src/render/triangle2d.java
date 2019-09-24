package render;

public class triangle2d {
	point2d a;
	point2d b;
	point2d c;

	public triangle2d(point2d a, point2d b, point2d c) {
		this.a=a;
		this.b=b;
		this.c=c;
	}
	public triangle2d(triangle2d a) {
		this.a=a.a;
		this.b=a.b;
		this.c=a.c;
	}
	

}

package render;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import javafx.geometry.Point3D;

@SuppressWarnings("serial")
public class Renderwindow extends JFrame implements Runnable, MouseMotionListener, MouseWheelListener {
	BufferedImage screenbuffer;
	Graphics2D graphics;
	double[][] starX;
	double[][] starY;
	double[][] starZ;
	double mousex;
	double mousey;
	int m_stars;
	double root3 = Math.sqrt(3) / 2;
	int screenx;
	int screeny;
	int screenxr;
	int screenyr;
	double veiwdist = 10;

	public Renderwindow(int xsize, int ysize, int stars) {

		screenx = xsize;
		screeny = ysize;
		setSize(xsize, ysize);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		m_stars = stars;
		starX = new double[stars][3];
		starY = new double[stars][3];
		starZ = new double[stars][3];
		screenbuffer = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
		graphics = screenbuffer.createGraphics();
		try {
			new Robot().mouseMove(getWidth() / 2, getHeight() / 2);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				screenx = Math.min(getWidth(), getHeight());
				screeny = Math.min(getWidth(), getHeight());
				screenxr = getWidth();
				screenyr = getHeight();
				screenbuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				graphics = screenbuffer.createGraphics();

			}
		});
		for (int i = 0; i < m_stars; i++) {
			initstar(i);
		}
		setVisible(true);

	}

	public void paint(Graphics g) {
		g.drawImage(screenbuffer, 0, 0, null);
	}

	public void update() {
		graphics.clearRect(0, 0, screenxr, screenyr);
		int[][] screenbuf = new int[screenx][2];
		scanconverttri(new point2d(0.0, 0.0), new point2d(0.1,0.1 ), new point2d(0.0, 0.1),screenbuf, false);
		drawscanbuf(screenbuf);
		for (int i = 0; i < m_stars; i++) {
			for (int j = 0; j < 3; j++) {
				starZ[i][j] -= 0.01;
				while (starZ[i][j] <= -5) {
					initstar(i);

				}

			}
			double[][] matrix1 = { { Math.cos(mousex), 0, -Math.sin(mousex) }, { 0, 1, 0 },
					{ Math.sin(mousex), 0, Math.cos(mousex) } };
			graphics.setColor(Color.WHITE);
			double[][] matrix2 = { { 1, 0, 0 }, { 0, Math.cos(mousey), -Math.sin(mousey) },
					{ 0, Math.sin(mousey), Math.cos(mousey) } };
			graphics.setColor(Color.WHITE);
			point3d p1 = new point3d(starX[i][0], starY[i][0], starZ[i][0]);
			point3d p2 = new point3d(starX[i][1], starY[i][1], starZ[i][1]);
			point3d p3 = new point3d(starX[i][2], starY[i][2], starZ[i][2]);
			triangle targ = new triangle(p1, p2, p3);
			targ.transform(matrix1);
			targ.transform(matrix2);
			drawtri(targ.a, targ.b, targ.c);

		}
		repaint();
	}

	public void initstar(int index) {
		double centerX = (Math.random() - 0.5) * 10;
		double centerY = (Math.random() - 0.5) * 10;
		double centerZ = (Math.random() - 0.5) * 10;
		for (int i = 0; i < 3; i++) {
			starX[index][i] = centerX + Math.random() * 0.5;
			starY[index][i] = centerY + Math.random() * 0.5;
			starZ[index][i] = centerZ + Math.random() * 0.5;
		}

	}

	public point2d findrender(double x, double y, double z) {
		return new point2d(x / (z - veiwdist), y / (z - veiwdist));

	}

	public void scanconverttri(point2d min, point2d mid, point2d max, int[][] scanbuffer, boolean handedness) { 
		writeline(min,max,handedness, scanbuffer);
		writeline(mid,min,!handedness, scanbuffer);
		writeline(max ,mid,!handedness, scanbuffer);
	}

	public void writeline(point2d end, point2d beginning, boolean onright, int[][] scanbuffer) {
		double xstep = end.x - beginning.x;
		double ystep = end.y - beginning.y;
		double step = (xstep / ystep) / screenx;
		int side = 0;
		if (onright) {
			side = 1;
		}
		double start = beginning.x;
		if (beginning.y<end.y) {
			for (int i = converted(beginning.y); i < converted(end.y); i++) {
				start += step;
				scanbuffer[i][side] = converted(start);
			}
		} else {
			for (int i = converted(beginning.y); i > converted(end.y); i--) {
				start -= step;
				scanbuffer[i][side] = converted(start);
			}
		}
	}

	public void drawscanbuf(int[][] scanbuffer) {
		for (int i = 0; i < scanbuffer.length; i++) {
			graphics.drawRect(scanbuffer[i][0], i, scanbuffer[i][1] - scanbuffer[i][0], 0);
		}
	}

	public point2d findrender(point3d p2) {
		return findrender(p2.x, p2.y, p2.z);
	}

	public void drawtri(point3d a, point3d b, point3d c) {
		drawline(a, b);
		drawline(a, c);
		drawline(c, b);

	}

	public void drawline(point3d a, point3d b) {
		double dist = 0.002 / (Math.abs(findrender(a.x, a.y, a.z).x - findrender(b.x, b.y, b.z).x)
				+ Math.abs(findrender(a.x, a.y, a.z).y - findrender(b.x, b.y, b.z).y));
		for (double i = 0; i < 1; i += dist) {
			drawpoint(lerp(a.x, b.x, i), lerp(a.y, b.y, i), lerp(a.z, b.z, i));
		}
	}

	public double lerp(double a, double b, double c) {
		return ((1 - c) * a + c * b) * 0.5;
	}

	public int converted(double a) {
		return (int) Math.ceil(screenxr * 0.5 + screenx * a * 0.5);
	}

	public void drawpoint(double x, double y, double z) {
		if (z > -veiwdist) {
			point2d a = findrender(x, y, z);
			graphics.drawRect((int) (screenx * 0.5 + screenx * a.x * 0.5), (int) (screeny * 0.5 + screeny * a.y * 0.5),
					0, 0);
		}

	}

	public static void main(String[] args) {
		Renderwindow r = new Renderwindow(1000, 1000, 10);
		new Thread(r).start();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			update();
		}

	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		mousex = 8 * (e.getX() * 1.0 / screenx - 0.5);
		mousey = 8 * (e.getY() * 1.0 / screeny - 0.5);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		veiwdist += e.getWheelRotation() / 10.0;

	}

}

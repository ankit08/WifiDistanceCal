package com.aricent.wifidistancecal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**
 * Class to calculate the intersection of the Two and Three Circle
 * 
 * @author Ankit
 * 
 */
public class Intersect3Circles {

	protected static final String TAG = Intersect3Circles.class.getSimpleName()
			.toString();
	private CircleCoordinates cCord = new CircleCoordinates();
	
	// Set and Get X and Y Coordinate
	private String xCord;
	private String yCord;
	private String radius ;

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getxCord() {
		return xCord;
	}

	public void setxCord(String xCord) {
		this.xCord = xCord;
	}

	public String getyCord() {
		return yCord;
	}

	public void setyCord(String yCord) {
		this.yCord = yCord;
	}

	/**
	 * Method to locate the InterSection of two Circle
	 * 
	 * @param x0
	 * @param y0
	 * @param r0
	 * @param x1
	 * @param y1
	 * @param r1
	 * @return
	 */
	public int intersectionOf2Circles(double x0, double y0, double r0,
			double x1, double y1, double r1) {
		double a, dx, dy, d, h, rx, ry;
		double x2, y2;
		double xi, xm, yi, ym;

		/*
		 * these are vertical and horizontal distances between the circle
		 * centers.
		 */
		dx = x1 - x0;
		dy = y1 - y0;

		/* straight-line distance between the centers */
		d = Math.hypot(dx, dy);

		/* do these circles intersect ? */
		if (d > (r0 + r1)) {
			/* circles do not intersect. */
			// Log.d(TAG, " Circle do not Intersect ");
			
			Log.i(TAG," Circle 1 radius " + r0 + " Circle 2 Radius " + r1 + " Distance " + d);
			
			xi = ((x1 * r0) + (x0 * r1)) / (r1 + r0);
			yi = ((y1 * r0) + (y0 * r1)) / (r1 + r0);
			Log.d(TAG, " Circle do not Intersect " + " x1 " + xi + " y1 " + yi);
			cCord.setCircleInterX(xi);
			cCord.setCircleDistX(xi);
			cCord.setCircleInterY(yi);
			cCord.setCircleDistY(yi);
		} else if (d < Math.abs(r0 - r1)) {
			/* one circle is subset of the other */
			Log.d(TAG, " circle is subset of the other ");
			xi = ((x0 * r1) - (x1 * r0)) / (r1 - r0);
			yi = ((y0 * r1) - (y1 * r0)) / (r1 - r0);
			cCord.setCircleInterX(xi);
			cCord.setCircleDistX(xi);
			cCord.setCircleInterY(yi);
			cCord.setCircleDistY(yi);
		} else {

			/*
			 * 'point 2' is the point where the line through the circle
			 * intersection points crosses the line between the circle centers.
			 */

			/* Determine the distance from point 0 to point 2. */
			a = ((r0 * r0) - (r1 * r1) + (d * d)) / (2.0 * d);

			/* Determine the coordinates of point 2. */
			x2 = x0 + (dx * a / d);
			y2 = y0 + (dy * a / d);

			/*
			 * Determine the distance from point 2 to either of the intersection
			 * points.
			 */
			h = Math.sqrt((r0 * r0) - (a * a));

			/*
			 * Now determine the offsets of the intersection points from point
			 * 2.
			 */
			rx = -dy * (h / d);
			ry = dx * (h / d);
			// Log.i(TAG," intersectionOf2Circles rx " + rx + " ry " + ry);

			Log.i(TAG, " intersectionOf2Circles  " + (x2 + rx) + " x2 - rx  "
					+ (x2 - rx) + " y2 + ry " + (y2 + ry) + " y2 - ry "
					+ (y2 - ry));

			/* Determine the absolute intersection points. */
			xi = x2 + rx;
			xm = x2 - rx;
			yi = y2 + ry;
			ym = y2 - ry;

			/* Determine centre point between them */
			xi = (xi + xm) / 2;
			yi = (yi + ym) / 2;
			xm = xi;
			ym = yi;

			cCord.setCircleInterX(xi);
			cCord.setCircleDistX(xm);
			cCord.setCircleInterY(yi);
			cCord.setCircleDistY(ym);
		}
		setxCord(String.valueOf(xi));
		setyCord(String.valueOf(yi));
		return 1;
	}

	/**
	 * Method to locate Trilateration
	 * 
	 * @param x0
	 * @param y0
	 * @param r0
	 * @param x1
	 * @param y1
	 * @param r1
	 * @param x2
	 * @param y2
	 * @param r2
	 */
	public void findThreeCirclesIntersection(double x0, double y0, double r0,
			double x1, double y1, double r1, double x2, double y2, double r2) {

		Double xCheck = null;
		Double yCheck = null;
		Double xMinValue = null;
		Double xMaxvalue = null;
		Double yMinValue = null;
		Double yMaxvalue = null;

		List<Double> xCord = new ArrayList<Double>();
		List<Double> yCord = new ArrayList<Double>();
		List<Point> finalList = new ArrayList<Point>();

		try {
			// circle 1 and circle 2
			intersectionOf2Circles(x0, y0, r0, x1, y1, r1);
			xCord.add(cCord.getCircleInterX());
			xCord.add(cCord.getCircleDistX());
			yCord.add(cCord.getCircleInterY());
			yCord.add(cCord.getCircleDistY());

			// circle 2 and circle 3
			intersectionOf2Circles(x1, y1, r1, x2, y2, r2);

			xCord.add(cCord.getCircleInterX());
			xCord.add(cCord.getCircleDistX());
			yCord.add(cCord.getCircleInterY());
			yCord.add(cCord.getCircleDistY());

			// circle 3 and circle 1
			intersectionOf2Circles(x2, y2, r2, x0, y0, r0);
			xCord.add(cCord.getCircleInterX());
			xCord.add(cCord.getCircleDistX());
			yCord.add(cCord.getCircleInterY());
			yCord.add(cCord.getCircleDistY());

			Log.i(TAG, " Size of the list " + xCord.size());

			if (!xCord.isEmpty()) {
				xMinValue = Collections.min(xCord);
				Log.i(TAG, " Minimum Values X " + xMinValue);

				xMaxvalue = Collections.max(xCord);
				Log.i(TAG, " Maximum Values X " + xMaxvalue);
			}

			if (!yCord.isEmpty()) {
				yMinValue = Collections.min(yCord);
				Log.i(TAG, " Minimum Values Y " + yMinValue);

				yMaxvalue = Collections.max(yCord);
				Log.i(TAG, " Maximum Values Y " + yMaxvalue);
			}
			if (!xCord.isEmpty() && !yCord.isEmpty()) {
				for (int i = 0; i < xCord.size(); i++) {
					if (xCord.get(i) != xMinValue && xCord.get(i) != xMaxvalue
							&& yCord.get(i) != yMinValue
							&& yCord.get(i) != yMaxvalue
							&& xCord.get(i) != xCheck && yCord.get(i) != yCheck) {
						Point testPoint = new Point();

						xCheck = xCord.get(i);
						yCheck = yCord.get(i);

						testPoint.setxCord(String.valueOf(xCheck));
						testPoint.setyCord(String.valueOf(yCheck));

						finalList.add(testPoint);
						Log.i(TAG, " xCheck Value " + xCheck + " yCheck Value "
								+ yCheck);
					}
				}
				findCentrePoint(finalList);
			} else {
				Log.d(TAG, " Cordinate list is Empty");
			}
		} catch (Exception e) {
			Log.e(TAG, " Exception " + e.getMessage());
		}
	}

	/**
	 * Method to call Centroid Function
	 * 
	 * @param pointCord
	 */
	private void findCentrePoint(List<Point> pointCord) {
		if (pointCord.get(0) != null && pointCord.get(1) != null
				&& pointCord.get(2) != null) {
			Point a1 = pointCord.get(0);
			Point b1 = pointCord.get(1);
			Point c1 = pointCord.get(2);
			calculateCentroid(a1, b1, c1);
		}
	}

	/**
	 * Method to calculate Centroid using 
	 * Three Coordinates
	 * 
	 * @param a
	 * @param b
	 * @param c
	 */
	private void calculateCentroid(Point a, Point b, Point c) {
		double a1, a2, a3;
		double b1, b2, b3;
		double radius ;
		double dx ;
		double dy ;

		a1 = Double.parseDouble(a.getxCord());
		b1 = Double.parseDouble(a.getyCord());

		Log.i(TAG, "a1 " + a1 + " Value of b1 " + b1);

		a2 = Double.parseDouble(b.getxCord());
		b2 = Double.parseDouble(b.getyCord());
		Log.i(TAG, "a2 " + a2 + " Value of b2 " + b2);

		a3 = Double.parseDouble(c.getxCord());
		b3 = Double.parseDouble(c.getyCord());

		double tempX = (a1 + a2 + a3) / 3;
		double tempY = (b1 + b2 + b3) / 3;

		Log.i(TAG, " centroid " + " temp " + tempX + " temp y " + tempY);
		
		dx = tempX - a1 ;
		dy = tempY - b1 ;
		
		radius  = Math.hypot(dx, dy);
		
		// Radius of the circle
	
		setxCord(String.valueOf(tempX));
		setyCord(String.valueOf(tempY));
		
		setRadius(String.valueOf(radius));

	}
}

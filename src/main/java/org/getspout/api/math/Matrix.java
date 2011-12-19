package org.getspout.api.math;

/**
 * Representation of a square matrix
 * 
 * 
 *
 */
public class Matrix {
	int dimension;
	double[] data;

	/**
	 * Creates a new 4x4 matrix, set to the Identity Matrix
	 */
	public Matrix(){
		this(4);
	}
	/**
	 * Creates a new matrix with the given dimension
	 * @param dim
	 */
	public Matrix(int dim) {
		dimension = dim;
		data = new double[dim * dim];
		for (int x = 0; x < dim; x++) {
			for (int y = 0; y < dim; y++) {
				if (x == y) data[index(x, y, dim)] = 1;
				else data[index(x, y, dim)] = 0;
			}
		}
	}

	public int getDimension() {
		return dimension;
	}

	/**
	 * Gets the value at the given row and colum
	 * @param row
	 * @param column
	 * @return
	 */
	public double get(int row, int column) {
		if ((row < 0 || row > dimension)) throw new IllegalArgumentException("Row must be between 0 and " + dimension);
		if ((column < 0 || column > dimension)) throw new IllegalArgumentException("Column must be between 0 and " + dimension);
		return data[index(row, column, dimension)];
	}

	/**
	 * Sets the value at the given row and column
	 * @param row
	 * @param column
	 * @param value
	 */
	public void set(int row, int column, double value) {
		if ((row < 0 || row > dimension)) throw new IllegalArgumentException("Row must be between 0 and " + dimension);
		if ((column < 0 || column > dimension)) throw new IllegalArgumentException("Column must be between 0 and " + dimension);
		data[index(row, column, dimension)] = value;
	}

	/**
	 * Multiplies this matrix with the provided matrix
	 * @param that
	 * @return
	 */
	public Matrix multiply(Matrix that) {
		return Matrix.multiply(this, that);
	}
	/**
	 * Adds this matrix to the given matrix
	 * @param that
	 * @return
	 */
	public Matrix add(Matrix that) {
		return Matrix.add(this, that);
	}

	/**
	 * Adds two matricies together
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix add(Matrix a, Matrix b) {
		if (a.dimension != b.dimension) throw new IllegalArgumentException("Matrix Dimensions must be equal");
		Matrix res = new Matrix(a.dimension);
		for (int x = 0; x < res.dimension; x++) {
			for (int y = 0; y < res.dimension; y++) {
				res.data[index(x, y, res.dimension)] = a.data[index(x, y, res.dimension)] + b.data[index(x, y, res.dimension)];
			}
		}
		return res;
	}

	/**
	 * Multiplies two matricies together
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix multiply(Matrix a, Matrix b) {
		if (a.dimension != b.dimension) throw new IllegalArgumentException("Matrix Dimensions must be equal");
		Matrix res = new Matrix(a.dimension);
		for (int i = 0; i < res.dimension; i++) {
			for (int j = 0; j < res.dimension; j++) {
				res.data[index(i, j, res.dimension)] = 0;
				for (int k = 0; k < res.dimension; k++) {
					res.data[index(i, j, res.dimension)] += a.data[index(i, k, res.dimension)] * a.data[index(k, j, res.dimension)];
				}
			}
		}
		return res;
	}


	private static int index(int x, int y, int dim) {
		return (y * dim + x);
	}

	/**
	 * Creates and returns a 4x4 identity matrix
	 * @return
	 */
	public static Matrix createIdentity() {
		return new Matrix(4);
	}
	/**
	 * Creates and returns a 4x4 matrix that represents the translation provided by the given Vector3
	 * @param vector
	 * @return
	 */
	public static Matrix translate(Vector3 vector) {
		Matrix res = createIdentity();
		res.set(0, 3, vector.getX());
		res.set(1, 3, vector.getY());
		res.set(2, 3, vector.getY());
		return res;
	}
	/**
	 * Creates and returns a 4x4 uniform scalar matrix
	 * @param ammount
	 * @return
	 */
	public static Matrix scale(double ammount) {
		Matrix res = createIdentity();
		res.set(0, 0, ammount);
		res.set(1, 1, ammount);
		res.set(2, 2, ammount);
		return res;
	}
	/**
	 * Creates and returns a 4x4 scalar matrix that scales each axis given by the provided Vector3
	 * @param ammount
	 * @return
	 */
	public static Matrix scale(Vector3 ammount) {
		Matrix res = createIdentity();
		res.set(0, 0, ammount.getX());
		res.set(1, 1, ammount.getY());
		res.set(2, 2, ammount.getZ());
		return res;
	}
	/**
	 * Creates and returns a 4x4 rotation matrix around the X axis
	 * @param rot
	 * @return
	 */
	public static Matrix rotateX(double rot){
		Matrix res = createIdentity();
		res.set(1, 1, MathHelper.cos(Math.toRadians(rot)));
		res.set(1, 2, -MathHelper.sin(Math.toRadians(rot)));
		res.set(2, 1, MathHelper.sin(Math.toRadians(rot)));
		res.set(2, 2, MathHelper.cos(Math.toRadians(rot)));	
		
		return res;		
	}
	/**
	 * Creates and returns a 4x4 rotation matrix around the Y axis
	 * @param rot
	 * @return
	 */
	public static Matrix rotateY(double rot){
		Matrix res = createIdentity();
		res.set(0, 0, MathHelper.cos(Math.toRadians(rot)));
		res.set(0, 2, MathHelper.sin(Math.toRadians(rot)));
		res.set(2, 0, -MathHelper.sin(Math.toRadians(rot)));
		res.set(2, 2, MathHelper.cos(Math.toRadians(rot)));				
		return res;		
	}
	/**
	 * Creates and returns a 4x4 rotation matrix around the Z axis
	 * @param rot
	 * @return
	 */
	public static Matrix rotateZ(double rot){
		Matrix res = createIdentity();
		res.set(0, 0, MathHelper.cos(Math.toRadians(rot)));
		res.set(0, 1, -MathHelper.sin(Math.toRadians(rot)));
		res.set(1, 0, MathHelper.sin(Math.toRadians(rot)));
		res.set(1, 1, MathHelper.cos(Math.toRadians(rot)));				
		return res;		
	}
}

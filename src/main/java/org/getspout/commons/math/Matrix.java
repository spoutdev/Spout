package org.getspout.commons.math;

public class Matrix {
	int dimension;
	double[] data;
	
	public Matrix(int dim){
		dimension = dim;
		data = new double[dim*dim];
		for(int x = 0; x < dim; x++){
			for(int y = 0; y < dim; y++){
				if(x == y) data[index(x,y, dim)] = 1;
				else data[index(x,y, dim)]= 0;
			
			}
		}
	}
	
	public int getDimension(){
		return dimension;
	}
	
	public double get(int row, int column){
		if((row < 0 || row > dimension)) throw new IllegalArgumentException("Row must be between 0 and "+ dimension);
		if((column < 0 || column > dimension)) throw new IllegalArgumentException("Column must be between 0 and "+ dimension);
		return data[index(row, column, dimension)];
	}
	
	
	
	
	
	public static Matrix Add(Matrix a, Matrix b){
		if(a.dimension != b.dimension) throw new IllegalArgumentException("Matrix Dimensions must be equal");
		Matrix res = new Matrix(a.dimension);
		for(int x = 0; x < res.dimension; x++){
			for(int y = 0; y < res.dimension; y++){
				res.data[index(x, y, res.dimension)] = a.data[index(x, y, res.dimension)] + b.data[index(x, y, res.dimension)];
			}
		}
		return res;
	}
	
	public static Matrix Multiply(Matrix a, Matrix b){
		if(a.dimension != b.dimension) throw new IllegalArgumentException("Matrix Dimensions must be equal");
		Matrix res = new Matrix(a.dimension);
		for(int i = 0; i< res.dimension; i++)
		{
			for(int j = 0; j <res.dimension; j++)
			{
				res.data[index(i,j,res.dimension)] = 0;
				for(int k = 0; k < res.dimension; k++)
				{
					res.data[index(i,j,res.dimension)] += a.data[index(i,k, res.dimension)] * a.data[index(k,j, res.dimension)];
				}
			}
		
		}
		return res;
	}
	
	
	public static Matrix createIdentity(){
		return new Matrix(4);
	}
	
	private static int index(int x, int y, int dim){
		return (y * dim + x);
	}
	
}

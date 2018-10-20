/**
 *  Tetris: It's Tetris...
 * 
 *  Copyright (C) 2010, 2011 Andrew Cox
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package adcox.tetris.objects;

import java.awt.Point;

/**
 *
 * @author Andrew
 * @version Jun 24, 2011
 */
public class Matrix {
    protected int rows, columns;
    protected double[][] data;
    
    public Matrix(double[][] aMatrix, int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        data = new double[rows][columns];
        data = aMatrix.clone();
    }//==================================
    
    public Matrix(double[] aVector, boolean columnVector){
        if(columnVector){
            columns = 1;
            rows = aVector.length;
            data = new double[rows][columns];
            for(int i = 0; i < rows; i++){
                data[i][0] = aVector[i];
            }
        }
        else{
            columns = aVector.length;
            rows = 1;
            data = new double[rows][columns];
            for(int i = 0; i < columns; i++){
                data[0][i] = aVector[i];
            }
        }
    }//======================================
    
    /**
     * Add two matrices, A and B, together. In short, this function returns the
     * result of A + B
     * @param A the first matrix
     * @param B the second matrix
     * @return a matrix representing the addition of A and B
     */
    public static Matrix add(Matrix A, Matrix B){
        if(A.getColumns() == B.getColumns() && A.getRows() == B.getRows()){
            double[][] C = new double[A.getRows()][A.getColumns()];
            for(int r = 0; r < A.getRows(); r++){
                for(int c = 0; c < A.getColumns(); c++){
                    C[r][c] = A.getData()[r][c] + B.getData()[r][c];
                }
            }
            return new Matrix(C, A.getRows(), A.getColumns());
        }
        System.err.println("Those two matrices cannot be added.");
        return null;
    }//==============================
    
    /**
     * Converts a point into a column or row vector matrix
     * @param p the Point p
     * @param columnVector a boolean representing whether or not you want the 
     * matrix to be a column vector
     * @return a Matrix that contains the Point's data
     */
    public static Matrix pointToMatrix(Point p, boolean columnVector){
        double[][] C;
        if(columnVector){
            C = new double[2][1];
            C[0][0] = p.x;
            C[1][0] = p.y;
            return new Matrix(C, 2, 1);
        }
        else{
            C = new double[1][2];
            C[0][0] = p.x;
            C[0][1] = p.y;
            return new Matrix(C, 1, 2);
        }
    }//====================================
    
    /**
     * Convert a matrix into a point. The matrix must either by a 2 by 1 or 1 by 2.
     * @param A the matrix to convert
     * @return a point representing the data in A
     */
    public static Point matrixToPoint(Matrix A){
        //round the values of A
        for(int r = 0; r < A.getRows(); r++){
            for(int c = 0; c < A.getColumns(); c++){
                A.setValue(r, c, (double)Math.round(A.getData(r, c)));
            }
        }
        if(A.getColumns() == 2 && A.getRows() == 1){
            return new Point((int)A.getData(0, 0), (int)A.getData(0, 1));
        }
        if(A.getRows() == 2 && A.getColumns() == 1){
            return new Point((int)A.getData(0,0), (int)A.getData(1, 0));
        }
        System.err.println("A must be a column or row vector of only two dimensions.");
        return null;
    }//===============================
    
    /**
     * Rotate matrix A by a given number of radians
     * @prerequisite A MUST have two columns.
     * @param A an x by 2 matrix, where x is any real, positive integer
     * @param radians the number of radians to rotate the matrix by
     * @return the rotated matrix A
     */
    public static Matrix rotate(Matrix A, double radians){
        if(A.getColumns() == 2){
            double[][] R = new double[2][2];
            R[0][0] = Math.cos(radians);
            R[1][0] = Math.sin(radians);
            R[0][1] = -1*Math.sin(radians);
            R[1][1] = R[0][0];
            Matrix rotationMatrix = new Matrix(R, 2, 2);
            return multiply(A, rotationMatrix);
        }
        else{
            System.err.println("A must have two columns to be rotated.");
            return null;
        }
    }//=============================
    
    /**
     * Subtract B from A.  This function returns the result of A - B
     * @param A the first Matrix
     * @param B the second Matrix
     * @return a Matrix representing the subtraction of B from A.
     */
    public static Matrix subtract(Matrix A, Matrix B){
        if(A.getColumns() == B.getColumns() && A.getRows() == B.getRows()){
            double[][] C = new double[A.getRows()][A.getColumns()];
            for(int r = 0; r < A.getRows(); r++){
                for(int c = 0; c < A.getColumns(); c++){
                    C[r][c] = A.getData()[r][c] - B.getData()[r][c];
                }
            }
            return new Matrix(C, A.getRows(), A.getColumns());
        }
        System.err.println("Those two matrices cannot be subtracted.");
        return null;
    }//==============================
    
    /**
     * Multiply A by B
     * @prerequisite Matrix A and B must have sizes (a, x) (x, b), respectively,
     * where a, x and b are all real, positive integers.
     * @param A the first Matrix
     * @param B the second Matrix
     * @return the result of the multiplication A*B
     */
    public static Matrix multiply(Matrix A, Matrix B){
        if(A.getColumns() == B.getRows()){
            double[][] C = new double[A.getRows()][B.getColumns()];
            //go through all the spots in the new matrix, C
            for(int r = 0; r < A.getRows(); r++){
                for(int c = 0; c < B.getColumns(); c++){
                    //go across the columns of A, and down the row in B and do your multiplication!
                    for(int i =0; i < A.getColumns(); i++){
                        C[r][c] += A.getData(r, i)*B.getData(i, c);
                    }
                }
            }
            
            return new Matrix(C, A.getRows(), B.getColumns());
        }
        System.err.println("Those two matrices cannot be multiplied.");
        return null;
    }//==================================
    
    /**
     * Multiplies a matrix by a scalar
     * @param A a matrix
     * @param scalar a real integer
     * @return the scalar multiplication of scalar*A
     */
    public static Matrix scalarMultiply(Matrix A, int scalar){
        double[][] C = new double[A.getRows()][A.getColumns()];
        for(int r = 0; r < A.getRows(); r++){
            for(int c = 0; c < A.getColumns(); c++){
                C[r][c] = A.getData(r, c)*scalar;
            }
        }
        
        return new Matrix(C, A.getRows(), A.getColumns());
    }//=====================================
    
    /**
     * Print out a matrix using System.out.print
     * @param A the Matrix to be printed
     */
    public static void printMatrix(Matrix A){
        System.out.print("[");
        for(int r = 0; r < A.getRows(); r++){
            System.out.print("[");
            for(int c = 0; c < A.getColumns(); c++){
                if(c > 0 && c != A.getColumns())
                    System.out.print(",");
                System.out.print(String.format("%15f", A.getData(r, c)));
            }
            System.out.print("]\n");
        }
        System.out.print("]\n");
    }//=====================================
    
    public int getRows(){return rows;}
    public int getColumns(){return columns;}
    public double[][] getData(){return data;}
    public double getData(int row, int column){return data[row][column];}
    public void setValue(int row, int column, double value){data[row][column] = value;}
   
}

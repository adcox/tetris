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

import adcox.tetris.gui.Tetris;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Andrew
 * @version Dec 10, 2010
 */
public class Piece {

    protected Color color;
    protected int typeIndex;
    protected int playerNumber;
    protected Point gridLoc;
    protected ArrayList<Square> squares;
    protected int numSquares;
    protected boolean hasHit = false;
    protected boolean toppedOut = false;

    /**
     * positions keeps track of the positions of the squares in a piece.
     * The first index is the piece
     * The second index is the square number
     * The value is the x and y offset from gridLoc
     */
    protected Point[][] positions = new Point[10][8];

    public static enum Type{Square, Stick, S, T, Z, L, BackL, LongStick, HollowBox, N};
    Type type;

    public static Color purple = new Color(150, 0, 160);
    public static Color brown = new Color(95, 55, 0);
    
    public Piece(int typeIndex, Point gridStartLoc, ArrayList<Square> allSquares, int playerNum){
        this.typeIndex = typeIndex;
        switch(this.typeIndex){
            case 0: this.type = Type.Square; numSquares = 4;break;
            case 1: this.type = Type.Stick; numSquares = 4;break;
            case 2: this.type = Type.S; numSquares = 4; break;
            case 3: this.type = Type.T; numSquares = 4; break;
            case 4: this.type = Type.Z; numSquares = 4; break;
            case 5: this.type = Type.L; numSquares = 4; break;
            case 6: this.type = Type.BackL; numSquares = 4; break;
            case 7: this.type = Type.LongStick; numSquares = 8; break;
            case 8: this.type = Type.HollowBox; numSquares = 8;break;
            case 9: this.type = Type.N; numSquares = 7; break;
            default: this.type = Type.Square; numSquares = 4;
        }
        gridLoc = (Point)gridStartLoc.clone();
        squares = new ArrayList<Square>();
        playerNumber = playerNum;
        initPiece();
        if(!allSquares.isEmpty() && !isValidMove(allSquares))
            toppedOut = true;
    }//==============================

    private void initPiece(){
        if(type == Type.Square){
            color = Color.cyan;
            //initialize the squares
            positions[typeIndex][0] = new Point(0, 0);
            positions[typeIndex][1] = new Point(0, -1);
            positions[typeIndex][2] = new Point(1, 0);
            positions[typeIndex][3] = new Point(1, -1);
        }
        if(type == Type.Stick){
            color = Color.red;
            positions[typeIndex][0] = new Point(0, -1);
            positions[typeIndex][1] = new Point(0, 0);
            positions[typeIndex][2] = new Point(0, 1);
            positions[typeIndex][3] = new Point(0, 2);
        }
        if(type == Type.T){
            color = Color.gray;

            positions[typeIndex][0] = new Point(1,0);
            positions[typeIndex][1] = new Point(0,0);
            positions[typeIndex][2] = new Point(0,1);
            positions[typeIndex][3] = new Point(-1,0);
        }
        if(type == Type.Z){
            color = Color.green;

            positions[typeIndex][0] = new Point(-1,-1);
            positions[typeIndex][1] = new Point(0,-1);
            positions[typeIndex][2] = new Point(0,0);
            positions[typeIndex][3] = new Point(1,0);
        }
        if(type == Type.S){
            color = Color.blue;
            positions[typeIndex][0] = new Point(1,-1);
            positions[typeIndex][1] = new Point(0,-1);
            positions[typeIndex][2] = new Point(0,0);
            positions[typeIndex][3] = new Point(-1,0);
        }
        if(type == Type.L){
            color = Color.yellow;

            positions[typeIndex][0] = new Point(1,0);
            positions[typeIndex][1] = new Point(0,0);
            positions[typeIndex][2] = new Point(-1,0);
            positions[typeIndex][3] = new Point(-1,1);
        }
        if(type == Type.BackL){
            color = purple;
            positions[typeIndex][0] = new Point(1,1);
            positions[typeIndex][1] = new Point(1,0);
            positions[typeIndex][2] = new Point(0,0);
            positions[typeIndex][3] = new Point(-1,0);
        }
        if(type == Type.HollowBox){
            color = Color.white;

            positions[typeIndex][0] = new Point(-1, 1);
            positions[typeIndex][1] = new Point(-1, 0);
            positions[typeIndex][2] = new Point(-1, -1);
            positions[typeIndex][3] = new Point(0, -1);
            positions[typeIndex][4] = new Point(1, -1);
            positions[typeIndex][5] = new Point(1, 0);
            positions[typeIndex][6] = new Point(1, 1);
            positions[typeIndex][7] = new Point(0, 1);
        }
        if(type == Type.LongStick){
            color = brown;

            positions[typeIndex][0] = new Point(-3, 0);
            positions[typeIndex][1] = new Point(-2, 0);
            positions[typeIndex][2] = new Point(-1, 0);
            positions[typeIndex][3] = new Point(0, 0);
            positions[typeIndex][4] = new Point(1, 0);
            positions[typeIndex][5] = new Point(2, 0);
            positions[typeIndex][6] = new Point(3, 0);
            positions[typeIndex][7] = new Point(4, 0);
        }
        if(type == Type.N){
            color = Color.orange;
            positions[typeIndex][0] = new Point(-1, 1);
            positions[typeIndex][1] = new Point(-1, 0);
            positions[typeIndex][2] = new Point(-1, -1);
            positions[typeIndex][3] = new Point(0, -1);
            positions[typeIndex][4] = new Point(1, -1);
            positions[typeIndex][5] = new Point(1, 0);
            positions[typeIndex][6] = new Point(1, 1);
        }

        for(int i = 0; i < numSquares; i++){
            squares.add(new Square(color, new Point(gridLoc.x + positions[typeIndex][i].x,
                            gridLoc.y + positions[typeIndex][i].y), Tetris.SQUARE_SIZE, playerNumber));
        }
    }//============================

    /**
     * This function tells the piece to drop until it hits the pieces below it.
     * @param allSquares an ArrayList of Square objects that represents all the
     * squares that make up the bottom of the tetris screen; all the squares that
     * have been placed already
     * @return the number of spots the piece dropped. One spot is one square height
     */
    public int drop(ArrayList<Square> allSquares){
        int numSpotsDropped = 0;
        while(hasHit == false){
            moveVertically(allSquares);
            numSpotsDropped++;
        }
        return numSpotsDropped;
    }//===============================

    /**
     * This function determines if the current position of the piece is allowable.
     * Non-allowable positions include intersecting other pieces or going off the
     * screen.
     * @param allSquares an ArrayList of Square objects that represents all the
     * squares that make up the bottom of the tetris screen; all the squares
     * that have been played already.
     * @return a boolean representing whether the current position is a valid one.
     */
    private boolean isValidMove(ArrayList<Square> allSquares){
        boolean isValid = true;
        for(int ss = 0; ss < squares.size(); ss++){
            for(int s = 0; s < allSquares.size(); s++){
                if(allSquares.get(s).getGridLoc().equals(squares.get(ss).getGridLoc())){
                    isValid = false;
                }
            }
            //also return false if the piece goes outside the screen
            int gridX = squares.get(ss).getGridLoc().x;
            int gridY = squares.get(ss).getGridLoc().y;
            if(gridX < 0 || gridX > 9 || gridY < 0 || gridY > 19)
                isValid = false;
        }
        return isValid;
    }//=================================
    
    /**
     * This function rotates the piece by 90 degrees using matrix multiplication
     * on the 2-D coordinates of each square.  For more information, take a 
     * Linear Algebra course
     * @param allSquares an ArrayList of Square objects that represents all the
     * squares that make up the bottom of the tetris screen; all the squares
     * that have been played already.
     */
    public void rotate(ArrayList<Square> allSquares){
        //don't spin the square
        if(type == Type.Square){return;}
        
        squares.clear();
        for(int i = 0; i < numSquares; i++){
            Matrix position = Matrix.pointToMatrix(positions[typeIndex][i], false);
            positions[typeIndex][i] = Matrix.matrixToPoint(Matrix.rotate(position, Math.PI/2));
            squares.add(new Square(color, new Point(gridLoc.x + positions[typeIndex][i].x,
                    gridLoc.y + positions[typeIndex][i].y), Tetris.SQUARE_SIZE, playerNumber));
        }
        
        //now check to see if the new position is valid
        if(!isValidMove(allSquares)){
            //move (re-rotate) the piece back
            squares.clear();
            for(int i = 0; i < numSquares; i++){
                Matrix position = Matrix.pointToMatrix(positions[typeIndex][i], false);
                positions[typeIndex][i] = Matrix.matrixToPoint(Matrix.rotate(position, Math.PI/-2));
                squares.add(new Square(color, new Point(gridLoc.x + positions[typeIndex][i].x,
                        gridLoc.y + positions[typeIndex][i].y), Tetris.SQUARE_SIZE, playerNumber));
            }
        }
    }//==================================
    
    /**
     * This function moves the piece left or right.
     * @param right a boolean representing whether or not the piece will be moved
     * to the right.
     * @param allSquares an ArrayList of Square objects that represents all the
     * squares that make up the bottom of the tetris screen; all the squares
     * that have been played already. 
     */
    public void moveHorizontally(boolean right, ArrayList<Square> allSquares){

        //try moving the direction
        for(int i = 0; i < squares.size(); i++)
            squares.get(i).moveHoriztonally(right);
        if(right)
            gridLoc.x ++;
        else
            gridLoc.x --;

        //check to see if it has run into anything
        if(!isValidMove(allSquares)){
            //move it back
            for(int i = 0; i < squares.size(); i++)
                squares.get(i).moveHoriztonally(!right);
            if(right)
                gridLoc.x --;
            else
                gridLoc.x ++;
        }
    }//==============================

    /**
     * This function moves the piece vertically by one spot if such a move is 
     * possible
     * @param allSquares an ArrayList of Square objects that represents all the
     * squares that make up the bottom of the tetris screen; all the squares
     * that have been played already.
     */
    public void moveVertically(ArrayList<Square> allSquares){
        //try moving the piece down
        for(int i = 0; i < squares.size(); i++){
            squares.get(i).moveVertically(true);
        }
        gridLoc.y ++;

        //check to see if the move was valid
        if(!hasHit && !isValidMove(allSquares)){
            //if the piece has hit something, move it back up
            for(int i = 0; i < squares.size(); i++)
                squares.get(i).moveVertically(false);
            hasHit = true;
            gridLoc.y--;
        }
    }//===========================

    /**
     * This function is responsible for drawing the piece.
     * @param g the Graphics2D object that does the drawing
     * @param displayMode a Tetris-specific enumerated type for the display mode
     * the game is in.
     */
    public void draw(Graphics2D g, Tetris.DisplayMode displayMode){
        for(int i = 0; i < squares.size(); i++)
            squares.get(i).draw(g, displayMode);
    }//================================

    /**
     * @return an ArrayList of Squares that make up this piece
     */
    public ArrayList<Square> getSquares(){return squares;}

    /**
     * @return a boolean of whether or not this piece has hit the pieces at the 
     * bottom of the tetris screen.
     */
    public boolean hasHit(){return hasHit;}

    /**
     * Set whether or not this piece has hit the squares at the bottom of the 
     * Tetris screen.
     * @param hit a boolean representing the above-described condition.
     */
    public void setHasHit(boolean hit){hasHit = hit;}

    /**
     * @return a boolean representing whether this piece is above the top of the
     * Tetris screen.
     */
    public boolean hasToppedOut(){return toppedOut;}
    
    /**
     * Set the size of all the squares in a piece, essentially changing the size of the piece.
     * @param size an integer representing the number of pixels wide and tall 
     * the squares will be
     */
    public void setSquareSize(int size){
        for(int i = 0; i < squares.size(); i++){
            squares.get(i).setSize(size);
        }
    }//=========================

    /**
     * Tell this piece which player it belongs too
     * @param playerNum an integer representing the player this piece will belong
     * to.
     */
    public void setPlayerNum(int playerNum){
        playerNumber = playerNum;
        squares.clear();
        for(int i = 0; i < numSquares; i++){
            squares.add(new Square(color, new Point(gridLoc.x + positions[typeIndex][i].x,
                            gridLoc.y + positions[typeIndex][i].y), Tetris.SQUARE_SIZE, playerNumber));
        }
    }//===================================
}
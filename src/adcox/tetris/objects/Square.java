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

/**
 *
 * @author Andrew
 * @version Dec 10, 2010
 */
public class Square {
    protected Color color;
    protected Point gridLoc;
    protected Point actLoc;
    protected int size;
    protected int playerNum;

    public Square(Color aColor, Point startGridLoc, int size, int playerNum){
        color = aColor;
        gridLoc = (Point)startGridLoc.clone();
        this.size = size;
        this.playerNum = playerNum;
        if(Tetris.playerMode == Tetris.PlayerMode.one){
            actLoc = new Point(Tetris.TOP_LEFT.x + gridLoc.x * size,
                    Tetris.TOP_LEFT.y + gridLoc.y * size);
        }
        if(Tetris.playerMode == Tetris.PlayerMode.two){
            if(playerNum == 1)
                actLoc = new Point(Tetris.TOP_LEFT_P1.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P1.y + gridLoc.y * size);
            if(playerNum == 2)
                actLoc = new Point(Tetris.TOP_LEFT_P2.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P2.y + gridLoc.y * size);
        }
    }//==================================

    public Point getGridLoc(){return gridLoc;}

    public void moveVertically(boolean down){
        if(down)
            gridLoc.y += 1;
        else
            gridLoc.y -= 1;

        if(Tetris.playerMode == Tetris.PlayerMode.one){
            actLoc = new Point(Tetris.TOP_LEFT.x + gridLoc.x * size,
                    Tetris.TOP_LEFT.y + gridLoc.y * size);
        }
        if(Tetris.playerMode == Tetris.PlayerMode.two){
            if(playerNum == 1)
                actLoc = new Point(Tetris.TOP_LEFT_P1.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P1.y + gridLoc.y * size);
            if(playerNum == 2)
                actLoc = new Point(Tetris.TOP_LEFT_P2.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P2.y + gridLoc.y * size);
        }
    }//=============================

    public void moveHoriztonally(boolean right){
        if(right)
            gridLoc.x ++;
        else
            gridLoc.x --;

        if(Tetris.playerMode == Tetris.PlayerMode.one){
            actLoc = new Point(Tetris.TOP_LEFT.x + gridLoc.x * size,
                    Tetris.TOP_LEFT.y + gridLoc.y * size);
        }
        if(Tetris.playerMode == Tetris.PlayerMode.two){
            if(playerNum == 1)
                actLoc = new Point(Tetris.TOP_LEFT_P1.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P1.y + gridLoc.y * size);
            if(playerNum == 2)
                actLoc = new Point(Tetris.TOP_LEFT_P2.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P2.y + gridLoc.y * size);
        }
    }//==============================

    public Point getActLoc(){return actLoc;}
    
    public void setSize(int s){
        size = s;
        if(Tetris.playerMode == Tetris.PlayerMode.one){
            actLoc = new Point(Tetris.TOP_LEFT.x + gridLoc.x * size,
                    Tetris.TOP_LEFT.y + gridLoc.y * size);
        }
        if(Tetris.playerMode == Tetris.PlayerMode.two){
            if(playerNum == 1)
                actLoc = new Point(Tetris.TOP_LEFT_P1.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P1.y + gridLoc.y * size);
            if(playerNum == 2)
                actLoc = new Point(Tetris.TOP_LEFT_P2.x + gridLoc.x * size,
                        Tetris.TOP_LEFT_P2.y + gridLoc.y * size);
        }
    }//===========================

    @Override
    public String toString(){
        return("Square at (" + gridLoc.x + ", " + gridLoc.y + ")");
    }//==========================

    public void draw(Graphics2D g, Tetris.DisplayMode displayMode){
        if(displayMode.equals(Tetris.DisplayMode.normal)){
            g.setColor(color);
            g.fill3DRect(actLoc.x, actLoc.y, size, size, true);
        }
        if(displayMode.equals(Tetris.DisplayMode.apple)){
            g.setColor(Color.white);
            g.fill3DRect(actLoc.x, actLoc.y, size, size, true);
        }
    }//=============================
}

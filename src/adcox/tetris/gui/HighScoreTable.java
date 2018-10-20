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
package adcox.tetris.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author Andrew
 * @version Dec 17, 2010
 */
public class HighScoreTable extends JDialog{

    JTextArea scoreArea = new JTextArea();

    public HighScoreTable(){
        this.setLocationByPlatform(true);
        setModal(true);
        scoreArea.setLocation(0, 0);
        scoreArea.setSize(500, 300);
        scoreArea.setVisible(true);
        scoreArea.setEditable(false);

        ArrayList<String> lines = readFile("hs.txt");
        ArrayList<Score> scores = new ArrayList<Score>();
        for (int i = 0; i < lines.size(); i++){
            scores.add(new Score(lines.get(i)));
        }

        scores = mergeSort(scores);

        String header = String.format("%7s%20s%40s\n", "Points", "Nickname", "Quote");
        scoreArea.setText(header);

        for(int i = scores.size() - 1; i >= 0; i--){
            String aLine = String.format("%7s%20s%50s", Integer.toString(scores.get(i).points),
                    scores.get(i).name, scores.get(i).quote);
            scoreArea.append("\n" + aLine);

            //only display the top 10
            if(scores.size() - 10 >= i)
                break;
        }

        this.add(scoreArea);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(470, 220);
        this.setVisible(true);
        this.setTitle("HIGH SCORES!!!!!");
        this.setLocation(Tetris.P_WIDTH/2 - this.getSize().width/2,
                Tetris.P_HEIGHT/2 - this.getSize().height/2);
    }//====================================

    private class Score{
        public int points;
        public String name, quote;

        public Score(String lineOfText){
            String[] parts = lineOfText.split(Tetris.REGEX);

            try{
                points = Integer.parseInt(parts[0]);
                name = parts[1];
                quote = parts[2];
            }catch(Exception e){}
        }//====================
    }//=============================

    private static ArrayList<String> readFile(String filePath){
        try{
            FileReader in = new FileReader(filePath);
            BufferedReader buff = new BufferedReader(in);

            ArrayList<String> lines = new ArrayList<String>();
            String temp = new String();

            int length = 0;
            while((temp = buff.readLine()) != null){
                length++;
                lines.add(temp);
            }

            in.close();
            return lines;

        }
        catch (IOException e){
            //System.err.println("Unable to read from file" + '\n' + e.toString());
            JOptionPane.showMessageDialog(null, "Unable to open the file.\n" + e,
                    "Oops...", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }//==========================================
    private static void writeToFile(String filePath, String strToWrite){
        BufferedWriter out;

        try {
        out = new BufferedWriter(new FileWriter(filePath, true));

        out.write(strToWrite);//Write out a string to the file

        out.newLine();//write a new line to the file so the next time you write to the file it does it on the next line

        out.close();//flushes and closes the stream
        }catch(IOException e){
            //System.out.println("There was a problem:" + e);
            JOptionPane.showMessageDialog(null, "Unable to write to the file.\n" + e,
                    "Oops...", JOptionPane.ERROR_MESSAGE);
        }
    }//===========================================

    private ArrayList<Score> mergeSort(ArrayList<Score> scoresToSort){
        if(scoresToSort.size() <= 1)
            return scoresToSort;

        //initialize variables
        ArrayList<Score> left = new ArrayList<Score>();
        ArrayList<Score> right = new ArrayList<Score>();
        ArrayList<Score> result = new ArrayList<Score>();
        int middle = scoresToSort.size()/2;

        //split the list into two halves: left and right
        for(int i = 0; i < scoresToSort.size(); i++){
            if(i < middle){
                left.add(scoresToSort.get(i));
            }
            else{
                right.add(scoresToSort.get(i));
            }
        }

        left = mergeSort(left);
        right = mergeSort(right);
        result = merge(left, right);

        return result;
    }

    private ArrayList<Score> merge(ArrayList<Score> left, ArrayList<Score> right){
        ArrayList<Score> result = new ArrayList<Score>();

        //keep looping while left or right are bigger than 0 elements
        while(left.size() > 0 || right.size() > 0){

            //check to see if left AND right are bigger than 0 elements
            if(left.size() > 0 && right.size() > 0){

                //if the first element of left has fewer points than the first
                //element of right add the first element of left to result
                if(left.get(0).points <= right.get(0).points){
                    result.add(left.get(0));
                    left.remove(0);
                }
                else{
                    result.add(right.get(0));
                    right.remove(0);
                }
            }

            //left and right are NOT both bigger than 0
            //if left is bigger than 0, add it to result
            else if(left.size() > 0){
                result.addAll(left);
                break;
            }

            //if right is bigger than 0, add it to result
            else if(right.size() > 0){
                result.addAll(right);
                break;
            }
        }

        return result;
    }//=============================

}

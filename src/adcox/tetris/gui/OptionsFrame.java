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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 *
 * @author Andrew
 * @version Jun 21, 2011
 */
public class OptionsFrame extends JDialog{
    
    static final int P_WIDTH = 400;
    static final int P_HEIGHT = 250;
    
    private JLabel defaultDisplayLbl = new JLabel();
    private JLabel penaltiesOnLbl = new JLabel();
    private JLabel previewOnLbl = new JLabel();
    private JLabel startingLevelLbl = new JLabel();
    private JComboBox displayOptions = new JComboBox();
    private JComboBox startingLevelOptions = new JComboBox();
    private JRadioButton penaltiesOnOption = new JRadioButton();
    private JRadioButton penaltiesOffOption = new JRadioButton();
    private JRadioButton previewOnOption = new JRadioButton();
    private JRadioButton previewOffOption = new JRadioButton();
    private JButton saveBtn = new JButton();
    private JButton cancelBtn = new JButton();
    Tetris theGame;
    
    public OptionsFrame(Tetris boss){
        setTitle("Preferences");
        setSize(P_WIDTH, P_HEIGHT);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setFont(new Font("SANSERIF", Font.ROMAN_BASELINE, 12));
        setLocationByPlatform(true);
        setModal(true);
        
        theGame = boss;
        initComponents();
        
        loadSettings();
        
        this.setVisible(true);
    }//================================
    
    private void initComponents(){
        defaultDisplayLbl.setText("Default display mode:");
        defaultDisplayLbl.setSize(150, 30);
        defaultDisplayLbl.setLocation(20, 10);
        this.add(defaultDisplayLbl);
        
        displayOptions.setModel(new DefaultComboBoxModel(new String[] {"Classical", "Apple Sophistication"}));
        displayOptions.setSelectedIndex(1);
        displayOptions.setLocation(170, 10);
        displayOptions.setSize(200, 30);
        this.add(displayOptions);
        
        penaltiesOnLbl.setText("By default, penalties start");
        penaltiesOnLbl.setSize(200, 30);
        penaltiesOnLbl.setLocation(20, 50);
        this.add(penaltiesOnLbl);
        
        penaltiesOnOption.setText(("ON"));
        penaltiesOnOption.setSize(70, 30);
        penaltiesOnOption.setLocation(220, 50);
        penaltiesOnOption.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                penaltyOnAction(evt);
            }
        });
        this.add(penaltiesOnOption);
        
        penaltiesOffOption.setText(("OFF"));
        penaltiesOffOption.setSelected(true);
        penaltiesOffOption.setSize(70, 30);
        penaltiesOffOption.setLocation(290, 50);
        penaltiesOffOption.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                penaltyOffAction(evt);
            }
        });
        this.add(penaltiesOffOption);
        
        previewOnLbl.setText("By default, piece-preview starts");
        previewOnLbl.setSize(200, 30);
        previewOnLbl.setLocation(20, 90);
        this.add(previewOnLbl);
        
        previewOnOption.setText(("ON"));
        previewOnOption.setSize(70, 30);
        previewOnOption.setLocation(220, 90);
        previewOnOption.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                previewOnAction(evt);
            }
        });
        this.add(previewOnOption);
        
        previewOffOption.setText(("OFF"));
        previewOffOption.setSelected(true);
        previewOffOption.setSize(70, 30);
        previewOffOption.setLocation(290, 90);
        previewOffOption.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                previewOffAction(evt);
            }
        });
        this.add(previewOffOption);
        
        startingLevelLbl.setText("Staring level:");
        startingLevelLbl.setSize(100, 30);
        startingLevelLbl.setLocation(20, 130);
        this.add(startingLevelLbl);
        
        startingLevelOptions.setModel(new DefaultComboBoxModel(new String[] 
                {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        startingLevelOptions.setLocation(100, 130);
        startingLevelOptions.setSize(70, 30);
        this.add(startingLevelOptions);
        
        saveBtn.setText("Save");
        saveBtn.setSize(70, 30);
        saveBtn.setLocation(100, this.getHeight() - 70);
        saveBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                saveBtnAction(evt);
            }
        });
        this.add(saveBtn);
        
        cancelBtn.setText("Cancel");
        cancelBtn.setSize(70, 30);
        cancelBtn.setLocation(200, this.getHeight() - 70);
        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                cancelBtnAction(evt);
            }
        });
        this.add(cancelBtn);
        
    }//==================================
    
    private void penaltyOnAction(ActionEvent evt){
        penaltiesOnOption.setSelected(true);
        penaltiesOffOption.setSelected(false);
    }//==================================
    
    private void penaltyOffAction(ActionEvent evt){
        penaltiesOffOption.setSelected(true);
        penaltiesOnOption.setSelected(false);
    }//==================================
    
    private void previewOnAction(ActionEvent evt){
        previewOnOption.setSelected(true);
        previewOffOption.setSelected(false);
    }//=========================
    
    private void previewOffAction(ActionEvent evt){
        previewOffOption.setSelected(true);
        previewOnOption.setSelected(false);
    }//=========================
    
    private void loadSettings(){
        ArrayList<String> lines = readFile("settings.txt");
        if(lines == null){
            //if the file can't be read, try to write a new file over it
            saveSettings();
            return;
        }
        else{
            //parse the lines and set the settings
            try{
                String display = lines.get(0);
                boolean penaltiesOn = Boolean.parseBoolean(lines.get(1));
                boolean previewOn = Boolean.parseBoolean(lines.get(2));
                int level = Integer.parseInt(lines.get(3));
                
                if(display.equals("Classical")){
                    displayOptions.setSelectedIndex(0);
                }
                if(display.equals("Apple Sophistication")){
                    displayOptions.setSelectedIndex(1);
                }
                    
                penaltiesOnOption.setSelected(penaltiesOn);
                penaltiesOffOption.setSelected(!penaltiesOn);
                
                
                previewOnOption.setSelected(previewOn);
                previewOffOption.setSelected(!previewOn);
                
                startingLevelOptions.setSelectedIndex(level - 1);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(this, "The settings file could not be read", "Sorry!", JOptionPane.ERROR_MESSAGE);
                //try to write a new one
                saveSettings();
            }
            
        }
    }//====================================
    
    public void saveSettings(){
        try{
            deleteFile("settings.txt");
            writeToFile("settings.txt", displayOptions.getModel().getSelectedItem().toString());
            writeToFile("settings.txt", Boolean.toString(penaltiesOnOption.isSelected()));
            writeToFile("settings.txt", Boolean.toString(previewOnOption.isSelected()));
            writeToFile("settings.txt", startingLevelOptions.getModel().getSelectedItem().toString());
         
            //apply the settings to the game
            theGame.setPreview(previewOnOption.isSelected());
            theGame.setPenalties(penaltiesOnOption.isSelected());
            if(displayOptions.getModel().getSelectedItem().toString().equals("Classical")){
                theGame.setDisplayMode(Tetris.DisplayMode.normal);
            }
            if(displayOptions.getModel().getSelectedItem().toString().equals("Apple Sophistication")){
                theGame.setDisplayMode(Tetris.DisplayMode.apple);
            }
            theGame.setLevel(startingLevelOptions.getSelectedIndex());
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "The settings file could not be written", "Sorry!", JOptionPane.ERROR_MESSAGE);
        }
    }//==========================
    
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
    }//===================================

    private static void writeToFile(String filePath, String strToWrite){
        BufferedWriter out;

        try {
        out = new BufferedWriter(new FileWriter(filePath, true));

        out.write(strToWrite);//Write out a string to the file

        out.newLine();//write a new line to the file so the next time you write to the file it does it on the next line

        out.close();//flushes and closes the stream
        }catch(IOException e){
            //System.out.println("There was a problem:" + e);
            JOptionPane.showMessageDialog(null, "Unable to open the file.\n" + e,
                    "Oops...", JOptionPane.ERROR_MESSAGE);
        }
    }//===========================================
    
    private static void deleteFile(String fileName) {
        try {
            // Construct a File object for the file to be deleted.
            File target = new File(fileName);

            if (!target.exists()) {
                System.err.println("File " + fileName
                    + " not present to begin with!");
                return;
            }

            // Quick, now, delete it immediately:
            if (target.delete())
                System.err.println("** Deleted " + fileName + " **");
            else
                System.err.println("Failed to delete " + fileName);
         } catch (SecurityException e) {
            System.err.println("Unable to delete " + fileName + "("
                + e.getMessage() + ")");
        }
    }//====================================
    
    private void saveBtnAction(ActionEvent evt){
        saveSettings();
        this.dispose();
    }//=================================
    
    private void cancelBtnAction(ActionEvent evt){
        this.dispose();
    }//=================================
}

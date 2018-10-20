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


import adcox.tetris.objects.Piece;
import adcox.tetris.objects.Square;
import com.apple.eawt.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Andrew
 * @version December 24, 2009
 */
public class Tetris extends JFrame implements KeyListener{
/**
 * Objects**********************************************************************
 */
    
    //Player 1
    private Piece fallingPiece;     //the piece that is falling for player 1
    private Piece previewPiece;     //the piece used for piece preview for player 1
    private int nextPieceNum;       //the index of the next piece for player 1
    private ArrayList<Square> squares = new ArrayList<Square>();        //all the sqaures - pieces that have been dropped
    private ArrayList<Square> backgroundSquares = new ArrayList<Square>();

    //Player 2
    private Piece fallingPieceP2;
    private Piece previewPieceP2;   //the piece used for piece preview for player 2
    private int nextPieceNumP2;     //the index of the next piece for player 2
    private ArrayList<Square> squaresP2 = new ArrayList<Square>();

    //menu stuff
    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu();
        private JMenuItem exitMenuItem = new JMenuItem();
        private JMenuItem newGameItem = new JMenuItem();
        private JRadioButtonMenuItem pauseGameItem = new JRadioButtonMenuItem();
        private JMenuItem highScoreItem = new JMenuItem();
        private JMenuItem optionsMenuItem = new JMenuItem();
    private JMenu helpMenu = new JMenu();
        private JMenuItem aboutItem = new JMenuItem();
        private JMenuItem instructionsItem = new JMenuItem();
    private JMenu difficultyMenu = new JMenu();
        private JMenu setLevelMenu = new JMenu();
        private ArrayList<JRadioButtonMenuItem> levelMenuItems = new ArrayList<JRadioButtonMenuItem>();
        private JRadioButtonMenuItem penaltyItem = new JRadioButtonMenuItem();
        private JRadioButtonMenuItem previewItem = new JRadioButtonMenuItem();
        private JRadioButtonMenuItem extraPieces = new JRadioButtonMenuItem();
    private JMenu playerMenu = new JMenu();
        private JRadioButtonMenuItem onePlayerItem = new JRadioButtonMenuItem();
        private JRadioButtonMenuItem twoPlayerItem = new JRadioButtonMenuItem();
    private JMenu modeMenu = new JMenu();
        private JRadioButtonMenuItem appleItem = new JRadioButtonMenuItem();
        private JRadioButtonMenuItem normalItem = new JRadioButtonMenuItem();

    
    private BufferedImage appleLogo, tetrisImage;
    private Timer timerP1;
    private Timer timerP2;

/**
 * Values***********************************************************************
 */
    public static enum OS{Mac, Windows, Linux};
    private OS osType;

    public boolean gameOver = false;
    public boolean paused = true;
    private int numPiecesAvailable = 7;
    
    private int level = 0;
    private int linesCleared = 0;
    private int pointsP1 = 0;

    //other player
    private int linesClearedP2 = 0;
    private int pointsP2 = 0;


    
    public static enum PlayerMode{one, two};
    public static PlayerMode playerMode = PlayerMode.one;
    
    public static enum DisplayMode{normal, apple};
    public static DisplayMode displayMode = DisplayMode.apple;

    //constants for entire program:
    public static final int P_WIDTH = 1024;
    public static final int  P_HEIGHT = 768;
    public static final int SQUARE_SIZE = 32;
    public static final Point TOP_LEFT = new Point(P_WIDTH/2 - 5*SQUARE_SIZE, 25);
    public static final Point TOP_LEFT_P1 = new Point(P_WIDTH - 10*SQUARE_SIZE - 50, 25);
    public static final Point TOP_LEFT_P2 = new Point(160, 25);
    public static final String REGEX = "##";

    //pointsP1
    static final int ONE_LINE = 25;     //pointsP1 for clearing 1, 2, 3 or 4 lines
    static final int TWO_LINE = 40;
    static final int THREE_LINE = 80;
    static final int FOUR_LINE = 160;
    //number of pointsP1 added per line dropped - if the block drops 10 spots, 10 * this value are added to the score
    static final int SPOTS_DROPPED = 2;


    //values for animation purposes
    public int speed = 1000;      //how many milliseconds between frames (piece dropping)
    private boolean pieceHit = false;
    private boolean gotScore = false;
    private boolean wasDropped = false;

    //other player
    private boolean pieceHitP2 = false;
    private boolean wasDroppedP2 = false;
    private boolean gotScoreP2 = false;

    //For some reason, key pressed gets called twice when a key is pressed.
    //this boolean prevents the code inside keyPressed() from running twice
    private boolean keyPressed = false;
    private boolean keyPressedP2 = false;

    public Tetris(OS osType){
        super("Tetris");
        setSize(P_WIDTH, P_HEIGHT);
        setPreferredSize(getSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setFont(new Font("SANSERIF", Font.ROMAN_BASELINE, 12));
        this.setLocationByPlatform(true);
        this.osType = osType;
        
        timerP1 = new Timer(speed, new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                timerP1Action(evt);
            }
        });
        
        timerP2 = new Timer(speed, new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                timerP2Action(evt);
            }
        });
        
        //load the images
        try{  
            
            appleLogo = ImageIO.read(new File("128px-apple-logo.png"));
        }catch(Exception e){
            try{
                appleLogo = ImageIO.read(new URL("http://ios.pp.ua/wp-content/uploads/e1/128px-apple-logo.png"));
            }
            catch(Exception e2){
                JOptionPane.showMessageDialog(this, "The image could not be loaded. Make sure you\n"
                    + "have an image called 128px-apple-logo.png or have an internet connection\n\nDetails:\n"
                        + e.getMessage(), "Sorry!", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try{
            tetrisImage = ImageIO.read(new File("TetrisIcon.png"));
        }
        catch(Exception e3){
            System.err.println("Could not load tetris Icon.");
            e3.printStackTrace();
        }
        
        //pick a piece
        Random randy = new Random();
        nextPieceNum = randy.nextInt(7);
        
        setLevel(level);
        initMenu();
        this.addKeyListener(this);
        
        loadSettings();
        
        if(osType.equals(OS.Mac))
            setMacAttributes();        
        
        setVisible(true);
    }//======================================

    private void initBackground(){
        Color color = null;
        backgroundSquares.clear();
        Random randy = new Random();
        if(playerMode == PlayerMode.one){
            for(int x = -11; x < 21; x ++){
                for(int y = -1; y < 23; y ++){
                    int num = randy.nextInt(2);
                    //skip the Tetris-playing area to save some cpu
                    if(y == 0 && x > 0 && x < 10)
                        y = 20;
                    if(num == 0){
                        if(level < 8){
                            color = pickBackgroundColor(level);
                        }else{
                            color = pickBackgroundColor(randy.nextInt(8));
                        }
                        backgroundSquares.add(new Square(color, new Point(x, y), SQUARE_SIZE, 1));
                    }
                }
            }
        }
        if(playerMode == PlayerMode.two){
            for(int x = -7; x < 29; x ++){
                for(int y = -1; y < 23; y ++){
                    int num = randy.nextInt(2);

                    if(num == 0){
                        if(level < 8){
                            color = pickBackgroundColor(level);
                        }else{
                            color = pickBackgroundColor(randy.nextInt(8));
                        }
                        backgroundSquares.add(new Square(color, new Point(x, y), SQUARE_SIZE, 2));
                    }
                }
            }
        }
    }//===============================
    
    private void initMenu(){

        //the file menu.............................
        fileMenu.setText("File");

        exitMenuItem.setText("Exit");
        if(osType.equals(OS.Mac))
            exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.META_DOWN_MASK));
        else
            exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }

        });

        newGameItem.setText("New Game");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        newGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                newGameItemActionPerformed(evt);
            }
        });

        highScoreItem.setText("High Scores");
        highScoreItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                highScoreItemActionPerformed(evt);
            }
        });

        pauseGameItem.setText("Pause");
        pauseGameItem.setSelected(paused);
        pauseGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        pauseGameItem.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt){
               pauseGameActionPerformed(evt);
           }
        });

        optionsMenuItem.setText("Preferences");
        optionsMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                optionsMenuItemActionPerformed();
            }
        });

        fileMenu.add(newGameItem);
        fileMenu.add(pauseGameItem);
        fileMenu.add(highScoreItem);
        
        if(!osType.equals(OS.Mac)){
            fileMenu.add(optionsMenuItem);
            fileMenu.add(exitMenuItem);
        }

        //the difficulty menu...................................................
        difficultyMenu.setText("Difficulty");
        setLevelMenu.setText("Set Level");

        for(int i = 1; i < 11; i++){
            JRadioButtonMenuItem levelBtn = new JRadioButtonMenuItem();
            levelBtn.setText(Integer.toString(i));
            levelBtn.setActionCommand(Integer.toString(i));
            if(i == 1)
                levelBtn.setSelected(true);
            levelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt){
                    levelMenuItemAction(evt);
                }
            });
            setLevelMenu.add(levelBtn);
            levelMenuItems.add(levelBtn);
        }

        penaltyItem.setText("Penalties");
        penaltyItem.setSelected(false);
        if(playerMode == PlayerMode.one)
            penaltyItem.setEnabled(false);
        else
            penaltyItem.setEnabled(true);
        
        penaltyItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                penaltyItemActionPerformed(evt);
            }
        });
        
        previewItem.setText("Piece Preview");
        previewItem.setSelected(false);
        previewItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                previewItemActionPerforemd(evt);
            }
        });
        
        extraPieces.setText("Extra Pieces");
        extraPieces.setSelected(false);
        extraPieces.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                extraPiecesActionPerformed(evt);
            }
        });
        
        difficultyMenu.add(setLevelMenu);
        difficultyMenu.add(penaltyItem);
        difficultyMenu.add(previewItem);
        difficultyMenu.add(extraPieces);

        //the help menu.........................................................
        helpMenu.setText("Help");

        aboutItem.setText("About");
        aboutItem.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent evt){
               aboutItemActionPerformed();
           }
        });

        instructionsItem.setText("Instructions");
        instructionsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                instructionsItemActionPerformed(evt);
            }
        });

        if(!osType.equals(OS.Mac))
            helpMenu.add(aboutItem);
        
        helpMenu.add(instructionsItem);

        //the player options menu
        playerMenu.setText("Player");

        onePlayerItem.setText("1 Player");
        onePlayerItem.setSelected(true);
        onePlayerItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                onePlayerItemActionPerformed(evt);
            }
        });

        twoPlayerItem.setText("2 Player");
        twoPlayerItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                twoPlayerItemActionPerformed(evt);
            }
        });

        playerMenu.add(onePlayerItem);
        playerMenu.add(twoPlayerItem);
        
        //The playerMode menu
        modeMenu.setText("Display");
        
        appleItem.setText("Apple Sophistication");
        appleItem.setSelected(true);
        appleItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                appleItemActionPerformed(evt);
            }
        });
        
        normalItem.setText("Classical");
        normalItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                normalItemActionPerformed(evt);
            }
        });
        
        modeMenu.add(normalItem);
        modeMenu.add(appleItem);

        menuBar.add(fileMenu);
        menuBar.add(difficultyMenu);
        menuBar.add(playerMenu);
        menuBar.add(modeMenu);
        menuBar.add(helpMenu);
        menuBar.setVisible(true);
        menuBar.setLocation(0, 0);
        menuBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        add(menuBar);
        setJMenuBar(menuBar);
    }//========================

    private Color pickBackgroundColor(int aNum){
        Color color;
        switch(aNum){
            case 0: color = Color.pink; break;
            case 1: color = Color.green; break;
            case 2: color = Color.blue; break;
            case 3: color = Color.red; break;
            case 4: color = Color.cyan; break;
            case 5: color = Color.gray; break;
            case 6: color = Piece.purple; break;
            case 7: color = Color.yellow; break;
            default: color = Color.darkGray; break;
        }
        return color;
    }//====================================
    
    private void setMacAttributes(){
        Application app = Application.getApplication();
        app.setDockIconImage(tetrisImage); 
        app.setAboutHandler(new AboutHandler(){
            public void handleAbout(AppEvent.AboutEvent e){
                aboutItemActionPerformed();
            }
        });
        
        app.setPreferencesHandler(new PreferencesHandler(){
            public void handlePreferences(AppEvent.PreferencesEvent e){
                optionsMenuItemActionPerformed();
            }
        });
        
        //***Set Mac Visual properties
        JRootPane root = this.getRootPane();
        root.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        
        try {
            // set the look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }//============================================
    
    public void setDisplayMode(DisplayMode mode){displayMode = mode;}
    
    public void setPreview(boolean previewOn){
        previewItem.setSelected(previewOn);
    }//=====================================
    
    public void setPenalties(boolean penaltiesOn){
        penaltyItem.setSelected(penaltiesOn);
    }//=====================================


//******************************************************************************
//*********Drawing**************************************************************


    /**
     * The Graphics object created from the image object.
     */
    private BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);
    private Graphics2D buffer = (Graphics2D)image.getGraphics();

    /**
     * Buffer the graphics by calling drawScreen with our buffer, and then clearing the screen.
     */
    public void bufferDraw(Graphics g) {
        buffer.clearRect(0,0,1024,768);
        drawScreen(buffer);
        g.drawImage(image, 0, 0, null);
    }//===================================

    @Override
    public void paint(Graphics g){
        bufferDraw(g);
    }//====================================

    public void drawScreen(Graphics2D g){
        //tell menu where it is supposed to be...
        menuBar.setLocation(0, 0);

        //draw the background white
        g.setColor(Color.white);
        g.fillRect(0, 0, P_WIDTH, P_HEIGHT);

        if(displayMode.equals(DisplayMode.normal)){
            //draw background pattern
            for(int i = 0; i < backgroundSquares.size(); i++){
                backgroundSquares.get(i).draw(g, displayMode);
            }
        }
            
        //draw the score information
        drawInfo(g);

        //draw Tetris playing area(s)
        if(displayMode.equals(DisplayMode.normal)){
            g.setColor(Color.black);
        }
        if(displayMode.equals(DisplayMode.apple)) {
            g.setColor(Color.lightGray);
        }

        if(playerMode == PlayerMode.one){
            g.fill3DRect(TOP_LEFT.x, TOP_LEFT.y, 10*SQUARE_SIZE, 20*SQUARE_SIZE, false);
        }
        else if(playerMode == PlayerMode.two){
            g.fill3DRect(TOP_LEFT_P1.x, TOP_LEFT_P1.y, 10*SQUARE_SIZE, 20*SQUARE_SIZE, false);
            g.fill3DRect(TOP_LEFT_P2.x, TOP_LEFT_P2.y, 10*SQUARE_SIZE, 20*SQUARE_SIZE, false);
        }

        if(displayMode.equals(DisplayMode.apple)){
            if(playerMode == PlayerMode.one){
                drawCenteredImage(g, TOP_LEFT);
            }
            if(playerMode == PlayerMode.two){
                drawCenteredImage(g, TOP_LEFT_P1);
                drawCenteredImage(g, TOP_LEFT_P2);
            }
        }

        //draw all the squares that are sitting on the ground
        for(int i = 0; i < squares.size(); i++){
            squares.get(i).draw(g, displayMode);
        }

        //stuff to do as long as there is a piece in existance
        if(fallingPiece != null){
            //draw the piece that is falling, check to see if the user has topped out
            fallingPiece.draw(g, displayMode);

            if(fallingPiece.hasToppedOut())
                gameOver = true;

            //check to see if the falling piece has hit and then check for solid rows
            if(!pieceHit && fallingPiece != null &&  fallingPiece.hasHit()){
                pieceHit = true;
            }

            if(playerMode == PlayerMode.two){
                if(fallingPieceP2 != null){
                    fallingPieceP2.draw(g, displayMode);

                    if(fallingPieceP2.hasToppedOut())
                        gameOver = true;
                }

                for(int i = 0; i < squaresP2.size(); i++){
                    squaresP2.get(i).draw(g, displayMode);
                }

                //check to see if the falling piece has hit and then check for solid rows
                if(!pieceHitP2 && fallingPieceP2 != null &&  fallingPieceP2.hasHit()){
                    pieceHitP2 = true;
                }
            }
        }
        if(gameOver){
            Font oldFont = g.getFont();
            g.setFont(new Font("OCR A std", Font.PLAIN, 120));
            g.setColor(Color.darkGray);
            g.drawString("GAME OVER", P_WIDTH/2 - 344, P_HEIGHT/2);
            g.setFont(oldFont);
            try {
                if (!gotScore && isHighScore(pointsP1, 1)) {
                    addScoreToTable(pointsP1, 1);
                    if(playerMode != PlayerMode.two){
                        HighScoreTable hst = new HighScoreTable();
                    }
                }
                if (playerMode == PlayerMode.two && !gotScoreP2 && isHighScore(pointsP2, 2)) {
                    addScoreToTable(pointsP2, 2);
                    HighScoreTable hst = new HighScoreTable();
                }
            } catch (IOException ex) {
                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //prevent the keyboard from sending two identical key code inputs
        keyPressed = false;
        keyPressedP2 = false;
        
    }//===================================
    
    private void timerP1Action(ActionEvent evt){
        if(!gameOver && !paused){
            if(pieceHit){
                if(!wasDropped){
                    //try moving it down one last time
                    fallingPiece.setHasHit(false);
                    fallingPiece.moveVertically(squares);
                }
                if(fallingPiece.hasHit()){
                    squares.addAll(fallingPiece.getSquares());
                    checkForSolidRow();
                    fallingPiece = null;
                    pieceHit = false;
                    getNewPiece(1);
                    wasDropped = false;
                }
                else{
                    pieceHit = false;
                }
            }
            fallingPiece.moveVertically(squares);
        }
        repaint();
    }//==========================================
    
    private void timerP2Action(ActionEvent evt){
        if(fallingPieceP2 != null && !gameOver && !paused){
            if(pieceHitP2){
                if(!wasDroppedP2){
                    //try moving it down one last time
                    fallingPieceP2.setHasHit(false);
                    fallingPieceP2.moveVertically(squaresP2);
                }
                if(fallingPieceP2.hasHit()){
                    squaresP2.addAll(fallingPieceP2.getSquares());
                    checkForSolidRow();
                    fallingPieceP2 = null;
                    pieceHitP2 = false;
                    getNewPiece(2);
                    wasDroppedP2 = false;
                }
                else{
                    pieceHitP2 = false;
                }
            }
            fallingPieceP2.moveVertically(squaresP2);
        }
        repaint();
    }//==================================
    
    /**
     * This function draws the apple logo in the center of a tetris playing area
     * @param g a Graphics2D object
     * @param topLeftReference a Point referencing the top left corner of the playing area
     */
    private void drawCenteredImage(Graphics2D g, Point topLeftReference){
        //move the graphics object the the middle of the screen
        try{
            int x = topLeftReference.x + 5*SQUARE_SIZE - (int)(.5*appleLogo.getWidth());
            int y = topLeftReference.y + 10*SQUARE_SIZE - (int)(.5*appleLogo.getHeight());
            g.translate(x, y);
            //draw the apple logo
            g.drawImage(appleLogo, AffineTransform.getScaleInstance(1.0, 1.0), null);
        
            //move the graphics object back
            g.translate(-x, -y);
        }catch(Exception e){}
    }//=====================================

    private void drawInfo(Graphics2D g){
        int width_box = 3*SQUARE_SIZE;
        int height_box = 150;
        int top_box = TOP_LEFT.y + 10;
        int left_box = TOP_LEFT.x - 120;
        int left_box1 = TOP_LEFT_P1.x -120;
        int left_apple_vers = 35;
        int left_apple_vers2P = 15;
        int top_box2 = TOP_LEFT_P2.y + 10;
        int left_box2 = TOP_LEFT_P2.x - 120;
            
        if(playerMode == PlayerMode.one){
            
            if(displayMode.equals(DisplayMode.normal)){
                g.setColor(Color.lightGray);
                g.
                        fill3DRect(left_box, top_box, width_box, height_box, true);
                g.setColor(Color.black);
                g.drawString("Score:", left_box + 10, top_box + 20);
                g.drawString(Integer.toString(pointsP1), left_box + 10, top_box + 40);
                g.drawString("Level:", left_box + 10, top_box + 60);
                g.drawString(Integer.toString(level + 1), left_box + 10, top_box + 80);
                g.drawString("Lines:", left_box + 10, top_box + 100);
                g.drawString(Integer.toString(linesCleared), left_box + 10, top_box + 120);
            }
            else if(displayMode.equals(DisplayMode.apple)){
                //remember the normal font
                Font defaultFont = g.getFont();
                g.setFont(new Font("SANSERIF", Font.ROMAN_BASELINE, 40));
                //draw the labels
                g.setColor(Color.lightGray);
                g.drawString("Score:", left_apple_vers, top_box + 50);
                g.drawString("Lines:", left_apple_vers, top_box + 150);
                g.drawString("Level:", left_apple_vers, top_box + 250);
                //draw the data
                g.setColor(Color.gray);
                g.drawString(Integer.toString(pointsP1), left_apple_vers + 150, top_box + 50);
                g.drawString(Integer.toString(linesCleared), left_apple_vers + 150, top_box + 150);
                g.drawString(Integer.toString(level + 1), left_apple_vers + 150, top_box + 250);
                //reset the font
                g.setFont(defaultFont);
                //set color for drawing the information
                g.setColor(Color.darkGray);                
            }

            if(previewItem.isSelected()){
                //draw the piece preview
                int previewLeftP1 = 0, previewTop = 0;
                if(displayMode.equals(DisplayMode.apple)){
                    previewTop = TOP_LEFT.y + 10;
                    previewLeftP1 = TOP_LEFT.x + 11*SQUARE_SIZE;

                    //initialize the preview piece.  It's location is in terms of squares, which are half as large as usual for the preview piece
                    previewPiece = new Piece(nextPieceNum, new Point(24,3), squares, 1);
                    previewPiece.setSquareSize(SQUARE_SIZE/2);
                    g.setColor(Color.gray);
                }
                else if(displayMode.equals(DisplayMode.normal)){
                    previewTop = top_box + 138;
                    previewLeftP1  = left_box;

                    //initialize the preview piece.  It's location is in terms of squares, which are half as large as usual for the preview piece
                    previewPiece = new Piece(nextPieceNum, new Point(-5,12), squares, 1);
                    previewPiece.setSquareSize(SQUARE_SIZE/2);

                    g.setColor(Color.black);
                }

                g.drawString("Preview:", previewLeftP1 + 10, previewTop);
                g.fillRect(previewLeftP1, previewTop + 15, SQUARE_SIZE*3, SQUARE_SIZE*3);

                if(previewPiece != null)
                    previewPiece.draw(g, displayMode);
            }
        }
        else if(playerMode == PlayerMode.two){
            
            if(displayMode.equals(DisplayMode.normal)){
                g.setColor(Color.lightGray);
                g.fill3DRect(left_box1, top_box, width_box, height_box, true);
                g.fill3DRect(left_box2, top_box2, width_box, height_box, true);
                g.setColor(Color.black);
                g.drawString("Score:", left_box1 + 10, top_box + 20);
                g.drawString(Integer.toString(pointsP1), left_box1 + 10, top_box + 40);
                g.drawString("Level:", left_box1 + 10, top_box + 60);
                g.drawString(Integer.toString(level + 1), left_box1 + 10, top_box + 80);
                g.drawString("Lines:", left_box1 + 10, top_box + 100);
                g.drawString(Integer.toString(linesCleared), left_box1 + 10, top_box + 120);

                g.drawString("Score:", left_box2 + 10, top_box2 + 20);
                g.drawString(Integer.toString(pointsP2), left_box2 + 10, top_box2 + 40);
                g.drawString("Level:", left_box2 + 10, top_box2 + 60);
                g.drawString(Integer.toString(level + 1), left_box2 + 10, top_box2 + 80);
                g.drawString("Lines:", left_box2 + 10, top_box2 + 100);
                g.drawString(Integer.toString(linesClearedP2), left_box2 + 10, top_box2 + 120);
                
                //draw the player numbers
                Font defaultFont = g.getFont();
                g.setFont(new Font("SANSERIF", Font.ROMAN_BASELINE, 40));
                g.drawString("Player 1", TOP_LEFT_P1.x + 3*SQUARE_SIZE, TOP_LEFT_P1.y + 21*SQUARE_SIZE);
                g.drawString("Player 2", TOP_LEFT_P2.x + 3*SQUARE_SIZE, TOP_LEFT_P2.y + 21*SQUARE_SIZE);
                g.setFont(defaultFont);
            }
            if(displayMode.equals(DisplayMode.apple)){
                //remember the normal font
                Font defaultFont = g.getFont();
                g.setFont(new Font("SANSERIF", Font.ROMAN_BASELINE, 20));
                //draw the labels
                g.setColor(Color.lightGray);
                g.drawString("Score:", TOP_LEFT_P1.x - 4*SQUARE_SIZE - 20, top_box + 50);
                g.drawString("Lines:", TOP_LEFT_P1.x - 4*SQUARE_SIZE - 20, top_box + 100);
                g.drawString("Level:", TOP_LEFT_P1.x - 4*SQUARE_SIZE - 20, top_box + 150);
                g.drawString("Score:", TOP_LEFT_P2.x - 4*SQUARE_SIZE - 20, top_box + 50);
                g.drawString("Lines:", TOP_LEFT_P2.x - 4*SQUARE_SIZE - 20, top_box + 100);
                g.drawString("Level:", TOP_LEFT_P2.x - 4*SQUARE_SIZE - 20, top_box + 150);
                //draw the data
                g.setColor(Color.gray);
                g.drawString(Integer.toString(pointsP1), TOP_LEFT_P1.x - 4*SQUARE_SIZE + 45, top_box + 50);
                g.drawString(Integer.toString(linesCleared), TOP_LEFT_P1.x - 4*SQUARE_SIZE + 45, top_box + 100);
                g.drawString(Integer.toString(level + 1), TOP_LEFT_P1.x - 4*SQUARE_SIZE + 45, top_box + 150);
                g.drawString(Integer.toString(pointsP2), TOP_LEFT_P2.x - 4*SQUARE_SIZE + 45, top_box + 50);
                g.drawString(Integer.toString(linesClearedP2), TOP_LEFT_P2.x - 4*SQUARE_SIZE  + 45, top_box + 100);
                g.drawString(Integer.toString(level + 1), TOP_LEFT_P2.x - 4*SQUARE_SIZE  + 45, top_box + 150);
                
                g.setFont(new Font("SANSERIF", Font.ROMAN_BASELINE, 40));
                g.drawString("Player 1", TOP_LEFT_P1.x + 3*SQUARE_SIZE, TOP_LEFT_P1.y + 21*SQUARE_SIZE);
                g.drawString("Player 2", TOP_LEFT_P2.x + 3*SQUARE_SIZE, TOP_LEFT_P2.y + 21*SQUARE_SIZE);
                
                //reset the font
                g.setFont(defaultFont);
                //set color for drawing the information
                g.setColor(Color.darkGray);  
            }
            
            if(previewItem.isSelected()){
                //draw the piece preview
                int previewTop = 0;
                int previewLeftP1 = 0, previewLeftP2 = 0;
                if(displayMode.equals(DisplayMode.apple)){
                    previewTop = TOP_LEFT_P1.y + 200;
                    previewLeftP1 = TOP_LEFT_P1.x - 4*SQUARE_SIZE - 10;
                    previewLeftP2 = TOP_LEFT_P2.x - 4*SQUARE_SIZE - 10;

                    //initialize the preview piece.  It's location is in terms of squares, which are half as large as usual for the preview piece
                    previewPiece = new Piece(nextPieceNum, new Point(-6,15), squares, 1);
                    previewPiece.setSquareSize(SQUARE_SIZE/2);

                    previewPieceP2 = new Piece(nextPieceNumP2, new Point(-6, 15), squaresP2, 2);
                    previewPieceP2.setSquareSize(SQUARE_SIZE/2);
                    g.setColor(Color.gray);
                }
                else if(displayMode.equals(DisplayMode.normal)){
                    previewTop = top_box + 138;
                    previewLeftP1  = left_box1;
                    previewLeftP2 = left_box2;

                    //initialize the preview piece.  It's location is in terms of squares, which are half as large as usual for the preview piece
                    previewPiece = new Piece(nextPieceNum, new Point(-5,12), squares, 1);
                    previewPiece.setSquareSize(SQUARE_SIZE/2);

                    previewPieceP2 = new Piece(nextPieceNumP2, new Point(-5, 12), squaresP2, 2);
                    previewPieceP2.setSquareSize(SQUARE_SIZE/2);
                    g.setColor(Color.black);
                }

                g.drawString("Preview:", previewLeftP1 + 10, previewTop);
                g.fillRect(previewLeftP1, previewTop + 15, SQUARE_SIZE*3, SQUARE_SIZE*3);

                g.drawString("Preview:", previewLeftP2 + 10, previewTop);
                g.fillRect(previewLeftP2, previewTop + 15, SQUARE_SIZE*3, SQUARE_SIZE*3);

                if(previewPiece != null)
                    previewPiece.draw(g, displayMode);
                if(previewPieceP2 != null)
                    previewPieceP2.draw(g, displayMode);
            }
        }
    }//==================================

    private void getNewPiece(int playerNum){
        Random randy = new Random();
        int pieceNum;
        Point startLoc = new Point(4,1);
        if(playerNum == 1){
            pieceNum = nextPieceNum;
            //then, pick the next piece
            nextPieceNum = randy.nextInt(numPiecesAvailable);
            fallingPiece = new Piece(pieceNum, startLoc, squares, 1);
        }
        if(playerNum == 2){
            pieceNum = nextPieceNumP2;
            //then, pick the next piece
            nextPieceNumP2 = randy.nextInt(numPiecesAvailable);
            fallingPieceP2 = new Piece(pieceNum, startLoc, squaresP2, 2);
        }
    }//============================

    private void checkForSolidRow(){
        int[] rowCount = new int[21];

        //count the number of squares in each row
        for(int s = 0; s < squares.size(); s++){
            try{
                rowCount[squares.get(s).getGridLoc().y]++;
            }catch(Exception e){}
        }

        int oldLines = linesCleared;
        for(int r = 0; r < rowCount.length; r++){
            if(rowCount[r] == 10)
                clearRow(r, 1);
        }
        //add pointsP1 according to how many lines are cleared
        switch(linesCleared - oldLines){
            case 1: pointsP1 += (level + 1) * ONE_LINE; break;
            case 2: pointsP1 += (level + 1) * TWO_LINE; break;
            case 3: pointsP1 += (level + 1) * THREE_LINE; break;
            case 4: pointsP1 += (level + 1) * FOUR_LINE; break;
        }

        if(playerMode == PlayerMode.two){
            int[] rowCount2 = new int[21];

            //count the number of squares in each row
            for(int s = 0; s < squaresP2.size(); s++){
                try{
                    rowCount2[squaresP2.get(s).getGridLoc().y]++;
                }catch(Exception e){}
            }

            int oldLines2 = linesClearedP2;
            for(int r = 0; r < rowCount.length; r++){
                if(rowCount2[r] == 10)
                    clearRow(r, 2);
            }
            //add pointsP1 according to how many lines are cleared
            switch(linesClearedP2 - oldLines2){
                case 1: pointsP2 += (level + 1) * ONE_LINE; break;
                case 2: pointsP2 += (level + 1) * TWO_LINE; break;
                case 3: pointsP2 += (level + 1) * THREE_LINE; break;
                case 4: pointsP2 += (level + 1) * FOUR_LINE; break;
            }

            //check to see if either player can penalize the other
            if(penaltyItem.isSelected()){
                if(linesClearedP2 - oldLines2 >= 2){
                    penalizePlayer(1, (linesCleared - oldLines2 - 1));
                }
                if(linesCleared - oldLines >= 2){
                    penalizePlayer(2, (linesCleared - oldLines - 1));
                }
            }
        }
    }//===============================

    private void clearRow(int row, int playerNum){
        if(playerNum == 1){
            //first delete all the squares in that row
            for(int s = 0; s < squares.size(); s++){
                if(squares.get(s).getGridLoc().y == row){
                    squares.remove(s);
                    s--;
                }
            }
            //next shift all rows above cleared row down by one row
            for(int s = 0; s < squares.size(); s++){
                if(squares.get(s).getGridLoc().y < row)
                    squares.get(s).moveVertically(true);
            }

            linesCleared++;
            if(level < 10 && linesCleared >= 10*(level + 1)){
                level ++;
                setLevel(level);
            }
        }
        if(playerNum == 2){
            //first delete all the squares in that row
            for(int s = 0; s < squaresP2.size(); s++){
                if(squaresP2.get(s).getGridLoc().y == row){
                    squaresP2.remove(s);
                    s--;
                }
            }
            //next shift all rows above cleared row down by one row
            for(int s = 0; s < squaresP2.size(); s++){
                if(squaresP2.get(s).getGridLoc().y < row)
                    squaresP2.get(s).moveVertically(true);
            }

            linesClearedP2++;
            //when playing two player, both players have to exceed the line limit
            if(level < 10 && linesClearedP2 >= 10*(level + 1) && 
                    linesCleared >= 10*(level + 1)){
                level ++;
                setLevel(level);
            }
        }
    }//====================================

    public void setLevel(int levelNum){
        level = levelNum;
        //set the speed - Assume 1 second is 20 frames
        speed = 1000 - (levelNum*104);
        
        //set a maximum speed
        if(speed <= 0){
            speed = 1;
        }

        timerP1.setDelay(speed);
        timerP2.setDelay(speed);
        initBackground();
    }//=============================

    private void startNewGame(int levelNum){
        level = levelNum;
        pointsP1 = 0;
        linesCleared = 0;
        squares.clear();
        fallingPiece = null;
        setLevel(level);
        gameOver = false;
        gotScore = false;
        gotScoreP2 = false;
        wasDropped = false;
        wasDroppedP2 = false;
        pointsP2 = 0;
        linesClearedP2 = 0;
        squaresP2.clear();
        fallingPieceP2 = null;
        paused = false;
        timerP1.start();
        if(playerMode == PlayerMode.one)
            getNewPiece(1);
        else{
            timerP2.start();
            getNewPiece(1);
            getNewPiece(2);
        }
    }//==============================

    private void penalizePlayer(int playerToPenalize, int linesToAdd){
        if(playerToPenalize == 1){
            for(int i = 0; i < squares.size(); i++){
                for(int s = 0; s < linesToAdd; s++)
                    squares.get(i).moveVertically(false);
            }

            Random randy = new Random();
            for(int x = 0; x < 10; x++){
                for(int y = 0; y < linesToAdd; y++){
                    if(randy.nextInt(2) == 0)
                        squares.add(new Square(Color.darkGray, new Point(x, 19 - y),
                                SQUARE_SIZE, playerToPenalize));
                }
            }
        }
        if(playerToPenalize == 2){
            for(int i = 0; i < squaresP2.size(); i++){
                for(int s = 0; s < linesToAdd; s++)
                    squaresP2.get(i).moveVertically(false);
            }

            Random randy = new Random();
            for(int x = 0; x < 10; x++){
                for(int y = 0; y < linesToAdd; y++){
                    if(randy.nextInt(2) == 0)
                        squaresP2.add(new Square(Color.darkGray, new Point(x, 19 - y),
                                SQUARE_SIZE, playerToPenalize));
                }
            }
        }
    }//================================

//******************************************************************************
//*********User Inputs**********************************************************

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        //System.out.println("KeyPressed: " + code);
        if(!paused || gameOver){
            if(fallingPiece != null && !keyPressed){
                if(code == KeyEvent.VK_LEFT && !wasDropped){
                    fallingPiece.moveHorizontally(false, squares);
                    keyPressed = true;
                }
                if(code == KeyEvent.VK_RIGHT && !wasDropped){
                    fallingPiece.moveHorizontally(true, squares);
                    keyPressed = true;
                }
                if(code == KeyEvent.VK_UP && !wasDropped){
                    fallingPiece.rotate(squares);
                    keyPressed = true;
                }
                if(code == KeyEvent.VK_DOWN){
                    if(!pieceHit){
                        int numSpotsDropped = fallingPiece.drop(squares);
                        pointsP1 += (SPOTS_DROPPED * numSpotsDropped);
                        pieceHit = true;
                        wasDropped = true;
                    }
                    keyPressed = true;
                }
            }
        }
        repaint();
    }//===============================================

    @Override
    public void keyTyped(KeyEvent e){
        int code = e.getKeyChar();
        //System.out.println("KeyTyped: " + code);

        if(!paused && !gameOver){
            if(playerMode == PlayerMode.two){
                //player two plays with A, S, D and W

                if(fallingPieceP2 != null && !keyPressedP2){
                    //A
                    if(code == KeyEvent.VK_A && !wasDroppedP2){
                        fallingPieceP2.moveHorizontally(false, squaresP2);
                        keyPressedP2 = true;
                    }

                    //D
                    if(code == KeyEvent.VK_D && !wasDroppedP2){
                        fallingPieceP2.moveHorizontally(true, squaresP2);
                        keyPressedP2 = true;
                    }

                    //W
                    if(code == KeyEvent.VK_W && !wasDroppedP2){
                        fallingPieceP2.rotate(squaresP2);
                        keyPressedP2 = true;
                    }

                    //S
                    if(code == KeyEvent.VK_S){
                        if(!pieceHitP2){
                            int numSpotsDropped = fallingPieceP2.drop(squaresP2);
                            pointsP2 += (SPOTS_DROPPED * numSpotsDropped);
                            pieceHitP2 = true;
                            wasDroppedP2 = true;
                        }
                        keyPressedP2 = true;
                    }
                }
            }
        }
        repaint();
    }//================================
    
    public void keyReleased(KeyEvent e) {}

//******************************************************************************
//*********Action Events********************************************************
    
    private void exitMenuItemActionPerformed(ActionEvent evt) {System.exit(0);}

    private void newGameItemActionPerformed(ActionEvent evt) {
        int levelNum = 0;
        for(int i = 0; i < levelMenuItems.size(); i++){
            if(levelMenuItems.get(i).isSelected())
                levelNum = i;
        }
        startNewGame(levelNum);
        repaint();
    }//====================================

    private void highScoreItemActionPerformed(ActionEvent evt){
        //load up the high score table!!
        HighScoreTable scores = new HighScoreTable();
        paused = true;
    }//=================================

    private void pauseGameActionPerformed(ActionEvent evt){
        paused = !paused;
        repaint();
    }

    private void levelMenuItemAction(ActionEvent evt){
        //find the index of the button pressed
        int index = 0;
        for(int i = 0; i < levelMenuItems.size(); i++){
            if(levelMenuItems.get(i).equals(evt.getSource()))
                index = i;
            else
                levelMenuItems.get(i).setSelected(false);
        }

        startNewGame(index);
        paused = true;
        repaint();
    }//=======================================

    private void aboutItemActionPerformed(){
        paused = true;
        pauseGameItem.setSelected(true);
        String aboutMessage = "Tetris\n\nProgrammed by:"
                + "\nAndrew Cox\n\nAll Rights Reserved\n"
                + "(c) Andrew Cox, 2010\n\nEnjoy my game!! :)";
        JOptionPane.showMessageDialog(this, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
    }//=================================

    private void instructionsItemActionPerformed(ActionEvent evt){
        paused = true;
        pauseGameItem.setSelected(true);
        String instructions = "General Game Play:\n\nIn Tetris, you try to fit pieces together "
                + "at the bottom of the screen so that you fill every space all the way across\n"
                + "a row.  Once you do this, the row will vanish, and you will earn"
                + " some points.\n\nFeatures\n\n"
                + "Most of the items in the menus are self explanatory. "
                + "However there is at least one item I need to explain:\n\n"
                + "Penalties: When you are playing with two players, and you clear "
                + "more than two lines, you will 'penalize' the other\n"
                + "player. Now, the controls:\n\nPlayer 1\nUP-ARROW"
                + ": Rotates the piece\nDOWN-ARROW: Drops the piece\nLEFT-ARROW:"
                + " Moves the piece left\nRIGHT-ARROW: Moves the piece right\n"
                + "\nPlayer 2\nA: move the piece left\nD: Moves the piece right\n"
                + "W: Flips the piece\nS: Drops the piece\n\n"
                + "Good luck, and have fun!!";

        JOptionPane.showMessageDialog(this, instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }//==============================

    private void onePlayerItemActionPerformed(ActionEvent evt){
        playerMode = PlayerMode.one;
        twoPlayerItem.setSelected(false);
        //start a new game
        newGameItemActionPerformed(null);
        
        //pause the animation, delete the pieces so that it doesn't start running
        paused = true;
        fallingPiece = null;
        fallingPieceP2 = null;

        //disablePenalites
        penaltyItem.setEnabled(false);
        repaint();
    }//===================================

    private void twoPlayerItemActionPerformed(ActionEvent evt){
        playerMode = PlayerMode.two;
        onePlayerItem.setSelected(false);
        //start a new game
        newGameItemActionPerformed(null);
        
        //pause the animation, delete the pieces so that it doesn't start running
        paused = true;
        fallingPiece = null;
        fallingPieceP2 = null;

        //enable penalties
        penaltyItem.setEnabled(true);
        repaint();
    }//===================================

    private void penaltyItemActionPerformed(ActionEvent evt){repaint();}
    
    private void previewItemActionPerforemd(ActionEvent evt){repaint();}

    private void optionsMenuItemActionPerformed(){
        paused = true;
        new OptionsFrame(this);
    }//=========================================
    
    private void appleItemActionPerformed(ActionEvent evt){
        normalItem.setSelected(!appleItem.isSelected());
        
        if(appleItem.isSelected())
            displayMode = DisplayMode.apple;
        
        repaint();
    }//==================================
    
    private void normalItemActionPerformed(ActionEvent evt){
        appleItem.setSelected(!normalItem.isSelected());
        
        if(normalItem.isSelected())
            displayMode = DisplayMode.normal;
        
        repaint();
    }//=================================
    
    private void extraPiecesActionPerformed(ActionEvent evt){
        if(extraPieces.isSelected())
            numPiecesAvailable = 10;
        else
            numPiecesAvailable = 7;
        
        repaint();
    }//====================================
    
    
//******************************************************************************
//*********Utilities************************************************************
    
    
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

    private boolean isHighScore(int score, int playerNum) throws IOException{
        //I at least TRIED to get the score...
        if(playerNum == 1)
            gotScore = true;
        if(playerNum == 2)
            gotScoreP2 = true;

        //open the high score files
        ArrayList<String> scoreStrings = readFile("hs.txt");
        //initialize a list of integeres to store the scores
        ArrayList<Integer> scoreNums = new ArrayList<Integer>();

        //try to convert strings into integers
        for(int i = 0; i < scoreStrings.size(); i++){
            String[] line = scoreStrings.get(i).split(REGEX);
            try{
                int scoreVal = Integer.parseInt(line[0]);
                scoreNums.add(scoreVal);
            }
            catch(Exception e){}
        }

        //sort the scores
        scoreNums = mergeSort(scoreNums);
        
        //if there aren't 10 entries yet, the score makes the list
        //if there are 10, check to see if the score is higher than the 10th entry
        if(scoreNums.size() < 10)
            return true;
        else{
            if(score > scoreNums.get(scoreNums.size() - 10)){
                return true;
            }
            else{
                return false;
            }
        }

    }//==============================

    private void addScoreToTable(int score, int playerNum) throws IOException{
        String scoreLine = "";
        try{
            String nickName, quote;
            do{
                nickName = JOptionPane.showInputDialog(this, "Player " + playerNum +
                        " - What is your super cool nickname? (Fewer than 20 "
                        + "characters please :))", "Your nick-name?", JOptionPane.INFORMATION_MESSAGE);
                if(nickName.length() > 20){
                    JOptionPane.showMessageDialog(this, "Please enter fewer characters.", "Oops!", JOptionPane.ERROR_MESSAGE);
                }
            }while(nickName == null || nickName.length() > 20);
            do{
                quote = JOptionPane.showInputDialog(this, "Player " + playerNum +
                        " - What is your super cool quote? (Fewer than 50 characters"
                        + " please :))", "Your quote?", JOptionPane.INFORMATION_MESSAGE);
                if(quote.length() > 50){
                    JOptionPane.showMessageDialog(this, "Please enter fewer characters.", "Oops!", JOptionPane.ERROR_MESSAGE);
                }
            }while(quote == null || quote.length() > 50);

            scoreLine = score + REGEX + nickName + REGEX + quote;
        }catch(Exception e){}
        //System.out.println(message);
        writeToFile("hs.txt", scoreLine);

    }//========================
    
    private void loadSettings(){
        ArrayList<String> lines = readFile("settings.txt");

        //parse the lines and set the settings
        try{
            String display = lines.get(0);
            boolean penaltiesOn = Boolean.parseBoolean(lines.get(1));
            boolean previewOn = Boolean.parseBoolean(lines.get(2));
            int levelNum = Integer.parseInt(lines.get(3));

            if(display.equals("Classical")){
                setDisplayMode(Tetris.DisplayMode.normal);
            }
            if(display.equals("Apple Sophistication")){
                setDisplayMode(Tetris.DisplayMode.apple);
            }

            setPenalties(penaltiesOn);
            setPreview(previewOn);
            setLevel(levelNum - 1);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "The settings file could not be read", "Sorry!", JOptionPane.ERROR_MESSAGE);

            //save the settings so that the message doesn't appear in the future
            OptionsFrame o = new OptionsFrame(this);
            //o.setVisible(false);
            //o.saveSettings();
            //o.dispose();
        }
        
    }//====================================
    
    private ArrayList<Integer> mergeSort(ArrayList<Integer> scoresToSort){
        if(scoresToSort.size() <= 1)
            return scoresToSort;

        //initialize variables
        ArrayList<Integer> left = new ArrayList<Integer>();
        ArrayList<Integer> right = new ArrayList<Integer>();
        ArrayList<Integer> result = new ArrayList<Integer>();
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
    }//==========================================

    private ArrayList<Integer> merge(ArrayList<Integer> left, ArrayList<Integer> right){
        ArrayList<Integer> result = new ArrayList<Integer>();

        //keep looping while left or right are bigger than 0 elements
        while(left.size() > 0 || right.size() > 0){

            //check to see if left AND right are bigger than 0 elements
            if(left.size() > 0 && right.size() > 0){

                //if the first element of left has fewer pointsP1 than the first
                //element of right add the first element of left to result
                if(left.get(0) <= right.get(0)){
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
    }//=====================================

}//***END OF TETRIS***

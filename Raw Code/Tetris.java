package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.*;

import static java.lang.System.out;
import static javafx.scene.paint.Color.rgb;

public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    public final Point[][][] Tetraminos = {
            // I-Piece
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3)}
            },

            // J-Piece
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
            },

            // L-Piece
            {
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                    {new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 0)}
            },

            // O-Piece
            {
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
            },

            // S-Piece
            {
                    {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
            },

            // T-Piece
            {
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
            },

            // Z-Piece
            {
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
            }
    };

    private final Color[] tetraminoColors = {
            Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

    private static JFrame f = new JFrame("Tetris Trainer");


    private long score;
    private Color[][] well;
    private AI ai = new AI();
    private int first = 0;
    private static Boolean playGame = true;
    private static Tetris game = new Tetris();

    private static KeyListener keys = new KeyListener() {
        public void keyPressed(KeyEvent e) {
            if (playGame) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_Z:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_X:
                        game.rotate(+1);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.move(+1);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.dropDown();
                        game.score += 1;
                        break;
                    case KeyEvent.VK_SPACE:
                        game.strongDown();
                        break;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                playGame = true;
            }

        }
        public void keyTyped(KeyEvent e) {
        }


        public void keyReleased(KeyEvent e) {
        }
    };


    private static JButton playAgainButton = new JButton("Press \"Enter\" \n to play again");
    private static Font playAgainFont = new Font("Tahoma",Font.PLAIN,18);


    // Creates a border around the well and initializes the dropping piece
    private void init() {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(20 * 26, 26 * 23 + 25);
        f.getContentPane().setBackground(Color.gray);

        f.setVisible(true);


        playAgainButton.setBounds(5 * 26, 9 * 23 + 25, 10 * 26, 4 * 23);
        playAgainButton.setBackground(Color.white);

        playAgainButton.setFont(playAgainFont);


        well = new Color[19][24];

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i >= 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }

        newPiece();
    }

    // Put a new, random piece into the dropping position
    public void newPiece() {
        pieceOrigin = new Point(5, 0);
        rotation = 0;
        if (nextPieces.isEmpty()) {
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);

        if (currentPiece == 5) {
            pieceOrigin.y++;
        }
        nextPieces.remove(0);
        nextPieces.add((int) (Math.random() * 6));

        ai.setInfo(well, currentPiece, rotation);

        first++;

    }

    public void drawNext(Graphics g) {

        for (int i = 0; i < 3; i++) {
            g.setColor(tetraminoColors[nextPieces.get(i)]);
            for (Point p : Tetraminos[nextPieces.get(i)][0]) {
                g.fillRect((p.x + 13) * 26,
                        (p.y + 5 + i * 4) * 26,
                        25, 25);
            }
        }

    }


    // Collision test for the dropping piece
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK && (well[p.x + x][p.y + y] != Color.WHITE)) {

                return true;
            }
        }


        return false;
    }

    // Rotate the piece clockwise or counterclockwise
    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {

            rotation = newRotation;
            ai.rotation = rotation;
        }


        repaint();
    }

    // Move the piece left or right
    public void move(int i) {
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }


        repaint();
    }

    // Drops the piece one line or fixes it to the well if it can't drop
    public void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            fixToWell();
        }

        repaint();

    }
    //Drops the piece all the way down to the bottom, then fixes it to the well
    public void strongDown() {
        for (int i = 0; ; i++) {
            if (!collidesAt(pieceOrigin.x, pieceOrigin.y + i, rotation)) {
                score += 2;
                continue;
            } else {
                pieceOrigin.y += i - 1;
                fixToWell();
                break;
            }
        }

        repaint();
    }


    // Make the dropping piece part of the well, so it is available for
    // collision detection.
    public void fixToWell() {

        if (pieceOrigin.y <= 1) {
            playGame = false;
            while (!playGame) { //repeats until "Enter" key is pressed
                playAgainButton.setVisible(true);
                f.setVisible(true);
            }
            playAgainButton.setVisible(false);
            clearBoard();
            main(new String[]{"one"});
        } else {


            Color accColor = Color.blue;
            if (first % 2 == 1) {

                double ranking = ai.evaluate(pieceOrigin.x, pieceOrigin.y);
                accColor = showAccuracy(ranking);
            }

            first++;


            for (Point p : Tetraminos[currentPiece][rotation]) {
                well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = accColor;
            }


            clearRows();

            newPiece();
        }
    }

    public Color showAccuracy(double ranking) {
        double green = 255 * ranking;
        return new Color((int) (255 - green), (int) green, 0);
    }


    private void deleteRow(int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j + 1] = well[i][j];
            }
        }

    }

    public void clearBoard() {
        for (int j = 21; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j] = Color.black;
            }
        }
        nextPieces.clear();
        repaint();
        newPiece();
    }

    // Clear completed rows from the field and award score according to
    // the number of simultaneously cleared rows.
    public void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.white && currentPiece != 0) {
                    well[i][j] = Color.black;
                }
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }

            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    // Draw the falling piece
    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }


    }

    @Override
    public void paintComponent(Graphics g) {
        // Paint the well
        g.fillRect(0, 0, 26 * 12, 26 * 23);
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(this.well[i][j]);
                if (this.well[i][j] == Color.white && currentPiece != 0) {
                    g.setColor(Color.black);
                }
                g.fillRect(26 * i, 26 * j, 25, 25);
            }
        }


        // Display the score
        g.setColor(Color.YELLOW);
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);
        g.drawString("Score: " + score, 30 * 12, getHeight()-50);

        // Draw the currently falling piece

        drawPiece(g);
        drawNext(g);


    }


    public static void main(String[] args) {



        //initializing a new game
        game = new Tetris();
        game.init();
        if (args.length == 0) {
            f.add(playAgainButton);
            playAgainButton.setVisible(false);
        }

        f.add(game);
        if (args.length == 0) {

            f.addKeyListener(keys);




            // Make the falling piece drop every second
            new Thread() {
                @Override

                public void run() {
                    while (playGame) {
                        try {
                            Thread.sleep(1000-(game.score/75));
                            game.dropDown();
                            f.setVisible(true);

                        } catch (InterruptedException e) {

                        }
                    }
                }
            }.start();
        }
    }
}

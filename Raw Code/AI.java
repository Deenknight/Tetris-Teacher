package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.System.identityHashCode;
import static java.lang.System.out;

public class AI {

    private final Point[][][] Tetraminos = {
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


    private Color[][] well;
    private Color[][][] testWell = new Color[10000][][];
    private int currentPiece;
    public int rotation;
    private int endPoint;
    private int[] height = new int[10];
    private ArrayList<Integer> possible = new ArrayList<>();
    private ArrayList<Integer> column = new ArrayList<>();
    private ArrayList<Integer> direction = new ArrayList<>();
    private ArrayList<Integer> spin = new ArrayList<>();
    private int[][] offset = new int[][]
            {      // I,  J,  L,  O,  S,  T,  Z,
                    {-1, -1, -2, -1, -1, -1, -1}, //basic
                    {-2, -2, -2, -1, -2, -2, -2}, //90
                    {-1, -2, -1, -1, -1, -1, -1}, //upside down
                    {-2, -2, -2, -1, -2, -2, -2}  //270
            };

    private int[][] size = new int[][]
            {      //I, J, L, O, S, T, Z,
                    {4, 3, 3, 2, 3, 3, 3}, //basic
                    {1, 3, 2, 2, 2, 2, 2}, //90
                    {4, 3, 3, 2, 3, 3, 3}, //upside down
                    {1, 2, 3, 2, 2, 2, 2}  //270
            };

    public AI() {
        this.well = new Color[19][24];

    }


    //obtains the information required for the ai to function
    public void setInfo(Color[][] well, int currentPiece, int rotation) {

        this.well = well;
        this.currentPiece = currentPiece;
        this.rotation = rotation;

        calculate();

    }

    // displays the computer's view of the screen
    // only for testing purposes
    private void display(int testing) {
        out.println("test well: " + testing);
        out.println("x: " + column.get(testing) + " y: " + possible.get(testing) + " r: " + spin.get(testing));
        for (int j = 0; j < 23; j++) {

            for (int i = 0; i < 12; i++) {
                if (i == 0 || i == 11 || j == 0 || j == 22) {
                    out.print("//");

                } else {
                    if (testWell[testing][i][j] == Color.black) {
                        out.print("  ");
                    } else {
                        out.print("[]");
                        if (testWell[testing][i][j] == Color.white) {
                            testWell[testing + 1][i][j] = Color.black;
                        }
                    }
                }
            }

            out.println();


        }

    }

    private void calculate() {
        possible.clear();
        column.clear();
        direction.clear();
        spin.clear();


        findLowest();
        findDirection();
        makeTestWells();

    }

    public double evaluate(int x, int y) {
        ArrayList<Double> score = new ArrayList<>();


        int location = -1;

        for (int i = 0; i < possible.size(); i++) {
            if (spin.get(i) == rotation && location == -1 && column.get(i) == x && possible.get(i) + offset[spin.get(i)][currentPiece] == y && spin.get(i) == rotation) {
                location = i;
            }
            score.add(getScore(i));
        }
        if (location != -1) {
            return getRanking(location, score);
        } else {
            return 0.5;
        }
    }


    //finds all the lowest points that a piece could land at
    private void findLowest() {

        for (int testRotate = 0; testRotate < 4; testRotate++) {
            for (int j = 22; j > 1; j--) {

                for (int i = 1; i < 11; i++) {
                    if (well[i][j] == Color.black) {
                        try {
                            if (well[i][j - offset[testRotate][currentPiece]] != Color.black) {

                                possible.add(j);
                                column.add(i);
                                spin.add(testRotate);

                            }

                        } catch (Exception e) { //in case the point goes off the screen, this prevents the game from breaking
                            break;
                        }
                    }
                }
            }
        }
    }

    //finds out if there is room beside the landing place for the piece
    private void findDirection() {

        endPoint = possible.size();

        for (int i = 0; i < endPoint; i++) {

            int counter = 0;
            for (Point p : Tetraminos[currentPiece][spin.get(i)]) {

                if (well[column.get(i) + p.x][possible.get(i) + p.y + offset[spin.get(i)][currentPiece]] != Color.black) {

                    direction.add(-1);
                    break;

                } else {
                    counter++;
                }
                if (counter == 4) {

                    direction.add(0);
                }
            }
        }
        for (int testRotate = 0; testRotate < 4; testRotate++) {
            for (int s = 1; s < size[testRotate][currentPiece] + 1; s++) {
                for (int i = 0; i < endPoint; i++) {
                    findNewLow(i, s, testRotate);

                }
            }
        }


    }

    private void findNewLow(int value, int disp, int testRotate) {
        for (int j = possible.get(value); j < 22; j++) {
            int counter = 0;
            for (Point p : Tetraminos[currentPiece][testRotate]) {
                try {


                    if (well[column.get(value) + p.x - disp][j + p.y + offset[testRotate][currentPiece]] != Color.black) {
                        break;

                    } else {

                        counter++;


                    }

                } catch (Exception e) {

                    break;
                }

                if (counter == 4) {
                    possible.add(j);
                    column.add(column.get(value) - disp);
                    spin.add(testRotate);
                    direction.add(0);
                }
            }
        }

    }

    private Color[][] getNewWell() {

        Color[][] newWell = new Color[19][24];
        for (int i = 0; i < 19; i++) {
            System.arraycopy(well[i], 0, newWell[i], 0, 23);
        }
        return newWell;
    }

    private void makeTestWells() {
        testWell = new Color[10000][30][30];
        Color[][] obj;


        for (int i = 0; i < possible.size(); i++) {
            obj = getNewWell();

            if (direction.get(i) != -1) {
                testWell[i] = obj;
                for (Point p : Tetraminos[currentPiece][spin.get(i)]) {
                    testWell[i]
                            [column.get(i) + p.x - direction.get(i)]
                            [possible.get(i) + p.y + offset[spin.get(i)][currentPiece]] = Color.white;
                }
            } else {

                possible.remove(i);
                direction.remove(i);
                column.remove(i);
                spin.remove(i);
                i--;
            }
        }


    }

    private double clearedLinesScore(int clearedLines, int currentHeight) {
        double min = 0.7852149234143508;
        double max = 0.9775599613545116;

        double weight = min + ((currentHeight / 23.0) * (max - min));

        return clearedLines * weight;
    }

    //getting the accuracy of the move
    private double getScore(int tested) {

        int[] info = getLinesInfo(tested);
        int clearedLines = info[0];
        int holes = info[1];
        int currentHeight = info[2];
        int rowsWithHoles = info[3];

        double holesWeight = 0.8927187106481858;
        double bumpinessWeight = 0.4078425751240049;
        double rowsWeight = 0.8908763354631786;

        int bumpiness = 0;
        findBumpiness(tested);
        for (int i = 1; i < height.length; i++) {
            bumpiness += Math.abs(height[i] - height[i - 1]);
        }


        return clearedLinesScore(clearedLines, currentHeight)
                - holes * holesWeight
                - bumpiness * bumpinessWeight
                - rowsWithHoles * rowsWeight;
    }

    private void deleteRow(int row, int tested) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                testWell[tested][i][j + 1] = testWell[tested][i][j];
            }
        }

    }

    private int[] getLinesInfo(int tested) {
        int score = 0;
        int gaps = 0;
        int top = 0;
        int rowsWithHoles = 0;
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;

            for (int i = 1; i < 11; i++) {

                if (testWell[tested][i][j] == Color.BLACK) {
                    gap = true;
                    if (testWell[tested][i][j - 1] != Color.black) {
                        gaps++;
                    }

                } else {
                    top = j;
                }

            }
            if (!gap) {
                deleteRow(j, tested);
                j += 1;
                numClears += 1;
            } else {
                rowsWithHoles++;
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

        return new int[]{score, gaps, top, rowsWithHoles};
    }


    private void findBumpiness(int tested) {


        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 23; j++) {
                if (testWell[tested][i][j] != Color.black) {
                    height[i - 1] = j;
                    break;
                }
            }
        }


    }

    private double getRanking(int location, ArrayList<Double> score) {
        double playerScore = score.get(location);
        double playerPosition = 0;


        Collections.sort(score);
        for (int i = score.size() - 1; i > -1; i--) {
            try {
                if (score.get(i).equals(score.get(i - 1))) {
                    score.remove(i);

                }
            } catch (Exception ignored) {

            }
        }

        for (int i = score.size() - 1; i > -1; i--) {
            if (score.get(i) == playerScore) {
                playerPosition = i;
                break;
            }


        }

        return playerPosition / score.size();
    }

}




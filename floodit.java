import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//represents a single cell in the game
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;
  String color;
  boolean isFlooded;
  Posn posn;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  ArrayList<String> colors;

  // general constructor
  Cell(int x, int y, boolean isFlooded, int numColors) {
    this.x = x;
    this.y = y;
    colorPallette();
    // randomizes the colors
    int random = (int) (Math.random() * numColors);
    this.color = colors.get(random);
    this.isFlooded = isFlooded;
    this.posn = new Posn(this.x, this.y);
  }

  // convenience constructor
  Cell(int x, int y, String color, boolean isFlooded, Cell left, Cell top, Cell right,
      Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.isFlooded = isFlooded;
    this.posn = new Posn(this.x, this.y);
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // draws each cell according to the color inputed
  WorldImage drawCell() {
    if (this.color.equals("BLACK")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLACK);
    }
    else if (this.color.equals("WHITE")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.WHITE);
    }
    else if (this.color.equals("MAGENTA")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.MAGENTA);
    }
    else if (this.color.equals("BLUE")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLUE);
    }
    else if (this.color.equals("GREEN")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.GREEN);
    }
    else if (this.color.equals("YELLOW")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.YELLOW);
    }
    else if (this.color.equals("ORANGE")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.ORANGE);
    }
    else {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.PINK);
    }
  }

  // list of all possible colors of board
  void colorPallette() {
    colors = new ArrayList<String>();
    colors.add("BLACK");
    colors.add("WHITE");
    colors.add("MAGENTA");
    colors.add("BLUE");
    colors.add("GREEN");
    colors.add("YELLOW");
    colors.add("ORANGE");
    colors.add("PINK");
  }

  // sets color of current cell to given color
  void setColor(String color) {
    this.color = color;
  }

  // creates waterfall effect by changing flooded colors to the new color
  void waterfall(String color) {
    if (this.left != null && !this.left.isFlooded && this.left.color.equals(color)) {
      this.left.isFlooded = true;
    }
    if (this.top != null && !this.top.isFlooded && this.top.color.equals(color)) {
      this.top.isFlooded = true;
    }
    if (this.right != null && !this.right.isFlooded && this.right.color.equals(color)) {
      this.right.isFlooded = true;
    }
    if (this.bottom != null && !this.bottom.isFlooded && this.bottom.color.equals(color)) {
      this.bottom.isFlooded = true;
    }
  }

}

// represents the actual flooding of cells in the world
class Flood extends World {
  ArrayList<Cell> cells;

  // ***** CHANGE GRIDSIZE HERE! ******
  static final int gridsize = 20;
  // **********************************

  int howManyColors = 4;
  int boardsize = 20;
  int allowedClicks;
  int currentClicks = 0;
  int currentTime = 0;

  // main constructor
  Flood(int boardsize, int howManyColors) {
    this.boardsize = boardsize;
    this.howManyColors = howManyColors;
    makePosn(boardsize);

    // formula for allowed clicks
    if (boardsize > 12) {
      allowedClicks = boardsize + howManyColors + 15;
    }
    else if (boardsize < 4) {
      allowedClicks = boardsize + howManyColors - 2;
    }
    else {
      allowedClicks = boardsize + howManyColors - 1;
    }

  }

  // convenience constructor
  Flood() {

    this.howManyColors = 6;
    this.allowedClicks = 7;
    this.boardsize = 3;
  }

  // creates a position for each cell, randomizes colors on board
  // ** from old version, combines makeposn and makeneighbor to one method **
  void makePosn(int bdsize) {
    cells = new ArrayList<Cell>();
    for (int i = 0; i < bdsize; i++) {
      for (int j = 0; j < bdsize; j++) {
        if (i == 0 && j == 0) {
          cells.add(new Cell(0, 0, true, this.howManyColors));
        }
        else {
          cells.add(new Cell(i, j, false, this.howManyColors));
        }
      }
    }
    for (int u = 0; u < cells.size(); u++) {
      Cell updatedCell = cells.get(u);
      if (cells.get(u).x == 0) {
        updatedCell.left = null;
      }
      else {
        updatedCell.left = cells.get(u - bdsize);
      }
      if (cells.get(u).x == bdsize - 1) {
        updatedCell.right = null;
      }
      else {
        updatedCell.right = cells.get(u + bdsize);
      }
      if (cells.get(u).y == 0) {
        updatedCell.top = null;
      }
      else {
        updatedCell.top = cells.get(u - 1);
      }
      if (cells.get(u).y == bdsize - 1) {
        updatedCell.bottom = null;
      }
      else {
        updatedCell.bottom = cells.get(u + 1);
      }
    }
  }

  // on mouse methods

  // returns the cell which was clicked
  public Cell clickedCell(Posn pos) {
    Cell current = null;
    for (int i = 0; i < cells.size(); i++) {
      if ((cells.get(i).x <= ((pos.x - 70) / 20)) && (((pos.x - 70) / 20) <= cells.get(i).x)
          && (cells.get(i).y <= ((pos.y - 70) / 20)) && (((pos.y - 70) / 20) <= cells.get(i).y)) {
        current = cells.get(i);
      }
    }
    return current;
  }

  // changes first cell
  public void firstCell(Cell cell) {
    if (cell != null) {
      Cell changeFirst = cells.get(0);
      changeFirst.color = cell.color;
      cells.set(0, changeFirst);
    }
  }

  // on mouse changes board
  public void onMouseClicked(Posn pos) {
    if ((pos.x < 70 || pos.x > (boardsize * 20 + 70))
        || (pos.y < 70 || pos.y > (boardsize * 20 + 70))) {
    }
    else {
      this.firstCell(this.clickedCell(pos));
      currentClicks++;
    }
  }

  // make scene methods

  public WorldScene makeScene() {
    WorldScene finalScene = new WorldScene(1000, 700);

    // current clicks display
//    finalScene.placeImageXY(
//        new TextImage(Integer.toString(currentClicks), 30, FontStyle.BOLD, Color.BLUE), 500, 600);
    // allowed clicks display
    finalScene.placeImageXY(
        new TextImage("REMAINING CLICKS: " + Integer.toString(allowedClicks - currentClicks), 30, FontStyle.BOLD, Color.BLUE),
        550, 600);
    // flood it title
    finalScene.placeImageXY(new TextImage("FLOOD IT!", 50, FontStyle.BOLD, Color.MAGENTA), 550,
        750);
    // outline of board
    WorldImage outline = new RectangleImage((boardsize + 1) * 20, (boardsize + 1) * 20,
        OutlineMode.SOLID, Color.GRAY);
    finalScene.placeImageXY(outline, ((boardsize + 2) * 10) + 50, ((boardsize + 2) * 10) + 50);
    // time counter (**extra credit**)
    finalScene.placeImageXY(
        new TextImage("Time: " + currentTime / 10 + "sec", 35, FontStyle.BOLD_ITALIC, Color.CYAN),
        550, 650);
    // win conditions
    if (currentClicks >= allowedClicks && (!flooded())) {
      finalScene.placeImageXY(new TextImage("YOU LOSE WITH " + currentClicks + " CLICKS", 25, Color.RED), 550, 570);
    }
    else if (currentClicks <= allowedClicks && flooded()) {
      finalScene.placeImageXY(new TextImage("YOU WIN WITH " + currentClicks + " CLICKS", 25, Color.GREEN), 550, 570);
    }
    for (int i = 0; i < cells.size(); i++) {
      finalScene.placeImageXY(cells.get(i).drawCell(), 80 + 20 * cells.get(i).x,
          80 + 20 * cells.get(i).y);
    }
    return finalScene;
  }

  // uses info to update the cells in the scene
  public void createCells() {
    Cell origin = this.cells.get(0);
    String destination = origin.color;
    for (int i = 0; i < cells.size(); i++) {
      Cell cell = cells.get(i);
      if (cell.isFlooded) {
        cell.setColor(destination);
        cell.waterfall(destination);
      }
      makeScene();
    }
  }

  // checks if cells are all flooded or not
  boolean flooded() {
    boolean floodOrNot = true;
    for (int i = 0; i < cells.size(); i++) {
      floodOrNot = floodOrNot && cells.get(i).isFlooded;
    }
    return floodOrNot;
  }

  // on tick methods

  // updates time
  public void onTick() {
    currentTime++;
    createCells();

  }

  // on key event methods

  // effect of "R" key
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.cells = new ArrayList<Cell>();
      currentClicks = 0;
      makePosn(boardsize);
    }
  }

  // game methods

  // creates game for player
  public void game(int bdsize, int numColors) {
    if (numColors > 8 || numColors < 1) {
      throw new IllegalArgumentException("Number of colors cannot exceed 8");
    }
    boardsize = bdsize;
    numColors = howManyColors;
    Flood flood = new Flood(gridsize, howManyColors);
    flood.bigBang(1000, 800, 0.1);
  }

}

// represents examples of the game
class ExamplesFlood {

  Cell black;
  Cell white;
  Cell magenta;
  Cell blue;
  Cell green;
  Cell yellow;
  Cell orange;
  Cell pink;

  ArrayList<Cell> mtBoard;
  ArrayList<String> colorPallette;
  Flood w1;
  Flood w2;

  ArrayList<Cell> testList;

  // init data for world
  void initData() {

    // sample 3x3 grid
    black = new Cell(0, 0, "BLACK", true, null, null, null, null);
    white = new Cell(1, 0, "WHITE", false, black, null, null, null);
    magenta = new Cell(0, 1, "MAGENTA", false, null, black, null, null);
    blue = new Cell(1, 1, "BLUE", false, magenta, white, null, null);
    green = new Cell(0, 2, "GREEN", false, null, magenta, null, null);
    yellow = new Cell(1, 2, "YELLOW", false, green, blue, null, null);
    orange = new Cell(0, 3, "PINK", false, null, green, null, null);
    pink = new Cell(1, 3, "PINK", false, orange, yellow, null, null);

    // assigning directions to colors
    blue.right = white;
    black.bottom = magenta;
    white.bottom = blue;
    magenta.right = blue;
    magenta.bottom = green;
    blue.bottom = yellow;
    green.bottom = orange;
    green.right = black;
    yellow.bottom = pink;
    orange.right = pink;

    // color palette initializing
    colorPallette = new ArrayList<String>();
    colorPallette.add("BLACK");
    colorPallette.add("WHITE");
    colorPallette.add("MAGENTA");
    colorPallette.add("BLUE");
    colorPallette.add("GREEN");
    colorPallette.add("YELLOW");
    colorPallette.add("ORANGE");
    colorPallette.add("PINK");

    // initializing board
    ArrayList<Cell> exBoard = new ArrayList<Cell>();
    exBoard.add(black);
    exBoard.add(white);
    exBoard.add(magenta);
    exBoard.add(blue);
    exBoard.add(green);
    exBoard.add(yellow);
    exBoard.add(orange);
    exBoard.add(pink);

    // initializing flood it world examples
    w1 = new Flood();
    w2 = new Flood();

    w1.makePosn(w1.boardsize);

    // test board
    mtBoard = w1.cells;
    w2.cells = new ArrayList<Cell>();
    w2.cells.add(black);
    w2.cells.add(white);
    w2.cells.add(magenta);
    w2.cells.add(blue);
  }

  // tests draw cell
  void testDrawCell(Tester t) {
    initData();
    t.checkExpect(this.black.drawCell(),
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLACK));
  }

  // ***** TEST GAME HERE! *****
  void testGame(Tester t) {
    Flood flood = new Flood(20, 6);
    flood.game(20, 6);
  }
  // ****************************
}
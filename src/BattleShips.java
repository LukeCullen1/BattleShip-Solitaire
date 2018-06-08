import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class BattleShips {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please enter a file to read and a number of attempts allowed");
            return;
        }
        try {
            String file = args[0];
            int attempts = Integer.parseInt(args[1]);
            ArrayList<Integer> ships = new ArrayList<>();
            ArrayList<Integer> Xhits = new ArrayList<>();
            ArrayList<Integer> Yhits = new ArrayList<>();
            populateLists(file, attempts, ships, Xhits, Yhits);
            int gameSize = Xhits.size();
            //initialise a game board using 2d array
            int[][] gameBoard = new int[Xhits.size()][Xhits.size()];
            int errors = 0;
            //populate the game board with generation 1
            initialPopulation(ships, gameBoard, Xhits, Yhits, ships);
            errors = countErrors(gameBoard, Xhits, Yhits);
            for(int i = 0; i < attempts; i++) {
                gameBoard = improveGuess(gameBoard, ships, Xhits, Yhits, errors);
                errors = countErrors(gameBoard, Xhits, Yhits);
            }



        } catch (Exception e) {
            System.out.println(e);
        }

    }
    //fill the lists up with the x and y hit data and the ship values
    public static void populateLists(String file, int attempts, ArrayList s, ArrayList x, ArrayList y) {
        try {
            String filename = "D:/Intellij/BattleShips/src/" + file;
            System.out.println("File: " + file + " attempts: " + attempts);
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String line;
            int lineCount = 0;
            //parse the input string
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] str = line.split(" ");
                for (int i = 0; i < str.length; i++) {
                    if(lineCount == 1) {
                        s.add(Integer.parseInt(str[i]));
                    }
                    else if(lineCount == 2)
                    {
                        x.add(Integer.parseInt(str[i]));
                    }
                    else
                    {
                        y.add(Integer.parseInt(str[i]));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e + "Poor input file format");
        }
    }
    //takes the first generation of game and picks a random boat and moves it around, if no improvement to error rate is made, this process is repeated.
    //If it takes too long to find a suitable place to put the boat, another boat is picked up
    public static int[][] improveGuess(int [][] gb, ArrayList ships, ArrayList xh, ArrayList yh, int errorsToImprove)
    {
        boolean improved = false;
        int tryCount = 0;
        int placementTryCount = 0;
        int shipsNeedingRemoval = 1;
        ArrayList<Integer> removedXVals = new ArrayList<>();
        ArrayList<Integer> removedYVals = new ArrayList<>();
        Random rand = new Random();
        int[][] gbCopy = gb;
        while(improved != true) {
            ArrayList<Integer> shipsRemoved = new ArrayList<>();
            gb=gbCopy;
            //picks a random spot on the board and checks if its a 1,
            //if its not, find new random values. if it is, remove all connecting 1's
            //as there should be no boats touching this boat
            while(shipsRemoved.size() < shipsNeedingRemoval) {
                int x = rand.nextInt(gb.length);
                int y = rand.nextInt(gb.length);
                while (gb[x][y] != 1) {
                    x = rand.nextInt(gb.length);
                    y = rand.nextInt(gb.length);
                }
                //different cases to ensure all parts of a boat are removed
                int n = 0;
                int length = 0;
                while (gb[x - n][y] != 0) {
                    gb[x - n][y] = 0;
                    removedXVals.add((x - n));
                    removedYVals.add(y);
                    n++;
                    length++;
                    if (x - n < 0) {
                        break;
                    }
                }
                n = 0;
                while (gb[x + n][y] != 0) {
                    gb[x + n][y] = 0;
                    removedXVals.add((x + n));
                    removedYVals.add(y);
                    n++;
                    length++;
                    if (x + n >= gb.length) {
                        break;
                    }
                }
                n = 1;
                if (y - n >= 0) {
                    while (gb[x][y - n] != 0) {
                        gb[x][y - n] = 0;
                        removedXVals.add((x));
                        removedYVals.add(y - n);
                        n++;
                        length++;
                        if (y - n < 0) {
                            break;
                        }
                    }
                }
                n = 1;
                if (y + n < gb.length) {
                    while (gb[x][y + n] != 0) {
                        gb[x][y + n] = 0;
                        removedXVals.add((x));
                        removedYVals.add(y + n);
                        n++;
                        length++;
                        if (y + n >= gb.length) {
                            break;
                        }
                    }
                }
                //keep track of the length of the boat that is removed to ensure it is put back on the board
                //im a different position
                shipsRemoved.add(length);
            }
            ArrayList<Integer> excludedY = new ArrayList<>();
            ArrayList<Integer> excludedX = new ArrayList<>();
            String[] directionValues = new String[2];
            directionValues[0] = "UP/DOWN";
            directionValues[1] = "ACROSS";
            for (int i = 0; i < xh.size(); i++) {
                if (xh.get(i).equals(0)) {
                    excludedX.add(i);
                }
                if (yh.get(i).equals(0)) {
                    excludedY.add(i);
                }
            }
            //for every ship in the list, try to place in a legal spot
            for(int shipNum = 0; shipNum<shipsRemoved.size(); shipNum++) {
                Boolean shipPlaced = false;
                while (shipPlaced != true) {
                    int xVal = rand.nextInt(gb.length);
                    int yVal = rand.nextInt(gb.length);
                    String direction = directionValues[rand.nextInt(2)];
                    if (placementOK(xVal, yVal, shipsRemoved.get(shipNum), direction, gb, excludedX, excludedY)) {
                        if (direction.compareTo("UP/DOWN") == 0) {
                            for (int j = yVal; j < yVal + shipsRemoved.get(shipNum); j++) {
                                gb[xVal][j] = 1;
                            }
                        } else {
                            for (int j = xVal; j < xVal + shipsRemoved.get(shipNum); j++) {
                                gb[j][yVal] = 1;
                            }
                        }
                        shipPlaced = true;
                        placementTryCount = 0;
                    }
                    placementTryCount++;
                    if (placementTryCount > 150) {
                        placementTryCount = 0;
                        shipPlaced = true;
                        if(shipsNeedingRemoval < 4) {
                            shipsNeedingRemoval++;
                        }
                    }
                }
            }
            //detect if too many tries to place in a legal spot have been made
            if(tryCount > 100)
            {
                if(shipsNeedingRemoval < 4) {
                    shipsNeedingRemoval++;
                }
                tryCount = 0;
            }
            if(countErrors(gb, xh, yh) < errorsToImprove)
            {
                improved = true;
            }
            tryCount++;
        }
        printGame(gb);
        System.out.println("SCORE: " + countErrors(gb, xh, yh));
        return gb;

    }
    //puts all of the boats onto the board and makes sure theyre all legally placed
    public static void initialPopulation(ArrayList s, int[][] gb, ArrayList xh, ArrayList yh, ArrayList sh)
    {

        ArrayList<Integer> excludedY = new ArrayList<>();
        ArrayList<Integer> excludedX = new ArrayList<>();
        String[] directionValues = new String[2];
        directionValues[0] = "ACROSS";
        directionValues[1] = "UP/DOWN";
        for(int i=0; i < xh.size(); i++)
        {
            if(xh.get(i).equals(0))
            {
                excludedX.add(i);
            }
           if(yh.get(i).equals(0))
            {
                excludedY.add(i);
            }
        }
        for(int i = 0; i < s.size();i++)
        {
            Random rand = new Random();
            Boolean shipPlaced = false;
            while(shipPlaced!=true) {
                int xVal = rand.nextInt(xh.size());
                int yVal = rand.nextInt(yh.size());
                String direction = directionValues[rand.nextInt(2)];
                if (placementOK(xVal, yVal, (Integer) s.get(i), direction, gb, excludedX, excludedY)) {
                    if (direction.compareTo("UP/DOWN") == 0) {
                        for (int j = yVal; j < (yVal + (Integer) s.get(i)); j++) {
                            gb[xVal][j] = 1;
                        }
                    } else {
                        for (int j = xVal; j < (xVal + (Integer) s.get(i)); j++) {
                            gb[j][yVal] = 1;
                        }
                    }
                    shipPlaced = true;
                }

            }
        }
        //printGame(gb);
        //System.out.println("ERROR COUNT = " + countErrors(gb, xh, yh));

    }
    //prints the game out to console
    public static void printGame(int[][] gb)
    {
        String[][] textGb = new String[gb.length][gb.length];
        for(int i = 0; i < gb.length; i++)
        {
            for(int j = 0; j<gb.length;j++)
            {
                if(gb[j][i] == 0)
                {
                    textGb[j][i] = "O";
                }
                else
                {
                    textGb[j][i] = "X";
                }
                System.out.print(textGb[j][i]);
            }
            System.out.println();
        }
    }

    //checks to make sure the boat will fit on the board
    public static boolean placementOK(int x, int y, int length, String direction, int[][] gb, ArrayList excludedX, ArrayList excludedY)
    {
        int offset = 0;
        if(direction.compareTo("UP/DOWN") == 0)
        {
            for(int i = y; i < (y+length); i++)
            {
                if(i>=gb.length || gb[x][i] != 0 || isSurroundingXWaterClear(gb, x, i, offset, length, excludedX, excludedY) == false)
                {
                    return false;
                }
                offset++;
            }
        }
        else
        {
            for(int i = x; i < (x+length); i++)
            {
                if(i>=gb.length || gb[i][y] != 0 || isSurroundingYWaterClear(gb, y, i, offset, length, excludedX, excludedY) == false)
                {
                    return false;
                }
                offset++;
            }
        }
        return true;
    }
    //counts all the errors in the current generation
    public static int countErrors(int[][] gb, ArrayList xVals, ArrayList yVals)
    {
        int count = 0;
        int errors = 0;
        for(int i = 0; i<gb.length; i++)
        {
            for(int j = 0; j < gb.length; j++)
            {
                if(gb[i][j] == 1)
                {
                    count++;
                }
            }
            if((Integer) xVals.get(i) - count < 0)
            {
                errors += (-1) * ((Integer) xVals.get(i) - count);
            }
            else
            {
                errors += (Integer) xVals.get(i) - count;
            }
            count = 0;
        }
        for(int i = 0; i<gb.length; i++)
        {
            for(int j = 0; j < gb.length; j++)
            {
                if(gb[j][i] == 1)
                {
                    count++;
                }
            }
            if((Integer) yVals.get(i) - count < 0)
            {
                errors += (-1) * ((Integer) yVals.get(i) - count);
            }
                else
            {
                errors += (Integer) yVals.get(i) - count;
            }
            count = 0;
        }
        return errors;
    }
    //makes sure there are no boats that will touch the boat being put down, this covers every case of a boat being put in a corner, a side, and the middle
    //x and y methods need to be defined to ignore parts of the boat that have already been placed
    public static boolean isSurroundingXWaterClear(int[][] gb, int x, int i, int offset, int length, ArrayList excludedX, ArrayList excludedY)
    {
                if(x-1 < 0 && i-1 < 0)
                {
                    if(gb[x+1][i] == 1 || gb[x][i+1] == 1 || gb[x+1][i+1] == 1)
                    {
                        return false;
                    }
                }
                else if(x - 1 < 0 && i+1 >= gb.length)
                {
                    if(gb[x+1][i] == 1 || gb[x+1][i-1] == 1 || gb[x][i-1]==1)
                    {
                        return false;
                    }
                }
                else if(x+1 >= gb.length && i-1 < 0)
                {
                    if(offset == 0) {
                        if (gb[x - 1][i] == 1 || gb[x][i + 1] == 1 || gb[x - 1][i + 1] == 1) {
                            return false;
                        }
                    }
                    else
                    {
                        if (gb[x][i + 1] == 1 || gb[x - 1][i + 1] == 1) {
                            return false;
                        }
                    }
                }
                else if(x+1 >= gb.length && i+1 >= gb.length)
                {
                    if(offset == 0) {
                        if (gb[x - 1][i] == 1 || gb[x][i - 1] == 1 || gb[x - 1][i - 1] == 1) {
                            return false;
                        }
                    }
                    else{
                        if (gb[x][i - 1] == 1 || gb[x - 1][i - 1] == 1) {
                            return false;
                        }
                    }
                }
                else if(x-1 < 0)
                {
                    if(gb[x+1][i] == 1 || gb[x+1][i+1] == 1 || gb[x+1][i-1] == 1 || gb[x][i+1] == 1 || gb[x][i-1]==1)
                    {
                        return false;
                    }
                }
                else if(x+1 >= gb.length)
                {
                    if(offset == 0) {
                        if (gb[x - 1][i] == 1 || gb[x - 1][i - 1] == 1 || gb[x - 1][i + 1] == 1 || gb[x][i - 1] == 1 || gb[x][i + 1] == 1) {
                            return false;
                        }
                    }
                    else
                    {
                        if (gb[x - 1][i - 1] == 1 || gb[x - 1][i + 1] == 1 || gb[x][i - 1] == 1 || gb[x][i + 1] == 1) {
                            return false;
                        }
                    }
                }
                else if(i-1 < 0)
                {
                    if(offset == 0) {
                        if (gb[x - 1][i] == 1 || gb[x - 1][i + 1] == 1 || gb[x][i + 1] == 1 || gb[x + 1][i + 1] == 1 || gb[x + 1][i] == 1) {
                            return false;
                        }
                    }
                    else
                    {
                        if (gb[x - 1][i + 1] == 1 || gb[x][i + 1] == 1 || gb[x + 1][i + 1] == 1 || gb[x + 1][i] == 1) {
                            return false;
                        }
                    }
                }
                else if(i+1 >= gb.length)
                {
                    if(offset == 0) {
                        if (gb[x - 1][i] == 1 || gb[x - 1][i - 1] == 1 || gb[x][i - 1] == 1 || gb[x + 1][i - 1] == 1 || gb[x + 1][i] == 1) {
                            return false;
                        }
                    }
                    else
                    {
                        if (gb[x - 1][i - 1] == 1 || gb[x][i - 1] == 1 || gb[x + 1][i - 1] == 1 || gb[x + 1][i] == 1) {
                            return false;
                        }
                    }
                }
                else if(offset == 0) {
                    if (gb[x - 1][i] == 1 || gb[x - 1][i - 1] == 1 || gb[x][i - 1] == 1 || gb[x + 1][i - 1] == 1 || gb[x + 1][i] == 1 || gb[x - 1][i + 1] == 1 || gb[x - 1][i] == 1 || gb[x + 1][i + 1] == 1) {
                        return false;
                    }
                }
                else
                {
                    if (gb[x - 1][i - 1] == 1 || gb[x][i - 1] == 1 || gb[x + 1][i - 1] == 1 || gb[x + 1][i] == 1 || gb[x - 1][i + 1] == 1 || gb[x - 1][i] == 1 || gb[x + 1][i + 1] == 1) {
                        return false;
                    }
                }
                for(int k = 0; k<excludedX.size(); k++)
                {
                    if(x == (Integer)excludedX.get(k))
                    {
                        return false;
                    }
                }
                for(int k = 0; k<excludedY.size(); k++)
                {
                    if(i == (Integer)excludedY.get(k))
                    {
                        return false;
                    }
                }
                return true;
    }

    public static boolean isSurroundingYWaterClear(int[][] gb, int y, int i, int offset, int length, ArrayList excludedX, ArrayList excludedY)
    {
        if(y-1 < 0 && i-1 < 0)
        {
            if(gb[i][y+1] == 1 || gb[i+1][y] == 1 || gb[i+1][y+1] == 1)
            {
                return false;
            }
        }
        else if(y - 1 < 0 && i+1 >= gb.length)
        {
            if(gb[i][y+1] == 1 || gb[i-1][y+1] == 1 || gb[i-1][y]==1)
            {
                return false;
            }
        }
        else if(y+1 >= gb.length && i-1 < 0)
        {
            if(offset == 0) {
                if (gb[i][y-1] == 1 || gb[i+1][y] == 1 || gb[i+1][y-1] == 1) {
                    return false;
                }
            }
            else
            {
                if (gb[i+1][y] == 1 || gb[i+1][y-1] == 1) {
                    return false;
                }
            }
        }
        else if(y+1 >= gb.length && i+1 >= gb.length)
        {
            if(offset == 0) {
                if (gb[i][y - 1] == 1 || gb[i - 1][y] == 1 || gb[i-1][y - 1] == 1) {
                    return false;
                }
            }
            else{
                if (gb[i-1][y] == 1 || gb[i-1][y - 1] == 1) {
                    return false;
                }
            }
        }
        else if(y-1 < 0)
        {
            if(gb[i][y+1] == 1 || gb[i+1][y+1] == 1 || gb[i-1][y+1] == 1 || gb[i+1][y] == 1 || gb[i-1][y]==1)
            {
                return false;
            }
        }
        else if(y+1 >= gb.length)
        {
            if(offset == 0) {
                if (gb[i][y-1] == 1 || gb[i - 1][y - 1] == 1 || gb[i+1][y-1] == 1 || gb[i-1][y] == 1 || gb[i+1][y] == 1) {
                    return false;
                }
            }
            else
            {
                if (gb[i - 1][y - 1] == 1 || gb[i+1][y-1] == 1 || gb[i-1][y] == 1 || gb[i+1][y] == 1) {
                    return false;
                }
            }
        }
        else if(i-1 < 0)
        {
            if(offset == 0) {
                if (gb[i][y-1] == 1 || gb[i+1][y-1] == 1 || gb[i+1][y] == 1 || gb[i + 1][y + 1] == 1 || gb[i][y+1] == 1) {
                    return false;
                }
            }
            else
            {
                if (gb[i+1][y-1] == 1 || gb[i+1][y] == 1 || gb[i + 1][y + 1] == 1 || gb[i][y+1] == 1) {
                    return false;
                }
            }
        }
        else if(i+1 >= gb.length)
        {
            if(offset == 0) {
                if (gb[i][y-1] == 1 || gb[i - 1][y - 1] == 1 || gb[i-1][y] == 1 || gb[i-1][y+1] == 1 || gb[i][y+1] == 1) {
                    return false;
                }
            }
            else
            {
                if (gb[i - 1][y - 1] == 1 || gb[i-1][y] == 1 || gb[i-1][y+1] == 1 || gb[i][y+1] == 1) {
                    return false;
                }
            }
        }
        else if(offset == 0) {
            if (gb[i][y-1] == 1 || gb[i - 1][y - 1] == 1 || gb[i-1][y] == 1 || gb[i-1][y+1] == 1 || gb[i][y+1] == 1 || gb[i+1][y-1] == 1 || gb[i][y-1] == 1 || gb[i + 1][y + 1] == 1) {
                return false;
            }
        }
        else
        {
            if (gb[i - 1][y - 1] == 1 || gb[i-1][y] == 1 || gb[i-1][y+1] == 1 || gb[i][y+1] == 1 || gb[i+1][y-1] == 1 || gb[i][y-1] == 1 || gb[i+1][y+1] == 1) {
                return false;
            }
        }

        for(int k = 0; k<excludedX.size(); k++)
        {
            if(i == (Integer)excludedX.get(k))
            {
                return false;
            }
        }
        for(int k = 0; k<excludedY.size(); k++)
        {
            if(y == (Integer)excludedY.get(k))
            {
                return false;
            }
        }
        return true;
    }
}

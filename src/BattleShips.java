import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
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
            int[][] gameBoard = new int[Xhits.size()][Xhits.size()];
            initialPopulation(ships, gameBoard, Xhits, Yhits);


        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void populateLists(String file, int attempts, ArrayList s, ArrayList x, ArrayList y) {
        try {
            String filename = "D:/Intellij/BattleShips/src/" + file;
            System.out.println("File: " + file + " attempts: " + attempts);
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String line;
            int lineCount = 0;
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

    public static void improveGuess(int [][] gb, ArrayList ships)
    {
        Random rand = new Random();
        int x = rand.nextInt(gb.length);
        int y = rand.nextInt(gb.length);
        while(gb[x][y]!=1)
        {
            x = rand.nextInt(gb.length);
            y = rand.nextInt(gb.length);
        }
        int n = 0;
        int length = 0;
        while(gb[x-n][y] != 0)
        {
            gb[x-n][y] = 0;
            n++;
            length++;
        }
        n=0;
        while(gb[x+n][y] != 0)
        {
            gb[x+n][y] = 0;
            n++;
            length++;
        }
        n=1;
        while(gb[x][y-n] != 0)
        {
            gb[x][y-n] = 0;
            n++;
            length++;
        }
        n=1;
        while(gb[x][y+n] != 0)
        {
            gb[x][y+n] = 0;
            n++;
            length++;
        }




    }

    public static void initialPopulation(ArrayList s, int[][] gb, ArrayList xh, ArrayList yh)
    {
        ArrayList<Integer> excludedY = new ArrayList<>();
        ArrayList<Integer> excludedX = new ArrayList<>();
        String[] directionValues = new String[2];
        directionValues[0] = "UP/DOWN";
        directionValues[1] = "ACROSS";
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
        printGame(gb);
        System.out.println("ERROR COUNT = " + countErrors(gb, xh, yh));


    }

    public static void printGame(int[][] gb)
    {
        for(int i = 0; i < gb.length; i++)
        {
            for(int j = 0; j<gb.length;j++)
            {
                System.out.print(gb[j][i]);
            }
            System.out.println();
        }
    }
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

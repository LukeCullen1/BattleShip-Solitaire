public class BattleShips {
    public static void main(String[] args)
    {
        if(args.length != 2)
        {
            System.out.println("Please enter a file to read and a number of attempts allowed");
            return;
        }
        try
        {
            String file = args[0];
            int attempts = Integer.parseInt(args[1]);
            System.out.println("File: " + file + " attempts: " + attempts);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }
}

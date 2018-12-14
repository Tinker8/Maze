import java.io.*;
import java.util.*;

/**
 Author: Adam Griffiths
 ##########################################
 This program takes one argument: filename
 ##########################################
 ############
 Requirements
 ############
INPUT:
        <WIDTH> <HEIGHT><CR>
        <START_X> <START_Y><CR>		(x,y) location of the start. (0,0) is upper left and (width-1,height-1) is lower right
        <END_X> <END_Y><CR>		(x,y) location of the end
        <HEIGHT> rows where each row has <WIDTH> {0,1} integers space delimited

        OUTPUT:
        the maze with a path from start to end
        walls marked by '#', passages marked by ' ', path marked by 'X', start/end marked by 'S'/'E'

 Example file:
 10 10
 1 1
 8 8
 1 1 1 1 1 1 1 1 1 1
 1 0 0 0 0 0 0 0 0 1
 1 0 1 0 1 1 1 1 1 1
 1 0 1 0 0 0 0 0 0 1
 1 0 1 1 0 1 0 1 1 1
 1 0 1 0 0 1 0 1 0 1
 1 0 1 0 0 0 0 0 0 1
 1 0 1 1 1 0 1 1 1 1
 1 0 1 0 0 0 0 0 0 1
 1 1 1 1 1 1 1 1 1 1

 OUTPUT:
 ##########
 #SXX     #
 # #X######
 # #XX    #
 # ##X# ###
 # # X# # #
 # # XX   #
 # ###X####
 # #  XXXE#
 ##########
*/
class RunMaze {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Maze simpleMaze = new Maze(args[0]);
        simpleMaze.run();

        System.out.println("GameOver");
    }
}
class Maze {

    private String filepath, filename;
    private int length;
    private String[][] grid = new String[0][]; //Creates a 2d String array to store the maze

    public Maze(String file) throws IOException {
        filename = file;
        loadFile();
        Scanner reader = new Scanner(System.in);
        reader.close();
    }

    private String getFilepath(){
        return filepath;
    }

    private void loadFile(){
        //Combines the local system directory with argument for easier user input
        filepath = System.getProperty("user.dir") + "\\" + filename + ".txt";
    }

    public void run()throws IOException{
        File file = new File(filepath);
        BufferedReader br = new BufferedReader(new FileReader(file));

        // Takes the first line from the text file and splits it for the array x,y
        String size = br.readLine();
        String[] tokens = size.split("\\s+");
        int rows = Integer.parseInt(tokens[0]);
        int col = Integer.parseInt(tokens[1]);
        //System.out.println(rows);
        //System.out.println(col);

        //2d array for maze grid using dimensions from text file
        grid = new String[col][rows];

        //Gets the starting location for within the maze from text file
        String startloc = br.readLine();
        tokens = startloc.split("\\s+");
        int startx = Integer.parseInt((tokens[0]));
        int starty = Integer.parseInt((tokens[1]));
        //System.out.println(startx);
        //System.out.println(starty);

        //Gets the destination for the end of the maze
        String endloc = br.readLine();
        tokens = endloc.split("\\s+");
        int endx = Integer.parseInt((tokens[0]));
        int endy = Integer.parseInt((tokens[1]));
        //System.out.println(endx);
        //System.out.println(endy);

        //loops through each line in the text file, starting from line 4
        int row = 0;
        String value;
        while ((value = br.readLine()) != null)
        {
            //System.out.println(test);
            tokens = value.split("\\s+");
            for (int i = 0; i < tokens.length; i++)
            {
                grid[row][i] = (tokens[i]); //Stores each value from the text file to the correct maze position
            }
            row++;
        }

        br.close(); //closes file reader as it is no longer needed

        //Using the start and end position, tries to solve the maze
        if (solve(startx, starty, endy, endx))
        {
            grid[startx][starty] = "S";
            markWalls();
            System.out.println("Solved!");
            printMaze();
        }
        else
            System.out.println("Failed");
    }

    public void markWalls(){
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++)
            {
                if (grid[i][j].equals("1"))
                {
                    grid[i][j] = "#";
                }
            }
        }
    }

    public void printMaze () {
        //Loops through all contents of the maze array, printing it to the console
        System.out.println();
        for (int row=0; row < grid.length; row++)
        {
            for (int column=0; column < grid[row].length; column++)
                System.out.print (grid[row][column]);
            System.out.println();
        }
        System.out.println();
    }

    public boolean solve(int row, int col, int endx, int endy){
        boolean solved = false;
        boolean completed = false;
        if (checked (row, col))//Tries starting position
        {
            grid[row][col] = " "; //Mark any passageways with a space

            //If I had more time I would like to have condensed this entire section
            //to reduce duplication and increase readability, would just need to combine
            //the directional movement and conditional statements
            while (grid[row][col].equals("0"))//Checks every position in the maze that has yet to be tried
            {
                completed = solve (row+1, col, endx, endy); //South
                if (!completed)
                    completed = solve (row, col+1,endx,endy); //East
                if (!completed)
                    completed = solve (row-1, col, endx, endy); //North
                if (!completed)
                    completed = solve (row, col-1, endx, endy); //West
            }

            //Marks the end of the maze and marks it as solved
            if (row == endx && col == endy)
            {
                solved = true;  //Maze is solved
                grid[row][col] = "E";
            }
            else
            {
                //Finds the end of the maze
                solved = solve (row+1, col, endx, endy); //South
                if (!solved)
                    solved = solve (row, col+1, endx, endy); //East
                if (!solved)
                    solved = solve (row-1, col, endx, endy); //North
                if (!solved)
                    solved = solve (row, col-1, endx, endy); //West
                if (solved)
                    grid[row][col] = "X"; //Marks the correct path that was used to find the end
            }

            if (!completed) //Runs through the rest of the maze even if the end has been found
            {
                completed = solve (row+1, col, endx, endy); //South
                if (!completed)
                    completed = solve (row, col+1, endx, endy); //East
                if (!completed)
                    completed = solve (row-1, col, endx, endy); //North
                if (!completed)
                    completed = solve (row, col-1, endx, endy); //West
            }
        }
        return solved;
    }

    private boolean checked(int row, int col){
        boolean result = false;

        //Check if current position is within the 2d array
        if (row >= 0 && row < grid.length &&
                col >= 0 && col < grid[0].length)

            //Check if position is an open path
            if (grid[row][col].equals("0"))
                result = true;
        return result;
    }
}

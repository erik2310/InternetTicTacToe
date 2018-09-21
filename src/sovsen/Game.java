package sovsen;

        import java.util.ArrayList;
        import java.util.List;

public class Game {

    private static List<ClientController> observers = new ArrayList<ClientController>();

    private static final int NO_PLAYER = 0;
    private static final int PLAYER_X = 1;
    private static final int PLAYER_O = 2;
    private static final int GAME_END = 3;


    private static int GAME = 0;
    private static int WIN = 1;
    private static int UPDATE = 2;
    private static int ERROR = 3;

    private static int state = 0;
    private static int[][] grid = new int[3][3];
    private static int player = 0;
    private static int winner = 0;



    public static void initGame(){
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid.length; j++){
                grid[i][j] = 0;
            }
        }
    }


    public static String processInput(String input){
        String output = "";
        System.out.println("processInput(): " + input);



        int[] field = getField(input);
        if (field != null){


            if (validMove(field) == true){
                setMark(player, getField(input));


                //Check if there's a win
                if (checkWin() == true){
                    showWin();
                    output = "Congratulations!";
                } else {

                    if (player == PLAYER_X) {
                        setPlayer(PLAYER_O);
                        output = "O's turn;";
                    } else if ((player == PLAYER_O) || (state == NO_PLAYER)){
                        setPlayer(PLAYER_X);
                        output = "X's turn";
                    } else if (state == GAME_END) {
                        if (winner == PLAYER_X){
                            output = "Player X has won";
                        } else if (winner == PLAYER_O){
                            output = "Player O has won";
                        }
                    }
                }


            } else {

                output = "Field occupied. Please select another location.";
            }

            Server.notifyAllObservers();


        }
        return output;
    }



    public static String showWin(){
        return "Player " + player + " won!";
    }



    private static int[] getField(String str){
      int[] temp = new int[2];


        switch(str){
            case "1":
                temp[0] = 0;
                temp[1] = 0;
                break;
            case "2":
                temp[0] = 0;
                temp[1] = 1;
                break;
            case "3":
                temp[0] = 0;
                temp[1] = 2;
                break;
            case "4":
                temp[0] = 1;
                temp[1] = 0;
                break;
            case "5":
                temp[0] = 1;
                temp[1] = 1;
                break;
            case "6":
                temp[0] = 1;
                temp[1] = 2;
                break;
            case "7":
                temp[0] = 2;
                temp[1] = 0;
                break;
            case "8":
                temp[0] = 2;
                temp[1] = 1;
                break;
            case "9":
                temp[0] = 2;
                temp[1] = 2;
                break;
            default:
                return null;
        }
        System.out.println("Temp is: " + temp[0]);
        return temp;
    }

    private static boolean validMove(int[] i){
        int[] j = new int[2];
        j[0] = i[0];
        j[1] = i[1];
        System.out.println("VALID MOVE");
        System.out.println("int: " + i[0]);
        if(grid[i[0]][i[1]] == 0){

            return true;
        }

        return false;
    }


    private static void setMark(int mark, int[] i){
        grid[i[0]][i[1]] = mark;
    }




    public static boolean checkWin(){

        for (int i = 0; i < 2; i++){

            if (checkH(i,0) || checkV(0,1) || checkCr()){
                return true;
            }

        }
        return false;
    }


    private static boolean checkH(int i1, int i2){

        if (grid[i1][i2] != 0){

            if ((grid[i1][i2] == grid[i1][i2 + 1]) && (grid[i1][i2] == grid[i1][i2 + 2])){

                return true;
            }
        }

        return false;
    }

    private static boolean checkV(int i1, int i2){

        if (grid[i1][i2] != 0) {

            if ((grid[i1][i2] == grid[i1 + 1][i2]) && (grid[i1][i2] == grid[i1 + 2][i2])) {

                return true;
            }
        }
        return false;
    }

    private static boolean checkCr(){

        int topleft = grid[0][0];
        int mid = grid[1][1];
        int topright = grid[0][2];
        int botleft = grid[2][0];
        int botright = grid[2][2];

        if (mid != 0) {

            if (((mid == topleft) && (mid == botright)) || ((mid == botleft) && (mid == topright))) {

                return true;
            }
        }

        return false;
    }



    public static void attach(ClientController s){
        System.out.println(s.toString() + " has been attached to Game");
        observers.add(s);
        //Assign clients as X and O

        if (getObservers() < 2){
            observers.get(0).setPlayer(PLAYER_X);
            TTTP.setClient1(s);
        } else {
            observers.get(1).setPlayer(PLAYER_O);
            TTTP.setClient1(s);
        }

        s.start();
    }




public static List<ClientController> getAllObservers(){
        return observers;
}

    public static int getObservers(){
        return observers.size();
    }


    private static void setPlayer(int player1){

        player = player1;
    }


}

package gurdle.gui;

import gurdle.Model;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import util.Observer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The graphical user interface to the Wordle game model in
 * {@link Model}.
 *
 * @author Connor Patterson
 */
public class Gurdle extends Application
        implements Observer< Model, String > {


    public int numGuesses;
    private Model model;
    public static final ArrayList<String> winningWords = new ArrayList<>();
    public static final ArrayList<String> dictionaryWords = new ArrayList<>();

    private int row;
    private int col;
    private final int MAX_COLUMN = 5;
    private final int MAX_ROW = 6;
    private String winningWord;
    public GridPane keyboard;
    public GridPane wordGrid;
    public GridPane root;
    public StackPane onTop;
    public VBox options;
    public int CURRENT_ROW;
    public int CURRENT_COL;
    public BorderPane layout;
    public Stage mainstage;
    public boolean win;


    @Override public void init() {
        this.win = false;
        this.winningWord = "";
        this.keyboard = new GridPane();
        this.wordGrid = new GridPane();
        this.onTop = new StackPane();
        this.options = new VBox(10);
        this.root = new GridPane();
        this.CURRENT_ROW = 0;
        this.CURRENT_COL = 0;
        this.model = new Model();
        this.model.addObserver( this );
        this.layout = new BorderPane();



        this.model.newGame();
    }

    @Override
    public void start( Stage mainStage ) {




        Text topSplash = new Text("Make a guess!");
        BorderPane.setAlignment(topSplash, Pos.TOP_CENTER);
        Text guesses = new Text("#guesses: " + numGuesses);
        BorderPane.setAlignment(guesses, Pos.TOP_LEFT);


        layout.setTop(topSplash);
        layout.setLeft(guesses);



        Button cheat = new Button("Cheat");
        cheat.setOnAction(event -> {Controller(cheat);
        });

        Button newGame = new Button("New Game");
        newGame.setOnAction(event -> {Controller(newGame);
        });

        options.getChildren().addAll(cheat, newGame);
        layout.setRight(options);

        keyboard = keyboard();
        wordGrid = createGrid("    ", 0, 0);

        layout.setCenter(wordGrid);
        layout.setBottom(keyboard);

        Scene test = new Scene(layout, 500, 715);




        mainStage.setScene(test);
        mainStage.setTitle("Gurdle");
        mainStage.show();




    }

    public GridPane createGrid(String c, int row, int col){


        if(!Objects.equals(c, "    ")){
            Label label = new Label(c);
            label.setFont(Font.font(40));

            root.setHgap(8);
            root.setVgap(8);

            StackPane cell = new StackPane(label);
            cell.setStyle("-fx-border-color: black; -fx-border-width: 2px;");

            root.add(cell, row, col);
            root.setAlignment(Pos.BASELINE_CENTER);


        }


        for (int i = 1; i <= MAX_ROW; i++) {
            for (int j = 1; j <= MAX_COLUMN; j++) {

                Label label = new Label("    ");

                label.setFont(Font.font(40));

                root.setHgap(8);
                root.setVgap(8);

                StackPane cell = new StackPane(label);
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2px;");

                root.add(cell, j, i);
                root.setAlignment(Pos.BASELINE_CENTER);


            }
        }
        return root;
    }

    public GridPane keyboard(){
        GridPane grid = new GridPane();
        grid.setHgap(10); // Horizontal spacing between buttons
        grid.setVgap(10); // Vertical spacing between buttons
        grid.setPadding(new Insets(10)); // Padding around the grid

        // Define the keys for the keyboard
        String[] keys = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M"
        };

        // Add the keys as buttons to the grid
        int row = 0;
        int col = 0;
        for (String key : keys) {
            Button button = new Button(key);
            button.setPrefSize(50, 50); // Set button size
            button.setOnAction(event -> {Controller(button);
            });
            grid.add(button, col, row); // Add button to the grid
            col++;
            if (col > 9) { // Max 10 keys per row
                col = 0;
                row++;
            }


        }
        grid.setAlignment(Pos.BOTTOM_LEFT);
        Button button = new Button("Enter");
        button.setOnAction(event -> {Controller(button);});
        button.setPrefSize(60, 50);
        grid.add(button, 8, 2);

        return grid;
    }



    @Override
    public void update( Model model, String message ) {

    }
    public void quit() {
        layout.getChildren().clear();
        init();
        start(new Stage());
    }


    public void gameStatus(){
        if (win){
            System.out.println("congrats, you have won!");
            Text topSplash = new Text("Congrats, you have won!");
            Text bottomSplash = new Text("Press new game or close out");
            BorderPane.setAlignment(topSplash, Pos.TOP_CENTER);
            BorderPane.setAlignment(bottomSplash, Pos.BOTTOM_CENTER);
            layout.setTop(topSplash);
            layout.setBottom(bottomSplash);
        }
        else {
            System.out.println("Sorry, out of guesses");
            Text topSplash = new Text("Sorry, out of guesses");
            Text bottomSplash = new Text("Press new game or close out");
            BorderPane.setAlignment(topSplash, Pos.TOP_CENTER);
            BorderPane.setAlignment(bottomSplash, Pos.BOTTOM_CENTER);
            layout.setTop(topSplash);
            layout.setBottom(bottomSplash);

        }
    }

    public void Controller(Button button){

        String buttonCha = button.getText();
        if(numGuesses == 6){
            gameStatus();
        }


        if(buttonCha.equals("Enter")){
            checkWin(winningWord);

            winningWord = "";
            numGuesses ++;
            Text guesses = new Text("#guesses: " + numGuesses);
            BorderPane.setAlignment(guesses, Pos.TOP_LEFT);
            layout.setLeft(guesses);
        }
        if(buttonCha.equals("Cheat")){
            options.getChildren().addAll(new Text("Secret: " + model.secret()));
        }
        if(buttonCha.equals("New Game")){
            model.newGame();
            quit();

        }

        if(buttonCha.length() == 1){


            col = getCURRENT_COL();
            row = getCURRENT_ROW();

            if(winningWord.length()>4){
                System.out.println("Error, guess is too long, trimming");
                winningWord = winningWord.substring(5);
            }

            winningWord+= buttonCha;

            button.setStyle("-fx-background-color: #bcbcbc; ");

            layout.setCenter(createGrid(buttonCha, row+1, col+1));

            CURRENT_ROW +=1;
            if(row == 4){
                CURRENT_COL++;
                CURRENT_ROW = 0;

            }

        }
    }

    public GridPane checkWin(String guess){
        col = getCURRENT_COL();
        row = 1;
        String solution = model.secret();

        for(int i = 0;i<5;i++)
            {if(solution.contains(String.valueOf(guess.charAt(i)))){

                String cha = String.valueOf(guess.charAt(i));
                StackPane cell = new StackPane(new Label(cha));
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #BE8641");
                root.add(cell, i+1, col);
                root.setAlignment(Pos.BASELINE_CENTER);

            }
            else{

                String cha = String.valueOf(guess.charAt(i));
                StackPane cell = new StackPane(new Label(cha));
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #D93225;");
                root.add(cell, i+1, col);
                root.setAlignment(Pos.BASELINE_CENTER);
            }

        }

        for(int i = 0;i<5;i++){
            if(guess.charAt(i) == solution.charAt(i)){
                String cha = String.valueOf(guess.charAt(i));
                StackPane cell = new StackPane(new Label(cha));
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #2FBB36;");
                root.add(cell, i+1, col);
                root.setAlignment(Pos.BASELINE_CENTER);

            }
        }

        if(guess.equals(solution)){
            win = true;
            gameStatus();
        }

        return root;

    }

    public int getCURRENT_ROW(){
        return CURRENT_ROW;
    }
    public int getCURRENT_COL(){
        return CURRENT_COL;
    }

    public static void main( String[] args ) {
        if ( args.length > 1 ) {
            System.err.println( "Usage: java Gurdle [1st-secret-word]" );
        }
        Application.launch( args );
    }
}

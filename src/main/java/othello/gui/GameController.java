package othello.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;



import javafx.scene.shape.Circle;
import othello.gamelogic.*;
import othello.gamelogic.state.GameHistory;
import othello.gamelogic.state.GameMemento;
import javafx.stage.FileChooser;
import othello.util.SaveLoadUtil;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Manages the interaction between model and view of the game.
 */
public class GameController  {

    // FXML Variables to manipulate UI components
    @FXML
    private Label turnLabel;

    @FXML
    private Pane gameBoard;

    @FXML
    private Circle turnCircle;

    @FXML
    private Button computerTurnBtn;

    @FXML
    private ComboBox<String> blackPlayerType;

    @FXML
    private ComboBox<String> whitePlayerType;

    @FXML
    private Button startButton;


    @FXML private Button saveBtn;
    @FXML private Button loadBtn;

    // Caretaker for mementos
    private final GameHistory history = new GameHistory();

    // Private variables
    private OthelloGame og;
    private int skippedTurns;
    private GUISpace[][] guiBoard;

    /**
     * Starts the game, called after controller initialization  in start method of App.
     * Sets the 2 players, their colors, and creates an OthelloGame for logic handling.
     * Then, shows the first move, beginning the game "loop".
     * @param arg1 type of player for player 1, either "human" or some computer strategy
     * @param arg2 type of player for player 2, either "human" or some computer strategy
     */
    public void initGame(String arg1, String arg2) {
        Player playerOne;
        Player playerTwo;
        // Player 1
        if (arg1.equals("human")) {
            playerOne = new HumanPlayer();
        } else {
            playerOne = new ComputerPlayer(arg1);
        }

        // Player 2
        if (arg2.equals("human")) {
            playerTwo = new HumanPlayer();
        } else {
            playerTwo = new ComputerPlayer(arg2);
        }

        // Set Colors
        playerOne.setColor(BoardSpace.SpaceType.BLACK);
        playerTwo.setColor(BoardSpace.SpaceType.WHITE);

        // Make a new game, create the visual board and display it with initial spaces
        og = new OthelloGame(playerOne, playerTwo);
        guiBoard = new GUISpace[8][8];
        displayBoard();
        initSpaces();

        // Player 1 starts the game
        turnText(playerOne);
        takeTurn(playerOne);
    }

    /**
     * Displays the board initially, adding the GUI squares into the window.
     * Also adds the initial state of the board with black and white taking spaces at the center.
     */
    @FXML
    protected void displayBoard() {
        BoardSpace[][] board = og.getBoard();
        for (BoardSpace[] spaces : board) {
            for (BoardSpace space : spaces) {
                GUISpace guiSpace = new GUISpace(space.getX(), space.getY(), space.getType());
                Pane square = guiSpace.getSquare();
                gameBoard.getChildren().add(square);
                guiBoard[space.getX()][space.getY()] = guiSpace;
            }
        }
    }


    /**
     * Called when the user clicks the "Start Game" button.
     */
    @FXML
    private void onStartGame(ActionEvent event) {
        //  Read and normalize the selected strategy names
        String blackChoice = blackPlayerType.getValue().toLowerCase();
        String whiteChoice = whitePlayerType.getValue().toLowerCase();

        //  Instantiate each player: use HumanPlayer for "human", otherwise use ComputerPlayer with the chosen strategy
        Player black = "human".equals(blackChoice)
                ? new HumanPlayer()
                : new ComputerPlayer(blackChoice);
        Player white = "human".equals(whiteChoice)
                ? new HumanPlayer()
                : new ComputerPlayer(whiteChoice);

        //  Initialize the game logic with the provided strategy identifiers
        initGame(blackChoice, whiteChoice);

        //  Disable the selection controls and start button to lock in the players
        blackPlayerType.setDisable(true);
        whitePlayerType.setDisable(true);
        startButton.setDisable(true);

        // Setup Save/Load handlers
        saveBtn.setDisable(false);
        loadBtn.setDisable(false);

        // Save to file
        saveBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Othello Game");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Othello Save File (*.ots)", "*.ots")
            );
            File file = chooser.showSaveDialog(gameBoard.getScene().getWindow());
            if (file != null) {
                try {
                    SaveLoadUtil.saveGame(og, file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loadBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load Othello Game");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Othello Save File (*.ots)", "*.ots")
            );
            File file = chooser.showOpenDialog(gameBoard.getScene().getWindow());
            if (file != null) {
                try {
                    SaveLoadUtil.loadGame(og, file);

                    clearBoard();
                    displayBoard();
                    turnText(og.getCurrentPlayer());

                    takeTurn(og.getCurrentPlayer());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    /**
     * Clears the board visually, called every time the board is redisplayed after the first time
     */
    @FXML
    protected void clearBoard() {
        BoardSpace[][] board = og.getBoard();
        for (BoardSpace[] spaces : board) {
            for (BoardSpace space : spaces) {
                GUISpace guiSpace = guiBoard[space.getX()][space.getY()];
                Pane square = guiSpace.getSquare();
                gameBoard.getChildren().remove(square);
            }
        }
    }

    /**
     * Sets the initial state of the Othello board
     */
    @FXML
    protected void initSpaces(){
        // Initial spaces
        guiBoard[3][3].addOrUpdateDisc(BoardSpace.SpaceType.WHITE);
        guiBoard[4][4].addOrUpdateDisc(BoardSpace.SpaceType.WHITE);
        guiBoard[3][4].addOrUpdateDisc(BoardSpace.SpaceType.BLACK);
        guiBoard[4][3].addOrUpdateDisc(BoardSpace.SpaceType.BLACK);
    }

    /**
     * Displays the score of the board and the current turn.
     */
    @FXML
    protected void turnText(Player player) {
        String humanOrCom = player instanceof HumanPlayer ? "(Human)\n" : "(Computer)\n";
        turnCircle.setFill(player.getColor().fill());
        turnLabel.setText(
                player.getColor() + "'s Turn\n" + humanOrCom + "Score: \n" +
                        og.getPlayerOne().getColor() + ": " + og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + " - " +
                        og.getPlayerTwo().getColor() + ": " + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size());
    }

    /**
     * Displays the score of the board.
     */
    @FXML
    protected void skipTurnText(Player player) {
        turnLabel.setText(
                "Skipped " + player.getColor() + "\n due to no moves available! " + otherPlayer(player).getColor() + "'s Turn\n" +
                        og.getPlayerOne().getColor() + ": " + og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + " - " +
                        og.getPlayerTwo().getColor() + ": " + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size());
    }

    /**
     * Either shows moves for humans or makes a decision for a computer.
     * @param player player to take a turn for, whether its human or computer
     */
    @FXML
    protected void takeTurn(Player player) {
        if (player instanceof HumanPlayer human) {
            computerTurnBtn.setVisible(false);
            showMoves(human);
        } else if (player instanceof ComputerPlayer computer) {
            computerTurnBtn.setVisible(true);
            computerTurnBtn.setOnAction(actionEvent -> {
                computerDecision(computer);
            });
        }
    }

    /**
     * Shows the current moves possible for the current board configuration.
     * If availableMoves is null, the getAvailableMoves method is likely not implemented yet.
     * If availableMoves is empty (no moves found, or full board), the game ends.
     * @param player player to show moves for
     */
    @FXML
    protected void showMoves(HumanPlayer player) {
        // check if both players have no moves → end game
        Map<BoardSpace, List<BoardSpace>> myMoves = og.getAvailableMoves(player);
        Map<BoardSpace, List<BoardSpace>> oppMoves = og.getAvailableMoves(otherPlayer(player));
        if (myMoves.isEmpty() && oppMoves.isEmpty()) {
            gameOver();
            return;
        }

        // check if current player has no moves → skip turn
        if (myMoves.isEmpty()) {
            skipTurnText(player);
            // schedule next turn
            Platform.runLater(() -> takeTurn(otherPlayer(player)));
            return;
        }

        // normal case: player has moves
        for (BoardSpace dest : myMoves.keySet()) {
            GUISpace gs = guiBoard[dest.getX()][dest.getY()];
            Pane cell = gs.getSquare();
            gs.setBgColor(Color.LIGHTYELLOW);

            EventHandler<MouseEvent> enter = e -> gs.setBgColor(Color.LIME);
            EventHandler<MouseEvent> exit  = e -> gs.setBgColor(Color.LIGHTYELLOW);
            cell.addEventHandler(MouseEvent.MOUSE_ENTERED, enter);
            cell.addEventHandler(MouseEvent.MOUSE_EXITED, exit);

            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                cell.removeEventHandler(MouseEvent.MOUSE_ENTERED, enter);
                cell.removeEventHandler(MouseEvent.MOUSE_EXITED, exit);
                selectSpace(player, myMoves, dest);
            });
        }
    }

    /**
     * Gets the computer decision, then selects the space.
     * @param player a reference to the current computer player (could be player 1 or 2)
     */
    @FXML
    protected void computerDecision(ComputerPlayer player) {
        // check if both players have no moves → end game
        Map<BoardSpace, List<BoardSpace>> myMoves = og.getAvailableMoves(player);
        Map<BoardSpace, List<BoardSpace>> oppMoves = og.getAvailableMoves(otherPlayer(player));
        if (myMoves.isEmpty() && oppMoves.isEmpty()) {
            gameOver();
            return;
        }

        // check if computer has no moves → skip turn
        if (myMoves.isEmpty()) {
            skipTurnText(player);
            Platform.runLater(() -> takeTurn(otherPlayer(player)));
            return;
        }

        // normal case: computer makes a move
        BoardSpace choice = og.computerDecision(player);
        og.takeSpaces(player, otherPlayer(player), myMoves, choice);
        updateGUIBoard(player, myMoves, choice);

        clearBoard();
        displayBoard();
        turnText(otherPlayer(player));
        Platform.runLater(() -> takeTurn(otherPlayer(player)));
    }

    /**
     * Handles what happens when a player chooses to select a certain space during their turn.
     * @param player current turn's player
     * @param availableMoves the available moves gotten from showMoves earlier
     * @param selectedDestination the selected destination space that was clicked on
     */
    @FXML
    protected void selectSpace(Player player,
                               Map<BoardSpace, List<BoardSpace>> availableMoves,
                               BoardSpace selectedDestination) {
        // 1. Execute the move and flip logic in the game model
        og.takeSpaces(
                player,
                otherPlayer(player),
                availableMoves,
                selectedDestination
        );

        // 2. Re‐draw the board so the GUI matches the updated model
        clearBoard();
        displayBoard();

        // 3. Update the turn indicator for the next player
        turnText(otherPlayer(player));

        // 4. If the next player is the computer, let it move immediately
        takeTurn(otherPlayer(player));
    }

    /**
     * Updates the GUI Board by adding or updating discs from all origins to a given destination
     * @param player player that is taking a turn
     * @param availableMoves the list of all available destinations and origins
     * @param selectedDestination the selected destination from either user input or computer decision
     */
    @FXML
    protected void updateGUIBoard(Player player, Map<BoardSpace, List<BoardSpace>> availableMoves, BoardSpace selectedDestination) {
        List<BoardSpace> selectedOrigins = availableMoves.get(selectedDestination);
        for (BoardSpace selectedOrigin : selectedOrigins) {
            int offsetX = selectedDestination.getX() - selectedOrigin.getX();
            int offsetY = selectedDestination.getY() - selectedOrigin.getY();
            // Intercardinals
            if (Math.abs(offsetX) > 0 && Math.abs(offsetY) > 0) {
                for (int i = 0; i < Math.abs(offsetX) + 1; i++) {
                    int x = (int) (selectedOrigin.getX() + (i * Math.signum((offsetX))));
                    int y = (int) (selectedOrigin.getY() + (i * Math.signum((offsetY))));
                    guiBoard[x][y].addOrUpdateDisc(player.getColor());
                }
            } else { // Cardinals
                // Origin -> Destination
                for (int i = 0; i < Math.abs(offsetX) + 1; i++) {
                    for (int j = 0; j < Math.abs(offsetY) + 1; j++) {
                        int x = (int) (selectedOrigin.getX() + (i * Math.signum((offsetX))));
                        int y = (int) (selectedOrigin.getY() + (j * Math.signum((offsetY))));
                        guiBoard[x][y].addOrUpdateDisc(player.getColor());
                    }
                }
            }
        }
    }

    /**
     * Returns the other player given one of the player fields
     */
    @FXML
    protected Player otherPlayer(Player player) {
        if (player == og.getPlayerOne()) {
            return og.getPlayerTwo();
        } else {
            return og.getPlayerOne();
        }
    }

    /**
     * Ends the game.
     */
    @FXML
    protected void gameOver() {
        boolean p1Victory = false;
        boolean tie = false;
        if (og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() > og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size()) {
            p1Victory = true;
        } else if (og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() == og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size()) {
            tie = true;
        }
        if (tie) {
            turnLabel.setText("GAME OVER!\n Game Tied with scores: \n " +
                    og.getPlayerOne().getColor() + ": " + og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + " - " +
                    og.getPlayerTwo().getColor() + ": " + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size());
        } else if (p1Victory) {
            turnLabel.setText("GAME OVER!\n BLACK wins with scores: \n " +
                    og.getPlayerOne().getColor() + ": " + og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + " - " +
                    og.getPlayerTwo().getColor() + ": " + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size());
        } else {
            turnLabel.setText("GAME OVER!\n WHITE wins with scores: \n " +
                    og.getPlayerOne().getColor() + ": " + og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + " - " +
                    og.getPlayerTwo().getColor() + ": " + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size());
        }
    }

}
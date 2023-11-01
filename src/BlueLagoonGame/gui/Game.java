package BlueLagoonGame.gui;

import BlueLagoonGame.BlueLagoonGame;
import BlueLagoonGame.GUIPosition;
import BlueLagoonGame.board.Tile;
import BlueLagoonGame.piece.Piece;
import BlueLagoonGame.player.Player;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.text.*;

import java.util.*;

import BlueLagoonGame.*;


public class Game extends Application {

    final Font phaseTitleFont = Font.loadFont(getClass().getResourceAsStream("m5x7.ttf"), 72);
    final Font textFont = Font.loadFont(getClass().getResourceAsStream("m5x7.ttf"), 25);
    private final Group root = new Group();
    private final Group controls = new Group();
    private final Group tiles = new Group();
    private final Group pieces = new Group();
    private final Group scores = new Group();
    private final Group status = new Group();
    private final Group playerOperation = new Group();
    private final Group settlerMoveHints = new Group();
    private final Group villageMoveHints = new Group();
    private final Group moveHintsHover = new Group();
    private final Group guideBeforeGame = new Group();
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    private final double pieceDisplayGUIX = 863.0;
    private final double[] settlerDisplayGUIY = {85, 235, 385, 535};
    private final double[] villageDisplayGUIY = {125, 275, 425, 575};
    private final double pieceRemainedNumDisplayX = 930;
    private final double[] settlerRemainedNumDisplayY = {108, 258, 408, 558};
    private final double[] villageRemainedNumDisplayY = {148, 298, 448, 598};
    private final double pointerDisplayGUIX = 750.0;
    private final double[] pointerDisplayGUIY = {58, 208, 358, 508};
    private final double playerScoreDisplayGUIX = 800;
    private final double[] playerScoreDisplayGUIY = {70, 220, 370, 520};
    private final double resourceNumDisplayGUIX = 1015;
    private final double[] resourceNumDisplayGUIY = playerScoreDisplayGUIY;
    private final double resourceTypeDisplayGUIX = 1035;
    private final double[] resourceTypeDisplayGUIY = playerScoreDisplayGUIY;
    private final HashMap<Piece.PieceType, ArrayList<Image>> pieceImageMap = new HashMap<>(Map.ofEntries(
            Map.entry(Piece.PieceType.SETTLER, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/PINKSETTLER.png")),
                    new Image(getClass().getResourceAsStream("images/ORANGESETTLER.png")),
                    new Image(getClass().getResourceAsStream("images/BLUESETTLER.png")),
                    new Image(getClass().getResourceAsStream("images/YELLOWSETTLER.png"))
            ))),
            Map.entry(Piece.PieceType.VILLAGE, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/PINKVILLAGE.png")),
                    new Image(getClass().getResourceAsStream("images/ORANGEVILLAGE.png")),
                    new Image(getClass().getResourceAsStream("images/BLUEVILLAGE.png")),
                    new Image(getClass().getResourceAsStream("images/YELLOWVILLAGE.png"))
            ))),
            Map.entry(Piece.PieceType.BAMBOO, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/BAMBOO.png"))
            ))),
            Map.entry(Piece.PieceType.COCONUT, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/COCONUT.png"))
            ))),
            Map.entry(Piece.PieceType.WATER, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/WATER.png"))
            ))),
            Map.entry(Piece.PieceType.PRECIOUS_STONE, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/PRECIOUS_STONE.png"))
            ))),
            Map.entry(Piece.PieceType.STATUETTE, new ArrayList<>(List.of(
                    new Image(getClass().getResourceAsStream("images/STATUETTE.png"))
            )))
    ));
    private final HashMap<Tile.TileType, Image> tileImageMap = new HashMap<>(Map.ofEntries(
            Map.entry(Tile.TileType.STONE_TILE,
                    new Image(getClass().getResourceAsStream("images/STONE_TILE.png"))),
            Map.entry(Tile.TileType.SEA_TILE,
                    new Image(getClass().getResourceAsStream("images/SEA_TILE.png"))),
            Map.entry(Tile.TileType.LAND_TILE,
                    new Image(getClass().getResourceAsStream("images/LAND_TILE.png")))
    ));
    private final Image pointerImage = new Image(getClass().getResourceAsStream("images/POINTER.png"));

    private final HashMap<GUIPosition, Position> positionMap = new HashMap();
    // information of each island score and score position to display on the GUI
    private final int[] defaultMapIslandScore = {6, 10, 8, 8, 8, 8, 10, 6};
    private final GUIPosition[] defaultMapIslandScorePosition = {new GUIPosition(80, 1), new GUIPosition(260, 1), new GUIPosition(410, 1), new GUIPosition(630, 1), new GUIPosition(10, 330), new GUIPosition(160, 660), new GUIPosition(460, 660), new GUIPosition(630, 660)};

    public ImageView getView(Image content, double x, double y, Boolean toShift, Double... size) {
        ImageView view = new ImageView(content);
        if (size.length != 0) {
            view.setFitHeight(size[0]);
            view.setFitWidth(size[0]);
            if (toShift) {
                view.setTranslateX(x - size[0] / 2);
                view.setTranslateY(y - size[0] / 2);
            } else {
                view.setX(x);
                view.setY(y);
            }
        } else {
            view.setX(x);
            view.setY(y);
        }
        return view;
    }

    public void displayImage(Image content, double x, double y, Group group, Boolean toShift, Double... size) {
        ImageView view;
        if (size.length != 0) {
            view = getView(content, x, y, toShift, size[0]);
        } else {
            view = getView(content, x, y, toShift);
        }
        group.getChildren().add(view);
    }

    public void displayText(String text, double x, double y, Group group, Font... fonts) {
        Text t = new Text();
        t.setFont(fonts.length == 0 ? textFont : fonts[0]);
        t.setText(text);
        t.setX(x);
        t.setY(y);
        group.getChildren().add(t);
    }

    void initDisplayState(BlueLagoonGame game) {
        // get each tile to add them
        initTiles(game);
        displayPieces(game);
        displayStatus(game);
    }

    void initTiles(BlueLagoonGame game) {
        for (int i = 0; i < game.getBoard().getSize(); i++) {
            for (int j = 0; j < game.getBoard().getSize(); j++) {
                if (game.getBoard().getTileMatrix()[i][j] != null) {
                    GUIPosition guiPosition = GUIPosition.parseFromBoardPosition(i, j, 32);
                    double x = guiPosition.getX();
                    double y = guiPosition.getY();
                    positionMap.put(guiPosition, new Position(i, j));

                    // get the tile and add to tiles group
                    var tile = game.getBoard().getTileMatrix()[i][j];
                    displayImage(tileImageMap.get(tile.getType()), x, y, tiles, true, 54.0);
                }
            }
        }

        // display the bonus score of each island for DEFAULT map
        for (int i = 0; i < defaultMapIslandScore.length; i++) {
            var text = String.valueOf(defaultMapIslandScore[i]);
            var x = defaultMapIslandScorePosition[i].getX();
            var y = defaultMapIslandScorePosition[i].getY();
            displayText(text, x, y, tiles);
        }
        // display the score pointer for island score
        var view = getView(new Image(getClass().getResourceAsStream("images/scorePointer.png")),
                280, 50, false);
        tiles.getChildren().add(view);
    }

    void displayPieces(BlueLagoonGame game) {
        pieces.getChildren().clear();
        for (int i = 0; i < game.getBoard().getSize(); i++) {
            for (int j = 0; j < game.getBoard().getSize(); j++) {
                if (game.getBoard().getTileMatrix()[i][j] != null) {
                    double x = GUIPosition.parseFromBoardPosition(i, j, 32).getX();
                    double y = GUIPosition.parseFromBoardPosition(i, j, 32).getY();
                    // if there is a piece on the tile, add them
                    if (game.getBoard().getTileMatrix()[i][j].isOccupied()) {
                        Piece piece = game.getBoard().getTileMatrix()[i][j].getOccupier();
                        Image image;
                        if (Piece.PieceType.getPlayerPieceType().contains(piece.getPieceType())) {
                            image = pieceImageMap.get(piece.getPieceType()).get(piece.getColour().getId());
                        } else {
                            image = pieceImageMap.get(piece.getPieceType()).get(0);
                        }
                        displayImage(image, x, y, pieces, true, 44.0);
                    }
                }
            }
        }
    }

    void displayStatus(BlueLagoonGame game) {
        status.getChildren().clear();
        // display phase info
        displayText(game.getPhase().toString(), 725, 42, status, phaseTitleFont);

        // display the pointer to the current player
        displayImage(pointerImage, pointerDisplayGUIX, pointerDisplayGUIY[game.getPlayerTurn()], status, false);
        // display all the information about the player
        for (Player player : game.getPlayers()) {
            StringBuilder sb;
            int id = player.getId();

            // display player id and score
            sb = new StringBuilder();
            sb.append("P");
            sb.append(player.getId() + 1);
            sb.append("\tScores: ");
            sb.append(player.getTotalScore());
            displayText(sb.toString(), playerScoreDisplayGUIX, playerScoreDisplayGUIY[id], status);

            // display pieces and remained number for the player
            displayImage(pieceImageMap.get(Piece.PieceType.SETTLER).get(id),
                    pieceDisplayGUIX, settlerDisplayGUIY[id], status, false, 35.0);
            var text = Integer.toString(player.getRemainedPieceNum(Piece.PieceType.SETTLER));
            displayText(text, pieceRemainedNumDisplayX, settlerRemainedNumDisplayY[id], status);
            // village pieces only appear in exploration phase
            if (game.getPhase() == BlueLagoonGame.GamePhase.EXPLORATION_PHASE) {
                displayImage(pieceImageMap.get(Piece.PieceType.VILLAGE).get(id),
                        pieceDisplayGUIX, villageDisplayGUIY[id], status, false, 35.0);
                text = Integer.toString(player.getRemainedPieceNum(Piece.PieceType.VILLAGE));
                displayText(text, pieceRemainedNumDisplayX, villageRemainedNumDisplayY[id], status);
            }

            // display resource type

            text = "bamboo(s)\ncoconut(s)\nprecious stone(s)\nwater\nstatuette(s)";
            displayText(text, resourceTypeDisplayGUIX, resourceTypeDisplayGUIY[id], status);

            // display the resource numbers
            sb = new StringBuilder();
            sb.append(player.getResourceAndStatuetteNumMap().get(Piece.PieceType.BAMBOO));
            sb.append("\n");
            sb.append(player.getResourceAndStatuetteNumMap().get(Piece.PieceType.COCONUT));
            sb.append("\n");
            sb.append(player.getResourceAndStatuetteNumMap().get(Piece.PieceType.PRECIOUS_STONE));
            sb.append("\n");
            sb.append(player.getResourceAndStatuetteNumMap().get(Piece.PieceType.WATER));
            sb.append("\n");
            sb.append(player.getResourceAndStatuetteNumMap().get(Piece.PieceType.STATUETTE));
            text = sb.toString();
            displayText(text, resourceNumDisplayGUIX, resourceNumDisplayGUIY[id], status);

            // display RESTART button
            Button restartButton = new Button();
            restartButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("images/RESTART.png"))));
            restartButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;");
            restartButton.setLayoutX(1100);
            restartButton.setLayoutY(650);
            restartButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    welcomeBoard();
                }
            });
            status.getChildren().add(restartButton);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(this.root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Blue Lagoon Game");
        welcomeBoard();
        root.getChildren().add(controls);
        stage.setScene(scene);
        stage.show();
    }

    private void welcomeBoard() {
        root.getChildren().clear();
        Image image = new Image(getClass().getResourceAsStream("images/WelcomeBoard.png"));
        ImageView imageView = new ImageView(image);
        Image buttonImg = new Image(getClass().getResourceAsStream("images/ButtonDefault.png"));
        Button defaultGameButton = new Button();
        defaultGameButton.setGraphic(new ImageView(buttonImg));
        defaultGameButton.setLayoutX(120);
        defaultGameButton.setLayoutY(420);
        defaultGameButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;");
        // default button to satrt a default game
        defaultGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startGame(BlueLagoonGame.DEFAULT_GAME);
            }
        });

        Button customGameButton = new Button();
        Image customImg = new Image(getClass().getResourceAsStream("images/ButtonCustom.png"));
        customGameButton.setGraphic(new ImageView(customImg));
        customGameButton.setLayoutX(664);
        customGameButton.setLayoutY(420);
        customGameButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-cursor: hand;");
        // enable options to play 3 and 4 player game
        customGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<String> choices = new ArrayList<>();
                // add options
                choices.add("3");
                choices.add("4");
                // create a dialog
                ChoiceDialog<String> dialog = new ChoiceDialog<>("3", choices);
                dialog.setTitle("Choose player numbers");
                dialog.setHeaderText("Please choose the number of players");
                dialog.setContentText("Num: ");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String num = result.get();
                    StringBuilder gameString = new StringBuilder(BlueLagoonGame.DEFAULT_GAME);
                    gameString.setCharAt(5, num.charAt(0));
                    startGame(gameString.toString());
                }
            }
        });

        root.getChildren().add(imageView);
        root.getChildren().add(defaultGameButton);
        root.getChildren().add(customGameButton);
    }

    private void startGame(String gameString) {
        root.getChildren().clear();
        controls.getChildren().clear();
        root.getChildren().add(controls);
        controls.getChildren().add(tiles);
        controls.getChildren().add(pieces);
        controls.getChildren().add(status);
        controls.getChildren().add(scores);
        controls.getChildren().add(settlerMoveHints);
        controls.getChildren().add(villageMoveHints);
        controls.getChildren().add(moveHintsHover);
        controls.getChildren().add(playerOperation);
        BlueLagoonGame game = new BlueLagoonGame(gameString);
        game.distributeResources();
        initDisplayState(game);

        // display guide for player
        var view = getView(new Image(getClass().getResourceAsStream("images/GUIDE.png")),
                500, 80, false);
        controls.getChildren().add(guideBeforeGame);
        guideBeforeGame.getChildren().add(view);

        // start display for player to operate
        displayPlayerOperation(game);
    }

    private void displayPlayerOperation(BlueLagoonGame game) {
        // display draggable pieces
        double size = 35.0;
        var id = game.getPlayerTurn();
        var settlerImageView = getView(pieceImageMap.get(Piece.PieceType.SETTLER).get(id),
                pieceDisplayGUIX, settlerDisplayGUIY[id], false, size);
        settlerImageView.setStyle("-fx-cursor: hand;");
        var villageImageView = getView(pieceImageMap.get(Piece.PieceType.VILLAGE).get(id),
                pieceDisplayGUIX, villageDisplayGUIY[id], false, size);

        // define the nearest GUIPosition to place piece
        final GUIPosition[] nearestGuiPos = new GUIPosition[1];
        HashMap<GUIPosition, Position> settlerHintPositionMap = new HashMap<>();
        HashMap<GUIPosition, Position> villageHintPositionMap = new HashMap<>();

        // clear the hints
        settlerMoveHints.getChildren().clear();
        villageMoveHints.getChildren().clear();
        // generate all valid moves for settlers
        game.generateValidMoves(game.getCurrentPlayer());
        for (Position position : game.getCurrentPlayer().getValidMoves().get(Piece.PieceType.SETTLER)) {
            Circle circle = new Circle();
            circle.setRadius(6);
            circle.setFill(Color.MEDIUMVIOLETRED);
            GUIPosition guiPos = GUIPosition.parseFromBoardPosition(position.getX(), position.getY(), 32);
            circle.setCenterX(guiPos.getX());
            circle.setCenterY(guiPos.getY());
            settlerMoveHints.getChildren().add(circle);
            settlerHintPositionMap.put(guiPos, position);
        }
        settlerMoveHints.setVisible(false);
        // generate all valid moves for villages
        for (Position position : game.getCurrentPlayer().getValidMoves().get(Piece.PieceType.VILLAGE)) {
            Circle circle = new Circle();
            circle.setRadius(6);
            circle.setFill(Color.MEDIUMVIOLETRED);
            GUIPosition guiPos = GUIPosition.parseFromBoardPosition(position.getX(), position.getY(), 32);
            circle.setCenterX(guiPos.getX());
            circle.setCenterY(guiPos.getY());
            villageMoveHints.getChildren().add(circle);
            villageHintPositionMap.put(guiPos, position);
        }
        villageMoveHints.setVisible(false);

        // create drag event handler
        EventHandler<MouseEvent> dragEventHandler = dragEvent -> {
            // clear the guidance before game
            guideBeforeGame.getChildren().clear();

            ImageView source = (ImageView) dragEvent.getSource();
            Piece.PieceType pieceType;
            if (source.equals(settlerImageView)) {
                pieceType = Piece.PieceType.SETTLER;
            } else {
                pieceType = Piece.PieceType.VILLAGE;
            }
            if (!game.getCurrentPlayer().getValidMoves().get(pieceType).isEmpty()) {
                double mouseX = dragEvent.getSceneX();
                double mouseY = dragEvent.getSceneY();
                int intMouseX = (int) mouseX;
                int intMouseY = (int) mouseY;
                if (source.equals(settlerImageView)) {
                    settlerMoveHints.setVisible(true);
                    settlerImageView.setX(mouseX - 27);
                    settlerImageView.setY(mouseY - 27);
                } else if (source.equals(villageImageView)) {
                    villageMoveHints.setVisible(true);
                    villageImageView.setX(mouseX - 27);
                    villageImageView.setY(mouseY - 27);

                }
                int nearestDistance = Integer.MAX_VALUE;
                if (mouseX < 750) {
                    if (source.equals(settlerImageView)) {
                        for (GUIPosition guiPosition : settlerHintPositionMap.keySet()) {
                            int newDistance = Math.abs(intMouseX - guiPosition.getIntX() - 20) + Math.abs(intMouseY - guiPosition.getIntY() - 20);
                            if (newDistance < nearestDistance) {
                                nearestGuiPos[0] = guiPosition;
                                nearestDistance = newDistance;
                            }
                        }
                    } else {
                        for (GUIPosition guiPosition : villageHintPositionMap.keySet()) {
                            int newDistance = Math.abs(intMouseX - guiPosition.getIntX() - 20) + Math.abs(intMouseY - guiPosition.getIntY() - 20);
                            if (newDistance < nearestDistance) {
                                nearestGuiPos[0] = guiPosition;
                                nearestDistance = newDistance;
                            }
                        }
                    }
                    Circle circle = new Circle();
                    circle.setRadius(6);
                    circle.setFill(Color.YELLOW);
                    circle.setCenterX(nearestGuiPos[0].getX());
                    circle.setCenterY(nearestGuiPos[0].getY());
                    moveHintsHover.getChildren().clear();
                    moveHintsHover.getChildren().add(circle);
                }
                if (mouseX > 750) {
                    moveHintsHover.getChildren().clear();
                }
                if (source.equals(settlerImageView)) {
                    settlerImageView.toFront();
                } else {
                    villageImageView.toFront();
                }
            }
        };

        // create release event handler
        EventHandler<MouseEvent> releaseEventHandler = releaseEvent -> {
            ImageView imageView = (ImageView) releaseEvent.getSource();
            Piece.PieceType pieceType;
            HashMap<GUIPosition, Position> hintMap = new HashMap();
            if (imageView.equals(settlerImageView)) {
                pieceType = Piece.PieceType.SETTLER;
                hintMap = settlerHintPositionMap;
            } else {
                pieceType = Piece.PieceType.VILLAGE;
                hintMap = villageHintPositionMap;
            }

            if (releaseEvent.getSceneX() < 750) {
                // place piece on the board
                if (game.isMoveValid(game.getCurrentPlayer(), pieceType, hintMap.get(nearestGuiPos[0]))) {
                    game.placePieces(game.getCurrentPlayer(), pieceType, hintMap.get(nearestGuiPos[0]));
                    // if the phase is over, display scores
                    if (game.isPhaseOver()) {
                        game.calculateScore();
                        game.applyUpdateScore();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Phase over");
                        alert.setHeaderText("Settlement phase is over!");
                        StringBuilder sb = new StringBuilder();
                        for (Player player : game.getPlayers()) {
                            sb.append("Player ");
                            sb.append(Integer.toString(player.getId() + 1));
                            sb.append("'s score:\n");
                            sb.append(player.getCalculateScore().toString());
                        }
                        alert.setContentText(sb.toString());
                        alert.showAndWait();
                        game.phaseOver();
                        //distribute resources again
                        if (game.getPhase() == BlueLagoonGame.GamePhase.EXPLORATION_PHASE) {
                            game.distributeResources();
                            game.setPhase(BlueLagoonGame.GamePhase.SETTLEMENT_PHASE);
                        } else {
                            // if the game is over, show the winner
                            String message = game.getGameResultString();
                            Alert result = new Alert(Alert.AlertType.INFORMATION);
                            result.setTitle("Game over");
                            result.setHeaderText(message);
                            result.showAndWait();
                            welcomeBoard();
                        }
                    }
                    // update game phase and turn
                    game.updatePlayerTurnAndPhase();
                }
            }
            settlerMoveHints.getChildren().clear();
            villageMoveHints.getChildren().clear();
            moveHintsHover.getChildren().clear();
            playerOperation.getChildren().clear();
            displayPieces(game);
            displayStatus(game);
            displayPlayerOperation(game);
        };

        // combine the events with the pieces
        settlerImageView.setOnMouseDragged(dragEventHandler);
        settlerImageView.setOnMouseReleased(releaseEventHandler);
        playerOperation.getChildren().add(settlerImageView);
        villageImageView.setOnMouseDragged(dragEventHandler);
        villageImageView.setOnMouseReleased(releaseEventHandler);
        if (game.getPhase() == BlueLagoonGame.GamePhase.SETTLEMENT_PHASE) {
            villageImageView.setVisible(false);
        }
        playerOperation.getChildren().add(villageImageView);
    }
}

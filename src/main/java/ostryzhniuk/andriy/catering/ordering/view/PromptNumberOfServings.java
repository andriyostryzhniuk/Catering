package ostryzhniuk.andriy.catering.ordering.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import ostryzhniuk.andriy.catering.overridden.elements.number.spinner.NumberSpinner;
import java.util.Timer;
import java.util.TimerTask;

public class PromptNumberOfServings {
    private boolean isException = false;
    private Integer numberOfServings = 0;
    private String nameOfDish;
    private Integer currentValue = 0;

    public PromptNumberOfServings(String nameOfDish, Integer currentValue) {
        this.nameOfDish = nameOfDish;
        this.currentValue = currentValue;
    }

    public int showPrompt(Window window) {
        Stage primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Group root = new Group();
        Scene scene = new Scene(root, 320, 162, Color.rgb(0, 0, 0, 0));

        Rectangle background = initRectangle();

        VBox vBox = new VBox(2);
        vBox.setPadding(new Insets(90, 0, 0, 40));
        HBox hBox = new HBox(3);

        Label exceptionLabel = new Label("Значення неприпустиме");
        exceptionLabel.setStyle("-fx-text-fill: red");

        NumberSpinner numberSpinner = initNumberSpinner();

        Button okButton = new Button("Прийняти");
        okButton.setOnAction((ActionEvent event) -> {
            if (!isException) {
                numberOfServings = numberSpinner.getValue().intValue();
                primaryStage.close();
            } else {
                vBox.getChildren().add(exceptionLabel);
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            vBox.getChildren().remove(exceptionLabel);
                        });
                    }
                }, 1000, 5000);
            }
        });

        Button cancelButton = new Button("Скасувати");
        cancelButton.setOnAction((ActionEvent event) -> {
            primaryStage.close();
        });

        Pane labelPane = initLabelPane();

        hBox.getChildren().addAll(numberSpinner, okButton, cancelButton);
        hBox.setMargin(numberSpinner, new Insets(0, 3, 0, 0));
        vBox.getChildren().add(hBox);
        root.getChildren().addAll(background, labelPane, vBox);

        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(window);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();

        return numberOfServings;
    }

    private NumberSpinner initNumberSpinner() {
        NumberSpinner numberSpinner = new NumberSpinner();
        numberSpinner.setPrefWidth(80);
        numberSpinner.setValue(currentValue);
        numberSpinner.setMinValue(0);
        numberSpinner.setMaxValue(10000);
        numberSpinner.valueProperty().addListener(ns -> {
//                change detected
            isException = false;
            try {
                if (numberSpinner.getValue().intValue() < 1) {
                    isException = true;
                }
            } catch (NullPointerException e) {
                isException = true;
            }
        });
        return numberSpinner;
    }

    private Rectangle initRectangle (){
        Color foreground = Color.rgb(255, 255, 255, 0.9);
        Rectangle background = new Rectangle(320, 162);
        background.setX(0);
        background.setY(0);
        background.setArcHeight(15);
        background.setArcWidth(15);
        background.setFill(Color.rgb(0, 0, 0, 0.55));
        background.setStroke(foreground);
        background.setStrokeWidth(1.5);
        return background;
    }

    private Pane initLabelPane(){
        String nameOfDish = this.nameOfDish;
        if (nameOfDish.length() > 40) {
            nameOfDish = nameOfDish.replace(nameOfDish, nameOfDish.substring(0, 40)+"...");
        }
        Label labelStock = new Label("Страва:\n" + nameOfDish);

        Label labelNumberOfServings = new Label("Кількість порцій:");

        labelStock.setStyle("-fx-text-fill: white");
        labelNumberOfServings.setStyle("-fx-text-fill: white");

        Pane labelPane = new Pane();
        labelPane.getChildren().addAll(labelStock, labelNumberOfServings);

        labelStock.setLayoutX(20);
        labelStock.setLayoutY(10);

        labelNumberOfServings.setLayoutX(40);
        labelNumberOfServings.setLayoutY(65);
        return labelPane;
    }
}

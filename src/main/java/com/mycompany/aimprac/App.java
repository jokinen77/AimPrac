package com.mycompany.aimprac;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    
    private double titleBarHeight = 42;
    
    private Insets configMenuInsets =  new Insets(10);
    private double configMenuItemWidth = 160;
    private Insets configMenuItemInsets = new Insets(10, 0, 0, 0);
    
    private int canvasWidth = 1200;
    private int canvasHeight = 720;
    private String mode = "close_dots";
    private double dotRadius = 24;
    private long dotVisibilityMillis = 1200;
    private int multiDotCount = 2;
    private long addDotDelayMillis = 200;
    private int addDots = 50;
    private int feedbackSoundGainLevel = 4;

    @Override
    public void start(Stage stage) {
        BorderPane rootPane = new BorderPane();
        rootPane.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 60), CornerRadii.EMPTY, Insets.EMPTY)));

        AimCanvas canvas = new AimCanvas(canvasWidth, canvasHeight, mode, dotRadius, addDots, dotVisibilityMillis, multiDotCount, addDotDelayMillis, feedbackSoundGainLevel*16-80);
        canvas.start();

        Button resetButton = new Button("Restart");
        resetButton.setPrefWidth(configMenuItemWidth);
        resetButton.setOnAction((event) -> {
            canvas.reset();
        });
        
        
        Label modeLabel = new Label("Mode:");
        modeLabel.setPadding(configMenuItemInsets);
        modeLabel.setTextFill(Color.WHITE);
        
        ComboBox modeSelection = new ComboBox();
        modeSelection.setPrefWidth(configMenuItemWidth);
        modeSelection.getItems().add("random_dots");
        modeSelection.getItems().add("close_dots");
        modeSelection.setValue(mode);
        modeSelection.valueProperty().addListener((event) -> {
            canvas.setMode(modeSelection.getValue().toString());
        });

        Label dotRadiusLabel = new Label("Radius:");
        dotRadiusLabel.setPadding(configMenuItemInsets);
        dotRadiusLabel.setTextFill(Color.WHITE);

        Slider dotRadiusSlider = new Slider(12, 48, dotRadius);
        dotRadiusSlider.setPrefWidth(configMenuItemWidth);
        dotRadiusSlider.setMajorTickUnit(6);
        dotRadiusSlider.setMinorTickCount(0);
        dotRadiusSlider.setShowTickLabels(true);
        dotRadiusSlider.setSnapToTicks(true);
        dotRadiusSlider.valueProperty().addListener((event) -> {
            canvas.setDotRadius(dotRadiusSlider.getValue());
        });

        Label dotCountLabel = new Label("Dots:");
        dotCountLabel.setPadding(configMenuItemInsets);
        dotCountLabel.setTextFill(Color.WHITE);

        Slider dotCountSlider = new Slider(25, 100, addDots);
        dotCountSlider.setPrefWidth(configMenuItemWidth);
        dotCountSlider.setMajorTickUnit(25);
        dotCountSlider.setMinorTickCount(0);
        dotCountSlider.setShowTickLabels(true);
        dotCountSlider.setSnapToTicks(true);
        dotCountSlider.valueProperty().addListener((event) -> {
            canvas.setAddDots((int) dotCountSlider.getValue());
        });

        Label multiDotLabel = new Label("Multiple targets:");
        multiDotLabel.setPadding(configMenuItemInsets);
        multiDotLabel.setTextFill(Color.WHITE);

        Slider multiDotSlider = new Slider(1, 5, multiDotCount);
        multiDotSlider.setPrefWidth(configMenuItemWidth);
        multiDotSlider.setMajorTickUnit(1);
        multiDotSlider.setMinorTickCount(0);
        multiDotSlider.setShowTickLabels(true);
        multiDotSlider.setSnapToTicks(true);
        multiDotSlider.valueProperty().addListener((event) -> {
            canvas.setMultiDotCount((int) multiDotSlider.getValue());
        });

        Label visibilityTimeLabel = new Label("Visibility millis:");
        visibilityTimeLabel.setPadding(configMenuItemInsets);
        visibilityTimeLabel.setTextFill(Color.WHITE);

        Slider visibilityTimeSlider = new Slider(400, 2400, dotVisibilityMillis);
        visibilityTimeSlider.setPrefWidth(configMenuItemWidth);
        visibilityTimeSlider.setMajorTickUnit(400);
        visibilityTimeSlider.setMinorTickCount(0);
        visibilityTimeSlider.setShowTickLabels(true);
        visibilityTimeSlider.setSnapToTicks(true);
        visibilityTimeSlider.valueProperty().addListener((event) -> {
            canvas.setDotVisibilityMillis((long) visibilityTimeSlider.getValue());
        });

        Label addDotDelayLabel = new Label("Add dot delay millis:");
        addDotDelayLabel.setPadding(configMenuItemInsets);
        addDotDelayLabel.setTextFill(Color.WHITE);

        Slider addDotDelaySlider = new Slider(0, 500, addDotDelayMillis);
        addDotDelaySlider.setPrefWidth(configMenuItemWidth);
        addDotDelaySlider.setMajorTickUnit(100);
        addDotDelaySlider.setMinorTickCount(0);
        addDotDelaySlider.setShowTickLabels(true);
        addDotDelaySlider.setSnapToTicks(true);
        addDotDelaySlider.valueProperty().addListener((event) -> {
            canvas.setAddDotDelayMillis((long) addDotDelaySlider.getValue());
        });
        
        Label feedbackVolumeLabel = new Label("Feedback volume:");
        feedbackVolumeLabel.setPadding(configMenuItemInsets);
        feedbackVolumeLabel.setTextFill(Color.WHITE);

        Slider feedbackVolumeSlider = new Slider(0, 5, feedbackSoundGainLevel);
        feedbackVolumeSlider.setPrefWidth(configMenuItemWidth);
        feedbackVolumeSlider.setMajorTickUnit(1);
        feedbackVolumeSlider.setMinorTickCount(0);
        feedbackVolumeSlider.setShowTickLabels(true);
        feedbackVolumeSlider.setSnapToTicks(true);
        feedbackVolumeSlider.valueProperty().addListener((event) -> {
            canvas.setPlaySounds((int) feedbackVolumeSlider.getValue()*16-80);
        });

        CheckBox customSize = new CheckBox("Custom size");
        customSize.setPadding(configMenuItemInsets);
        customSize.setTextFill(Color.WHITE);
        customSize.setSelected(true);

        Label widthSelectionLabel = new Label("Width:");
        widthSelectionLabel.setPadding(configMenuItemInsets);
        widthSelectionLabel.setTextFill(Color.WHITE);

        ComboBox widthSelection = new ComboBox();
        widthSelection.setDisable(customSize.isSelected());
        widthSelection.setPrefWidth(configMenuItemWidth);
        widthSelection.getItems().add("720");
        widthSelection.getItems().add("960");
        widthSelection.getItems().add("1200");
        widthSelection.getItems().add("1440");
        widthSelection.getItems().add("1680");
        widthSelection.getItems().add("1920");
        widthSelection.setValue(canvasWidth);

        Label heightSelectionLabel = new Label("Height:");
        heightSelectionLabel.setPadding(configMenuItemInsets);
        heightSelectionLabel.setTextFill(Color.WHITE);

        ComboBox heightSelection = new ComboBox();
        heightSelection.setDisable(customSize.isSelected());
        heightSelection.setPrefWidth(configMenuItemWidth);
        heightSelection.getItems().add("600");
        heightSelection.getItems().add("720");
        heightSelection.getItems().add("960");
        heightSelection.getItems().add("1200");
        heightSelection.getItems().add("1440");
        heightSelection.setValue(canvasHeight);
        
        widthSelection.valueProperty().addListener((event) -> {
            initStageSizeFromSelections(stage, canvas, widthSelection, heightSelection);
        });
        heightSelection.valueProperty().addListener((event) -> {
            initStageSizeFromSelections(stage, canvas, widthSelection, heightSelection);
        });
        customSize.selectedProperty().addListener((event) -> {
            if (customSize.isSelected()) {
                stage.setResizable(true);
            } else {
                stage.setResizable(false);
                initStageSizeFromSelections(stage, canvas, widthSelection, heightSelection);
            }
            initStageResizeListeners(stage, canvas);
            widthSelection.setDisable(customSize.isSelected());
            heightSelection.setDisable(customSize.isSelected());
        });
        initStageSizeFromSelections(stage, canvas, widthSelection, heightSelection);
        initStageResizeListeners(stage, canvas);

        VBox configurationMenu = new VBox();
        configurationMenu.setPadding(configMenuInsets);
        configurationMenu.getChildren().addAll(
                resetButton,
                modeLabel,
                modeSelection,
                dotCountLabel,
                dotCountSlider,
                dotRadiusLabel,
                dotRadiusSlider,
                multiDotLabel,
                multiDotSlider,
                visibilityTimeLabel,
                visibilityTimeSlider,
                addDotDelayLabel,
                addDotDelaySlider,
                feedbackVolumeLabel,
                feedbackVolumeSlider,
                customSize,
                widthSelectionLabel,
                widthSelection,
                heightSelectionLabel,
                heightSelection
        );
        //canvas.getView().setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        rootPane.setCenter(canvas.getView());
        rootPane.setLeft(configurationMenu);
        Scene scene = new Scene(rootPane);

        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("AimPrac-1.0");
        stage.show();
    }

    private void initStageResizeListeners(Stage stage, AimCanvas canvas) {
        stage.widthProperty().addListener((event) -> {
            canvas.setWidth(stage.getWidth() - configMenuItemWidth - configMenuInsets.getLeft() - configMenuInsets.getRight() - 10);
        });
        stage.heightProperty().addListener((event) -> {
            canvas.setHeight(stage.getHeight() - titleBarHeight);
        });
    }

    private void initStageSizeFromSelections(Stage stage, AimCanvas canvas, ComboBox widthSelection, ComboBox heightSelection) {
        String width = widthSelection.getValue().toString();
        canvas.setWidth(Integer.parseInt(width));
        stage.setWidth(Integer.parseInt(width) + configMenuItemWidth + configMenuInsets.getLeft() + configMenuInsets.getRight() + 10);
        
        String height = heightSelection.getValue().toString();
        canvas.setHeight(Integer.parseInt(height));
        stage.setHeight(Integer.parseInt(height) + titleBarHeight);
    }

    public static void main(String[] args) {
        launch();
    }
}

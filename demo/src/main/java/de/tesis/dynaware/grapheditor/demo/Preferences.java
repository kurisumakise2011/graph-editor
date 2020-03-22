package de.tesis.dynaware.grapheditor.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Preferences {
    public Button applyButton;
    public Button cancelButton;
    public Slider nodeHeightSlider;
    public Slider nodeWidthSlider;
    public Slider nodeLabelFontSizeSlider;
    public Slider nodeSolidSlider;
    public Slider commandLabelFontSizeSlider;
    public Slider connectorSizeSlider;
    public Slider circleSizeSlider;
    public RadioButton promptNodeRadio;
    public RadioButton displayBordersRadio;
    public AnchorPane preferences;
    public Button okButton;

    @FXML
    public void initialize() {
        nodeHeightSlider.setValue(Property.NODE_HEIGHT.getInt());
        nodeWidthSlider.setValue(Property.NODE_WIDTH.getInt());
        nodeSolidSlider.setValue(Property.NODE_SOLID.getInt());
        circleSizeSlider.setValue(Property.CIRCLE_SIZE.getInt());
        connectorSizeSlider.setValue(Property.CONNECTOR_SIZE.getInt());
        commandLabelFontSizeSlider.setValue(Property.COMMAND_LABEL_FONT_SIZE.getInt());
        nodeLabelFontSizeSlider.setValue(Property.NODE_LABEL_FONT_SIZE.getInt());
        promptNodeRadio.setSelected(Property.PROMPT.is());
        displayBordersRadio.setSelected(Property.DISPLAY_BORDERS_NODES.is());
    }

    public void apply(ActionEvent actionEvent) {
        int value = (int) nodeHeightSlider.getValue();
        Property.NODE_HEIGHT.set(String.valueOf(value));

        value = (int) nodeWidthSlider.getValue();
        Property.NODE_WIDTH.set(String.valueOf(value));

        value = (int) nodeSolidSlider.getValue();
        Property.NODE_SOLID.set(String.valueOf(value));

        value = (int) circleSizeSlider.getValue();
        Property.CIRCLE_SIZE.set(String.valueOf(value));

        value = (int) connectorSizeSlider.getValue();
        Property.CONNECTOR_SIZE.set(String.valueOf(value));

        value = (int) commandLabelFontSizeSlider.getValue();
        Property.COMMAND_LABEL_FONT_SIZE.set(String.valueOf(value));

        value = (int) nodeLabelFontSizeSlider.getValue();
        Property.NODE_LABEL_FONT_SIZE.set(String.valueOf(value));

        Property.PROMPT.set(String.valueOf(promptNodeRadio.isSelected()));
        Property.DISPLAY_BORDERS_NODES.set(String.valueOf(displayBordersRadio.isSelected()));

        applyButton.setDisable(true);
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void ok(ActionEvent actionEvent) {
        apply(actionEvent);
        cancel(actionEvent);
    }
}

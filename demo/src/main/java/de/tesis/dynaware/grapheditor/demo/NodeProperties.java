package de.tesis.dynaware.grapheditor.demo;

import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.model.GConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.eclipse.emf.common.util.EList;

public class NodeProperties {
    public TableView<NodeProperty> propertiesTable;
    public Button okButton;
    public Button cancelButton;
    public Label positionLabel;
    public AnchorPane nodePane;
    public TableColumn<NodeProperty, String> commandColumn;
    public TableColumn<NodeProperty, String> typeColumn;
    public TableColumn<NodeProperty, String> title1Column;
    public TableColumn<NodeProperty, String> title2Column;
    public TableColumn<NodeProperty, String> relationshipColumn;

    private GNodeSkin skin;

    @FXML
    public void initialize() {
        positionLabel.setText(new NodeGeometryWrapper(skin.getItem()).render());
        EList<GConnector> connectors = skin.getItem().getConnectors();

        commandColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        title1Column.setCellValueFactory(new PropertyValueFactory<>("title1"));
        title2Column.setCellValueFactory(new PropertyValueFactory<>("title2"));
        relationshipColumn.setCellValueFactory(new PropertyValueFactory<>("relationship"));

        if (connectors == null) {
            return;
        }

        for (GConnector connector : connectors) {
            propertiesTable.getItems().add(new NodeProperty(
                    connector.getId(),
                    connector.getType(),
                    String.valueOf(connector.getX()),
                    String.valueOf(connector.getY()),
                    connector.getConnections().size() == 0 ? "no" : String.valueOf(connector.getConnections().size())
            ));
        }
    }


    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void ok(ActionEvent actionEvent) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void setSkin(GNodeSkin skin) {
        this.skin = skin;
    }
}

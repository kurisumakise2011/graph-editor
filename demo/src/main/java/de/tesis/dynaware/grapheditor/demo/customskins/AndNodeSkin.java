package de.tesis.dynaware.grapheditor.demo.customskins;

import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.core.connectors.DefaultConnectorTypes;
import de.tesis.dynaware.grapheditor.demo.GraphEditorDemo;
import de.tesis.dynaware.grapheditor.demo.GraphEditorException;
import de.tesis.dynaware.grapheditor.demo.NodeProperties;
import de.tesis.dynaware.grapheditor.demo.Property;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AndNodeSkin extends GNodeSkin {
    public static final String AND_NODE_TYPE = "and-node";
    private static final Logger LOGGER = LoggerFactory.getLogger(AndNodeSkin.class);
    private static int counter = 0;
    private static ImagePattern ampersand;

    static {
        try (InputStream is = AndNodeSkin.class.getResourceAsStream("/de/tesis/dynaware/grapheditor/demo/ampersand.png")) {
            ampersand = new ImagePattern(new Image(is, 192.0, 192.0, false, true));
        } catch (IOException e) {
            throw new GraphEditorException("could not add a node", e);
        }

    }

    private static final String STYLE_CLASS_BORDER = "and-node-border";
    private static final String STYLE_CLASS_BACKGROUND = "and-node-background";
    private static final String STYLE_CLASS_SELECTION_HALO = "and-node-selection-halo";

    private static final double HALO_OFFSET = 5;
    private static final double HALO_CORNER_SIZE = 10;
    private static final double MINOR_POSITIVE_OFFSET = 2;
    private static final double MINOR_NEGATIVE_OFFSET = -3;

    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    private final Rectangle selectionHalo = new Rectangle();

    private final List<GConnectorSkin> inputs = new ArrayList<>();
    private final List<GConnectorSkin> outputs = new ArrayList<>();

    private int ordNum = ++counter;
    private int firstCmdNum;

    // Border and background are separated into 2 rectangles so they can have different effects applied to them.
    private final StackPane pane = new StackPane();
    private final Rectangle border = new Rectangle();
    private final Rectangle background = new Rectangle();
    private final Text cmd = new Text();
    private final Text ord = new Text();

    /**
     * Creates a new {@link GNodeSkin}.
     *
     * @param node the {@link GNode} represented by the skin
     */
    public AndNodeSkin(GNode node) {
        super(node);
        performChecks();

        node.setType(AND_NODE_TYPE);

        background.widthProperty().bind(border.widthProperty().subtract(border.strokeWidthProperty().multiply(2)));
        background.heightProperty().bind(border.heightProperty().subtract(border.strokeWidthProperty().multiply(2)));

        border.widthProperty().bind(getRoot().widthProperty());
        border.heightProperty().bind(getRoot().heightProperty());

        border.getStyleClass().setAll(STYLE_CLASS_BORDER);
        pane.getStyleClass().setAll(STYLE_CLASS_BACKGROUND);

        getRoot().getChildren().addAll(border, pane);

        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::filterMouseDragged);
        pane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass()
                            .getResource("/de/tesis/dynaware/grapheditor/demo/NodeProperties.fxml"));
                    NodeProperties controller = new NodeProperties();
                    controller.setSkin(this);

                    loader.setController(controller);
                    Parent root = loader.load();

                    var stage = new Stage();
                    stage.setTitle("Node properties");
                    stage.setScene(new Scene(root,
                            Property.PROPERTIES_NODE_WIDTH.getInt(),
                            Property.PROPERTIES_NODE_HEIGHT.getInt()));
                    stage.setResizable(false);
                    stage.initOwner(GraphEditorDemo.primary);
                    stage.show();
                } catch (IOException e) {
                    LOGGER.error("could nod node properties", e);
                    throw new GraphEditorException(e);
                }
            }
        });
        background.setFill(ampersand);

        pane.getChildren().addAll(background, cmd, ord);

        addSelectionHalo();
    }

    private void performChecks() {
        for (final GConnector connector : getItem().getConnectors()) {
            if (!DefaultConnectorTypes.isValid(connector.getType())) {
                LOGGER.error("Connector type '{}' not recognized, setting to 'left-input'.", connector.getType());
                connector.setType(DefaultConnectorTypes.LEFT_INPUT);
            }
        }
    }

    /**
     * Adds the selection halo and initializes some of its values.
     */
    private void addSelectionHalo() {

        getRoot().getChildren().add(selectionHalo);

        selectionHalo.setManaged(false);
        selectionHalo.setMouseTransparent(false);
        selectionHalo.setVisible(false);

        selectionHalo.setLayoutX(-HALO_OFFSET);
        selectionHalo.setLayoutY(-HALO_OFFSET);

        selectionHalo.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
    }

    @Override
    public void setConnectorSkins(List<GConnectorSkin> connectorSkins) {
        removeAllConnectors();
        inputs.clear();
        outputs.clear();

        if (connectorSkins != null) {
            for (final GConnectorSkin connectorSkin : connectorSkins) {

                final String type = connectorSkin.getItem().getType();

                if (DefaultConnectorTypes.isInput(type)) {
                    inputs.add(connectorSkin);
                } else if (DefaultConnectorTypes.isOutput(type)) {
                    outputs.add(connectorSkin);
                }

                getRoot().getChildren().add(connectorSkin.getRoot());
            }
        }
    }

    /**
     * Removes all connectors from the list of children.
     */
    private void removeAllConnectors() {
        inputs.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        outputs.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
    }


    @Override
    public void layoutConnectors() {
        layoutAllConnectors();
        layoutSelectionHalo();
    }

    /**
     * Lays out the node's connectors.
     */
    private void layoutAllConnectors() {
        layoutConnectors(outputs, true, getRoot().getWidth());
        layoutConnectors(inputs, true, 0);
    }

    /**
     * Lays out the given connector skins in a horizontal or vertical direction at the given offset.
     *
     * @param connectorSkins the skins to lay out
     * @param vertical {@code true} to lay out vertically, {@code false} to lay out horizontally
     * @param offset the offset in the other dimension that the skins are layed out in
     */
    private void layoutConnectors(final List<GConnectorSkin> connectorSkins, final boolean vertical, final double offset) {

        final int count = connectorSkins.size();

        for (int i = 0; i < count; i++) {

            final GConnectorSkin skin = connectorSkins.get(i);
            final Node root = skin.getRoot();

            if (vertical) {

                final double offsetY = getRoot().getHeight() / (count + 1);
                final double offsetX = getMinorOffsetX(skin.getItem());

                root.setLayoutX(GeometryUtils.moveOnPixel(offset - skin.getWidth() / 2 + offsetX));
                root.setLayoutY(GeometryUtils.moveOnPixel((i + 1) * offsetY - skin.getHeight() / 2));

            } else {

                final double offsetX = getRoot().getWidth() / (count + 1);
                final double offsetY = getMinorOffsetY(skin.getItem());

                root.setLayoutX(GeometryUtils.moveOnPixel((i + 1) * offsetX - skin.getWidth() / 2));
                root.setLayoutY(GeometryUtils.moveOnPixel(offset - skin.getHeight() / 2 + offsetY));
            }
        }
    }

    /**
     * Lays out the selection halo based on the current width and height of the node skin region.
     */
    private void layoutSelectionHalo() {
        if (selectionHalo.isVisible()) {

            selectionHalo.setWidth(border.getWidth() + 2 * HALO_OFFSET);
            selectionHalo.setHeight(border.getHeight() + 2 * HALO_OFFSET);

            final double cornerLength = 2 * HALO_CORNER_SIZE;
            final double xGap = border.getWidth() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;
            final double yGap = border.getHeight() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;

            selectionHalo.setStrokeDashOffset(HALO_CORNER_SIZE);
            selectionHalo.getStrokeDashArray().setAll(cornerLength, yGap, cornerLength, xGap);
        }
    }

    /**
     * Gets a minor x-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return an x-offset of a few pixels
     */
    private double getMinorOffsetX(final GConnector connector) {

        final String type = connector.getType();

        if (type.equals(DefaultConnectorTypes.LEFT_INPUT) || type.equals(DefaultConnectorTypes.RIGHT_OUTPUT)) {
            return MINOR_POSITIVE_OFFSET;
        } else {
            return MINOR_NEGATIVE_OFFSET;
        }
    }

    /**
     * Gets a minor y-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return a y-offset of a few pixels
     */
    private double getMinorOffsetY(final GConnector connector) {

        final String type = connector.getType();

        if (type.equals(DefaultConnectorTypes.TOP_INPUT) || type.equals(DefaultConnectorTypes.BOTTOM_OUTPUT)) {
            return MINOR_POSITIVE_OFFSET;
        } else {
            return MINOR_NEGATIVE_OFFSET;
        }
    }

    @Override
    public Point2D getConnectorPosition(GConnectorSkin connectorSkin) {
        final Node connectorRoot = connectorSkin.getRoot();

        final Side side = DefaultConnectorTypes.getSide(connectorSkin.getItem().getType());

        // The following logic is required because the connectors are offset slightly from the node edges.
        final double x, y;
        if (Objects.equals(side, Side.LEFT)) {
            x = 0;
            y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;
        } else if (Objects.equals(side, Side.RIGHT)) {
            x = getRoot().getWidth();
            y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;
        } else if (Objects.equals(side, Side.TOP)) {
            x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
            y = 0;
        } else {
            x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
            y = getRoot().getHeight();
        }

        return new Point2D(x, y);
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        if (isSelected) {
            selectionHalo.setVisible(true);
            layoutSelectionHalo();
            pane.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            getRoot().toFront();
        } else {
            selectionHalo.setVisible(false);
            pane.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
        }
    }

    /**
     * Stops the node being dragged if it isn't selected.
     *
     * @param event a mouse-dragged event on the node
     */
    private void filterMouseDragged(final MouseEvent event) {
        if (event.isPrimaryButtonDown() && !isSelected()) {
            event.consume();
        }
    }
}

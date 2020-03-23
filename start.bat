@echo off
cd demo/target/deploy
java --module-path="libs" --add-modules=javafx.controls,javafx.base,javafx.fxml,javafx.graphics -jar GraphEditorDemo.jar
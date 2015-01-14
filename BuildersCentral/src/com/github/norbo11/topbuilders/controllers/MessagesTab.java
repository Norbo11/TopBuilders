package com.github.norbo11.topbuilders.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import com.github.norbo11.topbuilders.models.Employee;
import com.github.norbo11.topbuilders.models.Message;
import com.github.norbo11.topbuilders.util.SceneHelper;
import com.github.norbo11.topbuilders.util.TabHelper;

public class MessagesTab extends AbstractController {
    public final static String FXML_FILENAME = "MessagesTab.fxml";
    
    @FXML private ResourceBundle resources;
    @FXML public TableView<Message> table;
    @FXML public TableColumn<Message, LocalDateTime> dateCol, timeCol;
    @FXML public TableColumn<Message, String> senderCol, titleCol;
    @FXML public TableColumn<Message, Message> xCol;

    private class DateCell extends TableCell<Message, LocalDateTime> {
        @Override
        protected void updateItem(LocalDateTime item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) setText("");
            else setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }
    
    private class TimeCell extends TableCell<Message, LocalDateTime> {
        @Override
        protected void updateItem(LocalDateTime item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) setText("");
            else setText(item.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
    
    private class ButtonCell extends TableCell<Message, Message> {
        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setText("");
            else {
                Button button = new Button("X");
                button.setOnAction(e -> deleteMessage(item));
                setGraphic(button); //setGraphic allows me to set an actual node instead of text for these cell contents
            }
        }
    }
    
    private void deleteMessage(Message item) {
        Message.deleteMessage(item);
        TabHelper.refreshAllTabs();
    }
    
	@FXML
	public void initialize() {				
		//Distribute column widths
		ReadOnlyDoubleProperty tableWidth = table.widthProperty();
		dateCol.prefWidthProperty().bind(tableWidth.multiply(0.07));
		timeCol.prefWidthProperty().bind(tableWidth.multiply(0.07));
		senderCol.prefWidthProperty().bind(tableWidth.multiply(0.30));
		titleCol.prefWidthProperty().bind(tableWidth.multiply(0.51));
		xCol.prefWidthProperty().bind(tableWidth.multiply(0.05));
		
		//Set custom date/time cell display classes
		dateCol.setCellFactory(column -> new DateCell());
		timeCol.setCellFactory(column -> new TimeCell());
		
		//Set the cell value of each X cell to contain the actual message object
		xCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Message>(data.getValue()));
		
		//Set the custom display class for X button cells
		xCol.setCellFactory(column -> new ButtonCell());
		
	    table.getItems().addAll(Message.getMessagesByRecipient(Employee.getCurrentEmployee()));
	    table.setRowFactory(value -> {
	        TableRow<Message> row = new TableRow<Message>();
	        row.setOnMouseClicked(e -> {
	            if (e.getClickCount() == 2 && !row.isEmpty()) {
	                readMessage(null);
	            }
	        });
	        return row;
	    });
	}
	
	public static void displayMessage(Message message) {
	    //Create new window
	    Stage stage = new Stage();
        stage.setTitle(message.getTitle());
        AbstractScene scene = SceneHelper.changeScene(stage, Employee.getCurrentEmployee().getSettings().isFullscreen(), DisplayMessageScene.FXML_FILENAME);
        
        //Display details
        DisplayMessageScene controller = (DisplayMessageScene) scene.getController();
        controller.setMessage(message);
        controller.update();

    }

    @FXML
	public void newMessage(ActionEvent event) {
	    Stage stage = new Stage();
	    stage.setTitle(resources.getString("messages.new"));
	    SceneHelper.changeScene(stage, Employee.getCurrentEmployee().getSettings().isFullscreen(), NewMessageScene.FXML_FILENAME);
	}
    
    @FXML
    public void readMessage(ActionEvent event) {
        displayMessage(table.getSelectionModel().getSelectedItem());
    }
	
}

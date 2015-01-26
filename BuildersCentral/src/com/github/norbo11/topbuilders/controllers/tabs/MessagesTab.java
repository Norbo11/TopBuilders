package com.github.norbo11.topbuilders.controllers.tabs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import com.github.norbo11.topbuilders.controllers.AbstractController;
import com.github.norbo11.topbuilders.controllers.scenes.NewMessageScene;
import com.github.norbo11.topbuilders.models.Employee;
import com.github.norbo11.topbuilders.models.Message;
import com.github.norbo11.topbuilders.util.Resources;
import com.github.norbo11.topbuilders.util.SceneHelper;
import com.github.norbo11.topbuilders.util.StageHelper;

public class MessagesTab extends AbstractController {
    public final static String FXML_FILENAME = "tabs/MessagesTab.fxml";
    public static Vector<MessagesTab> tabs = new Vector<MessagesTab>();
    
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
                button.setOnAction(e -> item.delete());
                setGraphic(button); //setGraphic allows me to set an actual node instead of text for these cell contents
            }
        }
    }
    
    /* FXML methods */

    @FXML
	public void initialize() {				
		//Set custom date/time cell display classes
		dateCol.setCellFactory(column -> new DateCell());
		timeCol.setCellFactory(column -> new TimeCell());
		
		//Set the cell value of each X cell to contain the actual message object
		xCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Message>(data.getValue()));
		
		//Set the custom display class for X button cells
		xCol.setCellFactory(column -> new ButtonCell());
		
	    table.setRowFactory(value -> {
	        TableRow<Message> row = new TableRow<Message>();
	        row.setOnMouseClicked(e -> {
	            if (e.getClickCount() == 2 && !row.isEmpty()) {
	                readMessage(null);
	            }
	        });
	        return row;
	    });
	    
	    update();
	}

    @FXML
	public void newMessage(ActionEvent event) {
	    Stage stage = StageHelper.createDialogStage(Resources.getResource(resources, "messages.new"));
	    SceneHelper.changeScene(stage, NewMessageScene.FXML_FILENAME);
	}
    
    @FXML
    public void readMessage(ActionEvent event) {
        Message.displayMessage(table.getSelectionModel().getSelectedItem());
    }
    
    
    /* Instance methods */
    
    private void update() {
        table.getItems().clear();
        table.getItems().addAll(Employee.getCurrentEmployee().getMessages());
    }
    
    /* Static methods */
    
    public static void updateAllTabs() {
        for (MessagesTab tab : tabs) {
            tab.update();
        }
    }

    public static Vector<MessagesTab> getTabs() {
        return tabs;
    }
	
}

package com.github.norbo11.topbuilders.util.factories;

import javafx.scene.control.TextArea;
import javafx.scene.control.TreeTableCell;

public class TextAreaTreeTableCell<T> extends TreeTableCell<T, String> {
        
    private TextArea textArea;

 
    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            
            createTextField();
            setText(null);
            setGraphic(textArea);
            textArea.selectAll();
        }
    }
 
    @Override
    public void cancelEdit() {
        super.cancelEdit();
         
        setText((String) getItem());
        setGraphic(null);
    }
     
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
         
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textArea != null) {
                    textArea.setText(getString());
                }
                
                setText(null);
                setGraphic(textArea);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }
     
    private void createTextField() {
        textArea = new TextArea(getString());
        textArea.setPrefHeight(100);
        textArea.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textArea.onKeyPressedProperty().addListener((observable, oldValue, newValue) -> {
            commitEdit(textArea.getText());
        });
    }
     
    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}

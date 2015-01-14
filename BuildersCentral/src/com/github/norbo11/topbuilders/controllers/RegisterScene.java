package com.github.norbo11.topbuilders.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import com.github.norbo11.topbuilders.util.SceneHelper;


public class RegisterScene extends AbstractController {
	public static final String FXML_FILENAME = "RegisterScene.fxml";
        
    @FXML TextField usernameField;
    @FXML TextField passwordField;
    @FXML Text statusText;
    @FXML Button submitButton;
    
    @FXML
    public void activate(ActionEvent event) {
    }
    
    @FXML
    public void back(ActionEvent event) {
        SceneHelper.changeMainScene(LoginScene.FXML_FILENAME, false);
    }

    public static String getAbsoluteFxmlFilename() {
        return "/com/github/norbo11/topbuilders/scenes/fxml/" + FXML_FILENAME;
    }
}

package com.github.norbo11.topbuilders.controllers.tabs;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.github.norbo11.topbuilders.controllers.AbstractController;
import com.github.norbo11.topbuilders.controllers.scenes.AbstractScene;
import com.github.norbo11.topbuilders.controllers.scenes.ModifyEmployeeScene;
import com.github.norbo11.topbuilders.models.Employee;
import com.github.norbo11.topbuilders.util.GoogleMaps;
import com.github.norbo11.topbuilders.util.Resources;
import com.github.norbo11.topbuilders.util.SceneHelper;
import com.github.norbo11.topbuilders.util.StageHelper;

public class EmployeesTab extends AbstractController {
    public final static String FXML_FILENAME = "tabs/EmployeesTab.fxml";
    
    //Makes all addresses become hyperlinks which open up Google Maps
    private class EmployeeAddressCell extends TreeTableCell<Employee, String> {
        @Override
        protected void updateItem(String address, boolean empty) {
            super.updateItem(address, empty);
            if (empty) {
                setText("");
                setGraphic(null);
            }
            else {                
                Hyperlink link = new Hyperlink(address);
                link.setOnAction(e -> GoogleMaps.openMap(address));
                
                getStyleClass().add("address");
                setGraphic(link);
            }
        }
    }
    
    //Prevents the a default wage of 0.0 from showing up on dummy employees (which serve as category labels
    public class EmployeeDefaultWageFactory implements Callback<CellDataFeatures<Employee, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(CellDataFeatures<Employee, String> param) {
            Employee employee = param.getValue().getValue();
            
            if (employee.isDummy()) return new ReadOnlyStringWrapper("");
            else return employee.defaultWageProperty().asString();
        }
    }
    
    
    @FXML private TreeTableView<Employee> table;
    @FXML private TreeItem<Employee> superusers, managers, employees;
    @FXML private TreeTableColumn<Employee, String> defaultWageCol, deleteCol, addressCol;
    
    @FXML
	public void initialize() {	    
        defaultWageCol.setCellValueFactory(new EmployeeDefaultWageFactory());
        addressCol.setCellFactory(column -> new EmployeeAddressCell());
        
        //Allow double clicking to modify employee
        table.setRowFactory(value -> {
            TreeTableRow<Employee> row = new TreeTableRow<Employee>() {
            	@Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    
                    //TODO broken
                    if (employee != null && employee.isDummy()) getStyleClass().add("heading");
                    else getStyleClass().remove("heading");
                }
            };
            
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    modifyEmployee(null);
                }
            });
            
            return row;
        });
                
    	update(); 
	}

    @FXML
    public void deleteEmployee(ActionEvent event) {
        Employee employee = table.getSelectionModel().getSelectedItem().getValue();
        if (!employee.isDummy()) {
            SceneHelper.showConfirmationDialog(Resources.getResource("general.confirm"), Resources.getResource("employees.delete.confirm", employee.getFullName()), () -> employee.delete());
        }
    }
    
    @FXML
    public void addEmployee(ActionEvent event) {
        //Create new window
        Stage stage = StageHelper.createDialogStage(Resources.getResource("employees.add"));
        AbstractScene scene = SceneHelper.changeScene(stage, ModifyEmployeeScene.FXML_FILENAME);
        
        //Display details
        ModifyEmployeeScene controller = (ModifyEmployeeScene) scene.getController();
        controller.setEmployee(new Employee(), true);
        controller.update();
    }
    
    @FXML
    public void modifyEmployee(ActionEvent event) {
        Employee employee = table.getSelectionModel().getSelectedItem().getValue();
        
        if (!employee.isDummy()) {
            //Create new window
            Stage stage = StageHelper.createDialogStage(employee.getFullName());
            AbstractScene scene = SceneHelper.changeScene(stage, ModifyEmployeeScene.FXML_FILENAME);
            
            //Display details
            ModifyEmployeeScene controller = (ModifyEmployeeScene) scene.getController();
            controller.setEmployee(employee, false);
            controller.update();
        }
    }
    
    @Override
    public void update() {
        superusers.getChildren().clear();
        managers.getChildren().clear();
        employees.getChildren().clear();
        
        for (Employee employee : Employee.getAllEmployees()) {
            TreeItem<Employee> item = new TreeItem<Employee>(employee);
                        
            switch (employee.getUserType()) {
            case SUPERUSER: superusers.getChildren().add(item); break;
            case MANAGER: managers.getChildren().add(item); break;
            case EMPLOYEE: employees.getChildren().add(item); break;
            }
        }
    }
}
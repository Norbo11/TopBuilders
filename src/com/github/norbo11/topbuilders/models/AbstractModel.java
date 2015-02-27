package com.github.norbo11.topbuilders.models;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.github.norbo11.topbuilders.util.Database;
import com.github.norbo11.topbuilders.util.Log;
import com.github.norbo11.topbuilders.util.helpers.StringUtil;


public abstract class AbstractModel {
    private IntegerProperty id;
    private boolean dummy;
    private boolean newModel;
    
    public AbstractModel() {
        this(0);
    }
    
    public AbstractModel(int id) {
        this.id = new SimpleIntegerProperty(id);
    }
    
    /* Properties */
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    /* Getters and setters */
    
    public int getId() {
        return id.get();
    }
    
    public boolean isNewModel() {
		return newModel;
	}

	public void setNewModel(boolean newModel) {
		this.newModel = newModel;
	}

	public void setId(int id) {
        this.id.set(id);
    }
        
    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
    
    /* Abstract methods */
    
    public abstract int add();
    
    public abstract void update();
    
    //Sets all of the properties for this model as obtained by the ResultSet - will only load the specified columns
    public abstract void loadFromResult(ResultSet result, String... columns) throws SQLException;
    
    public abstract String getDbTableName();
            
    /* Instance methods */
    
    public void save() {
        //Add or update the model accordingly
        if (newModel) {
            setId(add());
            newModel = false;
        } else {
            update();
        }
    }

    public void delete() {
        //Delete model if it isn't new
        if (!newModel) {
            Database.executeUpdate("DELETE FROM " + getDbTableName() + " WHERE id = ?", getId());
            Notification.deleteCorrespondingNotification(this);
        }
    }
    
    //This is called by loadFromResult - if no columns were specified, then an empty array will be passed here, for which we check and return true (because that
    //means all columns were requested
    public boolean containsColumn(String[] columns, String needle) {
        if (columns.length == 0) return true;
        
        for (String column : columns) {
            if (column.equals(needle)) return true;
        }
        
        return false;
    }

    //Load specified columns
    public void loadFromId(int id, String... columns) {
        String columnString = columns.length > 0 ? StringUtil.join(columns, ",") : "*";
        ResultSet result = Database.executeQuery("SELECT " + columnString + " FROM " + getDbTableName() + " WHERE id = ?", id);
        
        try {
            if (result.next()) {
                loadFromResult(result, columns);
            }
        } catch (SQLException e) {
            Log.error(e);
        }
    }
    
    /* Static methods */
	
    protected static ResultSet loadAllModels(final String DB_TABLE_NAME) {
    	return Database.executeQuery("SELECT * FROM " + DB_TABLE_NAME);
    }
    
    protected static ResultSet loadAllModelsWhereOrdered(final String DB_TABLE_NAME, String field, Object param, String sortField, boolean desc) {
        return Database.executeQuery("SELECT * FROM " + DB_TABLE_NAME + " WHERE " + field + " = ? ORDER BY " + sortField + " " + (desc ? "DESC" : "ASC"), param);
    }
    
    protected static ResultSet loadAllModelsWhere(final String DB_TABLE_NAME, String field, Object param) {
        return loadAllModelsWhere(DB_TABLE_NAME, new String[] { field }, new Object[] { param });
    }
    
    protected static ResultSet loadAllModelsWhere(final String DB_TABLE_NAME, String[] field, Object[] param) {
        if (field.length != param.length) throw new IllegalArgumentException();
        
        int noOfParams = field.length;
        String query = "SELECT * FROM " + DB_TABLE_NAME + " WHERE ";
        
        for (int i = 0; i < noOfParams; i++) {
            query += field[i] + "=?";
            if (i != noOfParams - 1) query += " AND "; //Add this at the end of each WHERE clause, except the last one
        }
        
        return Database.executeQuery(query, param);
    }
    
	@SuppressWarnings("unchecked")
	protected static <T extends AbstractModel> ObservableList<T> loadList(ResultSet result, Class<T> clazz) {
		ObservableList<T> models = FXCollections.observableArrayList();
        		
        try {
			while (result.next()) {
				T model = clazz.newInstance();
				ObservableList<T> modelList = null;
				
				try {
					modelList = (ObservableList<T>) clazz.getMethod("getModels").invoke(null); //Invoke the static method getModels
				} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				
				//Try to find an existing model
				for (AbstractModel existingModel : modelList) {
					if (existingModel.getId() == result.getInt("id")) {
						model = (T) existingModel;
					}
				}
				
				//If no existing model was found
				if (model.getId() == 0) {
					model = clazz.newInstance();
					model.loadFromResult(result);
					modelList.add(model);
				}
				
				models.add(model);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			Log.error(e);
		}
        
        return models;
	}
	
	protected static <T extends AbstractModel> T loadOne(ResultSet result, Class<T> clazz) {
		ObservableList<T> models = loadList(result, clazz);
        
        if (models.size() != 0) return models.get(0);
        else return null;
    }
}

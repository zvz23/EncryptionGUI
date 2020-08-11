 
package org.openjfx.controllerpack;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;




public class SettingsController {
    
    
   
    
    @FXML
    private CheckBox checkBox;
    
    @FXML
    private TextField pathField;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button dirButton;

    private Stage settingStage;
    
    public void setStage(Stage stage){
        this.settingStage = stage;
    }
    
    
    
    public void initialize(){
        Preferences prefs = Preferences.userRoot().node("encryptionsettings/settings");
        if(prefs.getBoolean("fileOutput", false)){
            checkBox.selectedProperty().set(true);
        }
        else{
            checkBox.selectedProperty().set(false);
        }
        checkBoxTick(null);
        pathField.setText(prefs.get("filePath", ""));
    }
    
    public void checkBoxTick(ActionEvent event){
        if(checkBox.isSelected()){
            pathField.setDisable(false);
            dirButton.setDisable(false);
        }
        else{
            pathField.setDisable(true);
            dirButton.setDisable(true);
        }
    }
    
    public void openDirectory(ActionEvent event){
        
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File dir = chooser.showDialog(settingStage);
        if(dir != null){
            pathField.setText(dir.getAbsolutePath());
        }
    }
    
    public void saveChanges(ActionEvent event){
        Preferences prefs = Preferences.userRoot().node("encryptionsettings/settings");
        Alert alert = null;
        if(pathField.getText().matches("\\s*") || pathField.getText().equals("")){
            prefs.putBoolean("fileOutput", false);
            new MainController().throwAlert(Alert.AlertType.WARNING, "Save", "Not saved because there is no output directory");
            this.settingStage.close();
            return;
        }
        if(new File(pathField.getText()).exists()){
            prefs.putBoolean("fileOutput", checkBox.isSelected());
            prefs.put("filePath", pathField.getText());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Save");
            alert.setContentText("Saved successfully");
            alert.showAndWait();
        }
        else{
          alert = new Alert(Alert.AlertType.ERROR);
          alert.setHeaderText("Save Error");
          alert.setContentText("Invalid path");
          alert.showAndWait();
        }
        this.settingStage.close();
    }
    
}

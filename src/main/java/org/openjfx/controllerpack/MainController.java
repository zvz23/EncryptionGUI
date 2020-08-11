package org.openjfx.controllerpack;

import org.openjfx.controllerpack.AboutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import encryptionpack.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.util.Scanner;
import java.io.File;
import java.net.URL;
import java.util.prefs.Preferences;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import java.io.FileWriter;

public class MainController {


    private Stage stage;
    private File file = null;
    private Alert errorAlert;
    private Encryption encryption = new Encryption();

    @FXML
    private TextField dataField;

    @FXML
    private TextField keyField;

    @FXML
    private Button clearButton;

    @FXML
    private ComboBox algoBox;
    
    @FXML
    private ToggleGroup modeGroup;
    
  
  
   
    public void setStage(Stage stage){
        this.stage=stage;
    }

    public void initialize(){
        algoBox.getItems().add("SHIFT");
        algoBox.getItems().add("UNICODE");
        algoBox.getItems().add("UNISHIFT");
        algoBox.getSelectionModel().selectFirst();
        for(Toggle toggle : modeGroup.getToggles()){
            ToggleButton button = (ToggleButton) toggle;
            button.setOnAction((ActionEvent t) -> {
                button.selectedProperty().set(true);
            });
        }
        
        encryption.setAlgorithm(new ShiftAlgorithm());
       
    }

    public void clickStart(ActionEvent event){
        ToggleButton selected = (ToggleButton)modeGroup.getSelectedToggle();
        Scanner scan = null;
        if(dataField.getText().equals("") || dataField.getText().matches("\\s*")){
            throwAlert(Alert.AlertType.ERROR, "No data", "Please set a data");
        }
        else{
            try{
                List<String> outputList = new ArrayList<>();
                int key = Integer.parseInt(keyField.getText());
                encryption.setKey(key);
                if(file != null){
                    scan = new Scanner(file);
                    while(scan.hasNextLine()){
                        encryption.setData(scan.nextLine());
                        if(selected.getText().equals("Encrypt")){
                            outputList.add(encryption.encrypt());
                        }
                        else{
                           outputList.add(encryption.decrypt());

                        }    
                    }
                    if(outputList.isEmpty()){
                        throwAlert(Alert.AlertType.WARNING, "No data", "File has no data");
                    }
                    else{
                        showOutput(outputList);
                    }
                }
                else{
                    outputList.clear();
                    encryption.setData(dataField.getText());
                    if(selected.getText().equals("Encrypt")){
                        outputList.add(encryption.encrypt());
                    }
                    else{
                        outputList.add(encryption.decrypt());
                    }
                    showOutput(outputList);
                  
                }
                
            }catch(NumberFormatException e){
                throwAlert(Alert.AlertType.ERROR,"Key error", "Key must be a number greater than 0");
            }
            //catch(IllegalArgumentException e){
            //    throwAlert(Alert.AlertType.WARNING, "Invalid key", "Key must be greater than zero");
            //}
            catch(FileNotFoundException e){
                throwAlert(Alert.AlertType.ERROR, "File error", "File not found");
            } catch (MalformedURLException e) {
                throwAlert(Alert.AlertType.ERROR, "URL error", "File path was invalid");

            } catch (IOException e) {
                throwAlert(Alert.AlertType.ERROR, "Error", "There was an error, please contanct the developer.");
            }
            finally{
                if(scan != null){
                    scan.close();
                }
            }
        }

    }
    
    public void showOutput(List<String> dataOutput) throws IOException{
        Preferences prefs = Preferences.userRoot().node("encryptionsettings/settings");
        if(!prefs.getBoolean("fileOutput", false)){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Output.fxml"));
            Stage outputStage = new Stage();
            Parent root = loader.load();
            ObservableList<Node> list = root.getChildrenUnmodifiable();
            TextArea textArea = null;
            for(Node node : list){
                if(node instanceof TextArea){
                    textArea = (TextArea) node;
                    break;
                }
            }
            textArea.setEditable(false);
            for(String data : dataOutput){
                 textArea.appendText(data + "\n");
            }
            outputStage.setTitle("Output");
            outputStage.setResizable(false);
            outputStage.initOwner(this.stage);
            outputStage.initModality(Modality.APPLICATION_MODAL);
            Scene outputScene = new Scene(root);
            outputStage.setScene(outputScene);
            outputStage.showAndWait();
        }
        else{
            File outputFile = new File(prefs.get("filePath", null)+"/output.txt");
            FileWriter writer = null;
            if(outputFile != null){
                try{
                    writer = new FileWriter(outputFile, false);
                    for(String data: dataOutput){
                         writer.write(data);
                         writer.write("\n");
                     }
                    throwAlert(Alert.AlertType.INFORMATION, "Output", "Output was succesfully saved to \n" + outputFile.getAbsoluteFile());
                }catch(IOException e){
                    throwAlert(Alert.AlertType.ERROR, "Writer error", "There was an error saving the output");
                }
                finally{
                    try{
                        if(writer != null){
                            writer.close();
                        }
                    }catch(IOException e){
                        
                    }
                }
                
            }
        }
        
        
    }
    
    public void algoChange(ActionEvent event){
        String algoStr = algoBox.getSelectionModel().getSelectedItem().toString();
        encryption.setAlgorithm(AlgorithmFactory.getAlgorithm(Algorithm.valueOf(algoStr)));
    }

    public void openFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        file = fileChooser.showOpenDialog(stage);
        if(file != null){
            Pattern pattern = Pattern.compile(".+\\.(?<post>.+)");
            Matcher matcher = pattern.matcher(file.getAbsolutePath());
            matcher.matches();
            if(!matcher.group("post").equals("txt")){
                throwAlert(Alert.AlertType.ERROR, "Invalid file", "Invalid file format, file must me a .txt");
            }
            else{
                dataField.setText(file.getAbsolutePath());
                dataField.setDisable(true);
                clearButton.setVisible(true);
            }
        }
    }

    public void clearField(ActionEvent event){
        dataField.clear();
        clearButton.setVisible(false);
        dataField.setDisable(false);
        file = null;

    }

    public void dataFieldChange(KeyEvent event){
        if(dataField.getText().matches("\\s*")){
            clearButton.setVisible(false);
        }
        if(dataField.getText().matches("(\\s*\\w+\\s*)+")){
            clearButton.setVisible(true);
        }
    }

    public void throwAlert(Alert.AlertType alertType, String head, String content){
        errorAlert = new Alert(alertType);
        errorAlert.setHeaderText(head);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
    
    public void showAbout(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/About.fxml"));
        Parent root = loader.load();
        AboutController controller = loader.getController();
        Stage aboutStage = new Stage();
        controller.setStage(aboutStage);
        aboutStage.setResizable(false);
        aboutStage.initOwner(this.stage);
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.setTitle("About");
        Scene scene = new Scene(root);
        aboutStage.setScene(scene);
        aboutStage.showAndWait();
        
    }
    
    public void showSettings(ActionEvent e) throws MalformedURLException, IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SettingsDesign.fxml"));
        Parent root = loader.load();
        SettingsController controller = loader.getController();
        Scene scene = new Scene(root);
        Stage settingStage = new Stage();
        controller.setStage(settingStage);
        settingStage.initOwner(this.stage);
        settingStage.initModality(Modality.APPLICATION_MODAL);
        settingStage.setScene(scene);
        settingStage.showAndWait();
       
    }


}

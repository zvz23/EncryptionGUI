
package org.openjfx.controllerpack;

import javafx.stage.Stage;


public class AboutController {
    
    private Stage mainStage;
    
    public void setStage(Stage stage){
        this.mainStage = stage;
    }
    
    public void closeStage(){
        mainStage.close();
    }
    
}

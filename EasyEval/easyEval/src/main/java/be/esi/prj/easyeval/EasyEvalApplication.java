package be.esi.prj.easyeval;

import be.esi.prj.easyeval.service.NavigationService;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class EasyEvalApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationService navigationService = NavigationService.getInstance();
        Image appIcon = new Image(getClass().getResourceAsStream("/be/esi/prj/easyeval/images/check-list.png"));
        primaryStage.getIcons().add(appIcon);
        navigationService.setPrimaryStage(primaryStage);

        navigationService.navigateToCourseManager();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
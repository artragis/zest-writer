package com.zestedesavoir.zestwriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private Scene scene;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<ExtractFile> extracts = FXCollections.observableArrayList();
    private ObservableMap<String, String> contents = FXCollections.observableMap(new HashMap<>());
    private ZdsHttp zdsutils;
    private MdTextController Index;
    private Properties props;
    StringBuilder key = new StringBuilder();

    public MainApp() {
        super();
        props = new Properties();
        InputStream input = MainApp.class.getClassLoader().getResourceAsStream("config.properties");

        try {
            props.load(input);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Scene getScene() {
        return scene;
    }

    public MdTextController getIndex() {
        return Index;
    }

    public ObservableMap<String, String> getContents() {
        return contents;
    }

    public ZdsHttp getZdsutils() {
        return zdsutils;
    }

    public ObservableList<ExtractFile> getExtracts() {
        return extracts;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zest Writer");
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        try {
            zdsutils = new ZdsHttp(props, primaryStage);
        } catch (IOException ex) {
            zdsutils = new ZdsHttp(props);
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        initRootLayout();
        showWriter();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Root.fxml"));
            rootLayout = loader.load();

            MenuController controller = loader.getController();
            controller.setMainApp(this);

            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            loadCombinason();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showWriter() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Index.fxml"));
            AnchorPane writerLayout = loader.load();

            rootLayout.setCenter(writerLayout);


            MdTextController controller = loader.getController();
            controller.setMainApp(this);
            Index = controller;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCombinason() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                String codeStr = t.getCode().toString();
                if(!key.toString().endsWith("_"+codeStr)){
                     key.append("_"+codeStr);
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(key.length()>0) {
                    if(key.toString().equals("_CONTROL_C_L_E_M")){
                     // Create the custom dialog.
                        Dialog<Void> dialog = new Dialog<>();
                        dialog.setTitle("Easter Egg");
                        dialog.setHeaderText(null);
                        dialog.setContentText(null);

                        dialog.setGraphic(new ImageView(this.getClass().getResource("view/static/goal.gif").toString()));
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                        dialog.showAndWait();
                    }
                    key = new StringBuilder();
                }

            }
        });
    }
}

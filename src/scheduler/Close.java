package scheduler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Close {
    private static boolean answer;
    public static boolean display(String title, String message){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label(message);

        //Create two buttons
        Button yes = new Button("Yes");
        Button no = new Button("No");
        yes.setMinWidth(50);
        no.setMinWidth(50);
        yes.requestFocus();
        yes.setOnAction(e-> {
            answer = true;
            window.close();
        });

        no.setOnAction(e-> {
            answer = false;
            window.close();
        });

        HBox buttons = new HBox(70);
        buttons.getChildren().addAll(yes,no);
        buttons.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(label,buttons);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GUI extends Application {
    Stage window;
    BorderPane mainLayout = new BorderPane();
    Scene submitScene ;
    Scene DFAScene;
    StackPane sp = new StackPane();
    GridPane d = new GridPane();
    Label acceptance = new Label("");
    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        stage.setTitle("Automaton");
        Text title = new Text("Automaton");
        sp.getChildren().addAll(submission());
        submitScene=  new Scene(sp, 800, 600) ;
        window.setScene(submitScene);
        window.show();
    }

    private VBox submission() {

        Label label = new Label("Enter String to be checked: ");
        TextField text = new TextField();
        text.setMaxWidth(400);
        Button button = new Button();
        button.setText("Submit");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    Automaton a = new Automaton("C:\\Users\\ASUS\\Documents\\GitHub\\compiler\\Compiler\\dependencies\\target.txt");
                    System.out.println(text.getText());
               //     acceptance= new Label("");
                    acceptance.setText(Compile.match(a, text.getText()));
                    acceptance.setScaleX(2);
                    acceptance.setScaleY(2);
                    acceptance.setScaleZ(2);

                }
                 catch (InvalidStatementException e) {
                    System.out.println(e.getMessage());
                }
                Button back = new Button("<<");
                back.setOnAction(e->
                {
                    window.setScene(submitScene);
                    window.show();
                });
                back.setAlignment(Pos.TOP_CENTER);
                d= new GridPane();
                d.setAlignment(Pos.CENTER);
                d.setHgap(15);
                d.setVgap(15);
                //d.setPadding(new Insets(25, 25, 25, 25));
                d.getChildren().add(acceptance);
                HBox dd =  new HBox();
                dd.setAlignment(Pos.TOP_CENTER);
                dd.getChildren().addAll(d,back);
                DFAScene = new Scene(dd, 800, 600);
                window.setScene(DFAScene);
                window.show();
            }

        });
        HBox h =  new HBox(50);
        VBox v = new VBox(50);
        v.setAlignment(Pos.TOP_CENTER);
        h.setAlignment(Pos.TOP_CENTER);
        h.getChildren().addAll(text, button);
        v.getChildren().addAll(label,h);
        return v;
    }

    public static ArrayList<String> readFile(String filename){
        ArrayList<String> result = new ArrayList<>();
        String regex = "";
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((regex = reader.readLine()) != null){
                result.add(regex);
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}

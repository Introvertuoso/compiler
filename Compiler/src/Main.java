import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    // There's a simple table used for debugging line 12 in Compile.java (Can inspire the GUI Table)
    // TODO Test and debug everything
    // TODO Create a GUI that passes a String to compile()
    // TODO Create the graph as a table in GUI
    // TODO Migrate showing logs to GUI

    public static void main(String[] args) {

            Automaton a = new Automaton("E:\\uni\\projects\\Third Year\\Second Semester\\Automata\\compiler\\Compiler\\dependencies\\target.txt");
           // Application.launch(GUI.class, args);
           // Compile.match(a, "int Salary =9.678675;");

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

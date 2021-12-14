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
        ArrayList<String> subRegex = readFile("dependencies\\target.txt");
        ArrayList<Automaton> subAutos = new ArrayList<>();
        try{
            for (String s: subRegex){
                subAutos.add(new Automaton(s));
            }
            Automaton a = new Automaton(subAutos);
            Compile.match(a, "int Salary =9.678675;");
        } catch (InvalidAutomatonException e){
            System.out.println(e.getMessage());
        }
        catch (InvalidStatementException e){
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<String> readFile(String filename){
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

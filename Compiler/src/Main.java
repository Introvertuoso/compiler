public class Main {

    // TODO make [ currentCase++ -> currentCase = j.getValue() ] in Compile.match()
    // There's a simple table used for debugging line 12 in Compile.java (Can inspire the GUI Table)
    // TODO Test and debug everything
    // TODO Create a GUI that passes a String to compile()
    // TODO Create the graph as a table in GUI
    // TODO Migrate showing logs to GUI

    public static void main(String[] args) {
        try{
            Automaton a = new Automaton("dependencies\\target.txt");
            Compile.match(a, "int Salary =9.678675;");
        } catch (InvalidAutomatonException e){
            System.out.println(e.getMessage());
        }
        catch (InvalidStatementException e){
            System.out.println(e.getMessage());
        }
    }
}

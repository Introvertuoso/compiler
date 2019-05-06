public class Main {

    // TODO Test and debug everything
    // TODO Create a GUI that passes a String to compile()
    // TODO Create the graph as a table in GUI
    // TODO Migrate showing logs to GUI

    public static void main(String[] args) {
        try{
            Automaton a = new Automaton("dependencies\\target.txt");
        } catch (InvalidAutomatonException e){
            System.out.println(e.getMessage());
        }
        /*
        try {
            Compile.match();
        } catch (InvalidStatementException e){
            System.out.println(e.getMessage());
        }
        */
    }
}

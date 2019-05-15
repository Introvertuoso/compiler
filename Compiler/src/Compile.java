import java.util.HashMap;
import java.util.Iterator;

public class Compile {
    public static String match(Automaton pattern, String target) throws InvalidStatementException{
        char[] targetAsCharArray = target.toCharArray();
        int currentCase = 0;
        String attempt = "";
        int counter =0;
        int x = 0;
        int indexOfTerminal = -1;

        System.out.println();
        // Simple graph for debugging (can inspire the GUI graph)
        for (Case c: pattern.getCases()){
            if (c.getState() == CaseType.TERMINAL){
                indexOfTerminal = x;
            }
            else if (c.getState() == CaseType.FINAL){
                pattern.getFinalCases().add(c);
            }
            System.out.println("Case: " + x + ", Type: " + c.getType().toString() + ", State: " + c.getState().toString());
            Iterator it = c.getNextRules().entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                System.out.println(pair.getKey() + " -> " + pair.getValue());
            }
            x++;
        }

        for (char i: targetAsCharArray){
            int exists = -2;
            attempt = attempt + i;
            Case c = pattern.getCases().get(currentCase);
            // Compiles the regex cases
            if (c.getType() == Property.PSTAR){
                if ((exists = checkCase(attempt, c)) != -1){
                    attempt = "";
                }
                else {
                    currentCase++;
                    if ((exists = checkCase(attempt, pattern.getCases().get(currentCase))) != -1){
                        currentCase = exists;
                        attempt= "";
                    }
                    else{
                        while (pattern.getCases().get(currentCase).getType() != Property.NONE){
                            currentCase++;
                            if ((exists = checkCase(attempt, pattern.getCases().get(currentCase))) != -1){
                                currentCase = exists;
                                attempt= "";
                            }
                        }
                    }
                }
            }
            // Compiles regex cases
            else if (c.getType() == Property.PPLUS){
                if ((exists = checkCase(attempt, c)) != -1){
                    attempt = "";
                }
                else if (counter>0){
                    currentCase++;
                    if ((exists = checkCase(attempt, pattern.getCases().get(currentCase))) != -1){
                        currentCase = exists;
                        attempt= "";
                    }
                    else{
                        while (pattern.getCases().get(currentCase).getType() != Property.NONE){
                            currentCase++;
                            if ((exists = checkCase(attempt, pattern.getCases().get(currentCase))) != -1){
                                currentCase = exists;
                                attempt= "";
                            }
                        }
                    }
                    counter=0;
                }
                else {
                    throw new InvalidStatementException("Case " + currentCase + " not met");
                }
                counter++;
            }
            // Is shared for regex and DFA compilation
            else {
                if ((exists = checkCase(attempt, c)) >= 0) {
                    currentCase = exists;
                    attempt = "";
                } else if (exists == -1 && indexOfTerminal != -1) {
                    currentCase = indexOfTerminal;
                    attempt = "";
                }
            }
        }
        System.out.println("\nProgram has reached case: #" + currentCase);
        if (pattern.getCases().get(currentCase).getState() == CaseType.FINAL){
            return "Statement Accepted. It's a " + pattern.getCases().get(currentCase).getComment() + ".";
          //  throw new InvalidStatementException("Statement Accepted");
        }
        else {
            return "Statement Rejected";
           // throw new InvalidStatementException("Statement Rejected");
        }
    }

    private static int checkCase(String s, Case c){
        // -2 is the doesn't exist value
        // -1 is the exists and goes to Terminal Case value
        // else is the exists and goes to a normal case value
        int exists = -2;
        HashMap<String, Integer> tempMap = c.getNextRules();
        Iterator it = tempMap.entrySet().iterator();
        while(it.hasNext()) {
            HashMap.Entry<String, Integer> j = (HashMap.Entry) it.next();
            if (s.length() == 1){
                char joker = s.toCharArray()[0];
                if (j.getKey().compareTo("A..Z")== 0){
                    if (Character.isAlphabetic(joker)){
                        exists = j.getValue();
                    }
                }
                else if (j.getKey().compareTo("0..9") == 0){
                    if (Character.isDigit(joker)){
                        exists = j.getValue();
                    }
                }
                else if (j.getKey().compareTo(" ") == 0){
                    if (Character.isWhitespace(joker))
                        exists = j.getValue();
                }
                else {
                    String jokerString = "" + joker;
                    if (jokerString.compareTo(j.getKey()) == 0){
                        exists = j.getValue();
                    }
                }
            }
            else {
                if (s.compareTo(j.getKey()) == 0){
                    exists = j.getValue();
                }
            }
        }
        return exists;
    }

    // Finds the alphabet to be fed into the TableObject's columns in GUI
    // void may need to be changed to String[]
    public static void graph(Automaton pattern){}
}

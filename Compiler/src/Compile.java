import java.util.HashMap;
import java.util.Iterator;

public class Compile {
    public static void match(Automaton pattern, String target) throws InvalidStatementException{
        char[] targetAsCharArray = target.toCharArray();
        int currentCase = 0;
        String attempt = "";
        int counter =0;

        System.out.println();
        // Simple graph for debugging (can inspire the GUI graph)
        for (Case c: pattern.getCases()){
            System.out.println("Case: " + counter + ", Type: " + c.getType().toString() + ", State: " + c.getState().toString());
            Iterator it = c.getNextRules().entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                System.out.println(pair.getKey() + " -> " + pair.getValue());
            }
            counter++;
        }

        for (char i: targetAsCharArray){
            attempt = attempt + i;
            Case c = pattern.getCases().get(currentCase);
            if (c.getType() == Property.PSTAR){
                if (checkCase(attempt, c)){
                    attempt = "";
                }
                else {
                    currentCase++;
                    if (checkCase(attempt, pattern.getCases().get(currentCase))){
                        currentCase++;
                        attempt= "";
                    }
                    else{
                        while (pattern.getCases().get(currentCase).getType() != Property.NONE){
                            currentCase++;
                            if (checkCase(attempt, pattern.getCases().get(currentCase))){
                                currentCase++;
                                attempt= "";
                            }
                        }
                    }
                }
            }
            else if (c.getType() == Property.PPLUS){
                if (checkCase(attempt, c)){
                    attempt = "";
                }
                else if (counter>0){
                    currentCase++;
                    if (checkCase(attempt, pattern.getCases().get(currentCase))){
                        currentCase++;
                        attempt= "";
                    }
                    else{
                        while (pattern.getCases().get(currentCase).getType() != Property.NONE){
                            currentCase++;
                            if (checkCase(attempt, pattern.getCases().get(currentCase))){
                                currentCase++;
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
            else {
                if (checkCase(attempt, c)){
                    currentCase++;
                    attempt="";
                }
            }
        }
        System.out.println("\nProgram has reached case: #" + currentCase);
        if (pattern.getCases().get(currentCase).getState() == CaseType.FINAL){
            throw new InvalidStatementException("Statement Accepted");
        }
        else {
            throw new InvalidStatementException("Statement Rejected");
        }
    }

    private static boolean checkCase(String s, Case c){
        boolean exists = false;
        HashMap<String, Integer> tempMap = c.getNextRules();
        Iterator it = tempMap.entrySet().iterator();
        while(it.hasNext()) {
            HashMap.Entry<String, Integer> j = (HashMap.Entry) it.next();
            if (s.length() == 1){
                char joker = s.toCharArray()[0];
                if (j.getKey().compareTo("A..Z")== 0){
                    if (Character.isAlphabetic(joker)){
                        exists = true;
                    }
                }
                else if (j.getKey().compareTo("0..9") == 0){
                    if (Character.isDigit(joker)){
                        exists = true;
                    }
                }
                else if (j.getKey().compareTo(" ") == 0){
                    if (Character.isWhitespace(joker))
                        exists = true;
                }
                else {
                    String jokerString = "" + joker;
                    if (jokerString.compareTo(j.getKey()) == 0){
                        exists = true;
                    }
                }
            }
            else {
                if (s.compareTo(j.getKey()) == 0){
                    exists = true;
                }
            }
        }
        return exists;
    }

    // Finds the alphabet to be fed into the TableObject's columns in GUI
    // void may need to be changed to String[]
    public static void graph(Automaton pattern){}
}

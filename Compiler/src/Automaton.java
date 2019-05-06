import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Automaton {
    private ArrayList<Case> cases;
    //private List<Character> alphabet;
    private Case initial;
    private Case _final;

    private int indexOfFinal;
    private int indexOfCurrent = 0;
    private char[] temp = null;
    private int keywordTracker = 0;
    private Stack<Character> braces;
    private boolean powerON = false;

    Automaton(String path) throws InvalidAutomatonException{
        String regex = readFile(path);
        regex = regex.replaceAll("\\s","");
        char[] regexAsCharArray = regex.toCharArray();
        validateBrackets(regexAsCharArray);
        initial = new Case();
        _final = new Case();
        initial.setState(CaseType.INITIAL);
        _final.setState(CaseType.FINAL);
        cases.add(initial);
        cases.add(_final);
        indexOfCurrent = cases.indexOf(_final);
        if (
                regexAsCharArray[regexAsCharArray.length-1] == ',' ||
                regexAsCharArray[regexAsCharArray.length-1] == '+' ||
                regexAsCharArray[regexAsCharArray.length-1] == '^' ||
                regexAsCharArray[0] == '^' ||
                regexAsCharArray[0] == ',' ||
                regexAsCharArray[0] == '+'
        ){
            throw new InvalidAutomatonException("Automaton cannot start/end with '.', '+' or '^'");
        }
        int counter = 0;
        for (char i: regexAsCharArray){
            try {
                createDFA(i);
            } catch (InvalidAutomatonException e){
                throw new InvalidAutomatonException(e.getMessage() + " at index: " + counter);
            }
            counter++;
        }
        cases.get(indexOfCurrent).getNextRules().add(new Pair<>(temp.toString(), indexOfCurrent+1));
    }

    private String readFile(String filename){
        String regex = null;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            regex  = reader.readLine();
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return regex;
    }

    private void validateBrackets (char[] regex) throws InvalidAutomatonException{
        if (regex != null) {
            int flag = 0;
            for (char i : regex) {
                if (i == '(')
                    flag++;
                if (i == ')')
                    flag--;
            }
            if (flag != 0)
                throw new InvalidAutomatonException("Missing bracket(s)\n");
        }
        else
            throw new InvalidAutomatonException("Missing reqular expression\n");
    }

    private void createDFA(char i) throws InvalidAutomatonException{
        // Enters power mode
        if (i == '^'){
            if (temp == null)
                throw new InvalidAutomatonException("Nothing to use '^' for");
            powerON = true;
        }
        // Declares a new case
        else if (i == '('){
            braces.push('(');
            Case c = new Case();
            c.setState(CaseType.FINAL);
            cases.add(c);
            indexOfFinal = cases.indexOf(c);
        }
        // OR (adds new rule in same case)
        else if (i == '+'){
            if (temp == null)
                throw new InvalidAutomatonException("Nothing to use '+' for");
            cases.get(indexOfCurrent).getNextRules().add(new Pair<>(temp.toString(), indexOfCurrent+1));
            keywordTracker = 0;
            temp = null;
        }
        // AND (adds new case)
        else if (i == ',') {
            if (temp == null)
                throw new InvalidAutomatonException("Nothing to use ',' for");
            Case c = new Case();
            cases.add(c);
            if (braces.empty()) {
                c.setState(CaseType.FINAL);
                cases.get(indexOfFinal).setState(CaseType.REGULAR);
                indexOfFinal = cases.indexOf(c);
            }
            indexOfCurrent++;
            cases.get(indexOfCurrent).getNextRules().add(new Pair<>(temp.toString(), indexOfCurrent+1));
            keywordTracker = 0;
            temp = null;
        }
        // Omits the case and allows its repetition
        else if (i == '*' && powerON){
            for (Pair<String, Integer> j: cases.get(indexOfCurrent-1).getNextRules()){
                Pair p = new Pair(j.getFirst(), indexOfCurrent+2);
                cases.get(indexOfCurrent-1).getNextRules().add(p);
            }
            for (Pair<String, Integer> j: cases.get(indexOfCurrent).getNextRules()){
                Pair p = new Pair(j.getFirst(), indexOfCurrent);
                cases.get(indexOfCurrent).getNextRules().add(p);
            }
            powerON = false;
        }
        // Allows case repetition
        else if (i == '+' && powerON){
            for (Pair<String, Integer> j: cases.get(indexOfCurrent).getNextRules()){
                Pair p = new Pair(j.getFirst(), indexOfCurrent);
                cases.get(indexOfCurrent).getNextRules().add(p);
            }
            powerON = false;
        }
        // Keeps track of brackets
        else if (i == ')' ){
            if (temp == null)
                throw new InvalidAutomatonException("Empty automaton");
            braces.pop();
        }
        // Reads alphabet characters
        else {
            if (i == '_'){
                temp[keywordTracker] = '\u00A0';
                keywordTracker++;
            }
            else {
                temp[keywordTracker] = i;
                keywordTracker++;
            }
        }
    }

    public ArrayList<Case> getCases() {
        return cases;
    }
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Automaton {
    private ArrayList<Case> cases = new ArrayList<>();
    private Case initial;
    private Case _final;

    private int indexOfCurrent = 0;
    private ArrayList<Character> temp = new ArrayList<>();
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
        indexOfCurrent = cases.indexOf(initial);
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
    }

    private String readFile(String filename){
        String regex = "";
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
        if (regex.length != 0) {
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
            if (cases.isEmpty())
                throw new InvalidAutomatonException("Nothing to use '^' for");
            powerON = true;
        }
        // Declares a new case
        else if (i == '('){
            Case c = new Case();
            c.setState(CaseType.FINAL);
            _final.setState(CaseType.REGULAR);
            _final = c;
            cases.add(c);
        }
        // OR (adds new rule in same case)
        else if (i == '+' && !powerON){
            if (temp.isEmpty())
                throw new InvalidAutomatonException("Nothing to use '+' for");
            cases.get(indexOfCurrent).getNextRules().put(getStringRepresentation(temp), indexOfCurrent+1);
            temp.clear();
        }
        // AND (adds new case)
        else if (i == ',') {
            indexOfCurrent++;
        }
        // Omits the case and allows its repetition
        else if (i == '*' && powerON){
            cases.get(indexOfCurrent).setType(Property.PSTAR);
            powerON = false;
        }
        // Allows case repetition
        else if (i == '+' && powerON){
            cases.get(indexOfCurrent).setType(Property.PPLUS);
            powerON = false;
        }
        // Keeps track of brackets
        else if (i == ')' ){
            if (temp.isEmpty())
                throw new InvalidAutomatonException("Empty automaton");
            cases.get(indexOfCurrent).getNextRules().put(getStringRepresentation(temp), indexOfCurrent+1);
            temp.clear();
        }
        // Reads alphabet characters
        else {
            if (i == '_')
                temp.add(' ');
            else
                temp.add(i);
        }
    }

    private String getStringRepresentation(ArrayList<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list)
        {
            builder.append(ch);
        }
        return builder.toString();
    }

    public ArrayList<Case> getCases() {
        return cases;
    }
}

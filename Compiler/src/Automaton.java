import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Automaton {
    private ArrayList<Case> cases = new ArrayList<>();
    private Case initial;
    private ArrayList<Case> finalCases = new ArrayList<>();
    private Case _final;

    private static int indexOfCurrent = 0;
    private static ArrayList<Character> temp = new ArrayList<>();
    private static boolean powerON = false;
    private static String[] alphabetArray;

    /*  Automaton(String regex) throws InvalidAutomatonException{
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
*/

    Automaton(ArrayList<Automaton> list){
        initial = new Case();
        initial.setState(CaseType.INITIAL);
        cases.add(initial);
        int offset = 0;

        for (Automaton a : list){
            for (Case c: a.getCases()){
                if (c.getState() == CaseType.INITIAL){
                    if (c.getType() == Property.PSTAR){
                        initial.setType(Property.PSTAR);
                    }
                    else if (c.getType() == Property.PPLUS && initial.getType() != Property.PSTAR){
                        initial.setType(Property.PPLUS);
                    }
                    HashMap<String, Integer> tempMap = c.getNextRules();
                    Iterator it = tempMap.entrySet().iterator();
                    while(it.hasNext()) {
                        HashMap.Entry<String, Integer> j = (HashMap.Entry) it.next();
                        initial.getNextRules().put(j.getKey(), j.getValue()+offset);
                    }
                }
                else {
                    Case clone = new Case();
                    clone.setType(c.getType());
                    clone.setState(c.getState());
                    HashMap<String, Integer> tempMap = c.getNextRules();
                    Iterator it = tempMap.entrySet().iterator();
                    while(it.hasNext()) {
                        HashMap.Entry<String, Integer> j = (HashMap.Entry) it.next();
                        clone.getNextRules().put(j.getKey(), j.getValue()+offset);
                    }
                    cases.add(clone);
                }
            }
            offset = a.getCases().size()-1;
        }
    }

    Automaton(String s){
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(s))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray  DFA = (JSONArray) obj;
            JSONArray alphabetJSON = (JSONArray) DFA.get(0);
            JSONArray casesJSON = (JSONArray) DFA.get(1);
            alphabetArray = new String[alphabetJSON.size()];
            for (int i=0; i<alphabetJSON.size(); i++){
                alphabetArray[i] = (String) alphabetJSON.get(i);
                System.out.println(alphabetArray[i]);
            }
            //Iterate over employee array
            casesJSON.forEach( c -> {

                cases.add(parseCaseObject((JSONObject) c));
            }
            );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static Case parseCaseObject(JSONObject c) {
        Case resultCase = new Case();
        //Get type
        String type = (String) c.get("type");
        switch (type){
            case "initial":
                resultCase.setState(CaseType.INITIAL);
                break;
            case "regular":
                resultCase.setState(CaseType.REGULAR);
                break;
            case "final":
                resultCase.setState(CaseType.FINAL);
                break;
            case "terminal":
                resultCase.setState(CaseType.TERMINAL);
                break;
        }

        if (resultCase.getState() == CaseType.FINAL){
            resultCase.setComment((String)c.get("comment"));
        }

        for(String s: alphabetArray){
            if(c.get(s)!= null)
                resultCase.getNextRules().put(s, Integer.parseInt(c.get(s).toString()));
            else
                resultCase.getNextRules().put(s, -1);
        }


        return resultCase;
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

    public ArrayList<Case> getFinalCases() {
        return finalCases;
    }
}

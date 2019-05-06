public class Compile {
    public static void match(Automaton pattern, String target) throws InvalidStatementException{
        char[] targetAsCharArray = target.toCharArray();
        char[] temp= null;
        int wordTracker = 0;
        int currentCase = 0;
        boolean changed = false;

        for (char i : targetAsCharArray){
            temp[wordTracker] = i;
            wordTracker++;
            String attempt = temp.toString();
            for (Pair<String, Integer> j : pattern.getCases().get(currentCase).getNextRules()){
                if (attempt == j.getFirst()) {
                    currentCase = j.getSecond();
                    changed = true;
                }
            }
            if (changed){
                wordTracker = 0;
                temp = null;
                changed = false;
            }
        }
        if (pattern.getCases().get(currentCase).getState() == CaseType.FINAL){
            throw new InvalidStatementException("Accepted");
        }
        else
            throw new InvalidStatementException("Rejected");
    }

    // Finds the alphabet to be fed into the TableObject's columns in GUI
    // void may need to be changed to String[]
    public static void graph(Automaton pattern){}
}

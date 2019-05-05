import java.util.ArrayList;

public class Case {
    private CaseType state;
    private ArrayList<Pair<String, Integer>> nextRules;

    public void Case(CaseType st, ArrayList<Pair<String, Integer>> rules){
        this.state = st;
        this.nextRules = rules;
    }

    public CaseType getState() {
        return state;
    }

    public ArrayList<Pair<String, Integer>> getNextRules() {
        return nextRules;
    }

    public void setState(CaseType state) {
        this.state = state;
    }

    public void setNextRules(ArrayList<Pair<String, Integer>> nextRules) {
        this.nextRules = nextRules;
    }
}

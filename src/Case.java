import java.util.HashMap;

public class Case {
    private CaseType state;
    private Property type= Property.NONE;
    private HashMap<String, Integer> nextRules = new HashMap<>();

    public void Case(CaseType st, HashMap<String, Integer> rules){
        this.state = st;
        this.nextRules = rules;
    }

    public CaseType getState() {
        return state;
    }

    public HashMap<String, Integer> getNextRules() {
        return nextRules;
    }

    public void setState(CaseType state) {
        this.state = state;
    }

    public void setNextRules(HashMap<String, Integer> nextRules) {
        this.nextRules = nextRules;
    }

    public Property getType() {
        return type;
    }

    public void setType(Property type) {
        this.type = type;
    }
}

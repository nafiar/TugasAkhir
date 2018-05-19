import clauseBehavior.ProjectingBehavior;
import clauseBehavior.ReadingBehavior;
import clauseBehavior.SimpleProjectingBehavior;

import java.util.ArrayList;

public class Clause {
    private String sparqlQueryString;
    private ArrayList<ReadingBehavior> listOfReadingBehavior;
    private ProjectingBehavior projectingBehavior;

    public Clause(String sparqlQuery){
        this.sparqlQueryString = sparqlQuery;

    }
}

package comparators;

import java.util.Comparator;
import model.Request;

public class RequestPriorityComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
        return Integer.compare(r2.getPriority(), r1.getPriority());
    }
}
package comparators;

import java.util.Comparator;
import model.Request;

public class RequestPriorityComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
        if (r1 == null && r2 == null) {
            return 0;
        }
        if (r1 == null) {
            return 1;
        }
        if (r2 == null) {
            return -1;
        }
        return Integer.compare(r2.getPriority(), r1.getPriority());
    }
}

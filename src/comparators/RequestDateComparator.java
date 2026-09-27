package comparators;

import java.util.Comparator;
import model.Request;

public class RequestDateComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
        return r1.getRequestDate().compareTo(r2.getRequestDate());
    }
}

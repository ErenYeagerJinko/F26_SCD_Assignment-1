import java.util.Comparator;

class RequestPriorityComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
        return Integer.compare(r2.getPriority(), r1.getPriority());
    }
}
import java.util.Comparator;

class RequestDateComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
        return r1.getRequestDate().compareTo(r2.getRequestDate());
    }
}
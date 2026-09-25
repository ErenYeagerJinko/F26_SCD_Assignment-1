package comparators;

import java.util.Comparator;

class FYPMeetingDateComparator implements Comparator<FYPMeeting> {
    @Override
    public int compare(FYPMeeting m1, FYPMeeting m2) {
        return m1.getMeetingDate().compareTo(m2.getMeetingDate());
    }
}
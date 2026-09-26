package model;

import java.time.*;
import enums.*;
import exceptions.*;
import util.*;

public class Schedule {
    private Day day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

    public Schedule(Day day, LocalTime startTime, LocalTime endTime, String room) throws InvalidUserDataException {
        if (day == null) {
            Logger.error("Failed to create Schedule: Day is null");
            throw new InvalidUserDataException("Schedule day cannot be null.");
        }
        if (startTime == null || endTime == null) {
            Logger.error("Failed to create Schedule: Start time or end time is null");
            throw new InvalidUserDataException("Schedule start time and end time cannot be null.");
        }
        if (!startTime.isBefore(endTime)) {
            Logger.error("Failed to create Schedule: Start time " + startTime + " is not before end time " + endTime);
            throw new InvalidUserDataException("Schedule start time must be before end time.");
        }
        if (room == null || room.trim().isEmpty()) {
            Logger.error("Failed to create Schedule: Room is empty");
            throw new InvalidUserDataException("Schedule room cannot be null or empty.");
        }

        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room.trim();
        Logger.info("Schedule initialized: " + getScheduleInfo());
    }

    public Day getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) throws InvalidUserDataException {
        if (room == null || room.trim().isEmpty()) {
            Logger.error("Failed to update schedule room: Room is empty");
            throw new InvalidUserDataException("Schedule room cannot be null or empty.");
        }
        String oldRoom = this.room;
        this.room = room.trim();
        Logger.info("Updated schedule room from '" + oldRoom + "' to '" + this.room + "'");
    }

    public boolean hasClash(Schedule schedule) {
        if (schedule == null || schedule.day != this.day) {
            return false;
        }
        return this.startTime.isBefore(schedule.endTime) && schedule.startTime.isBefore(this.endTime);
    }

    public String getScheduleInfo() {
        return day + " " + startTime + "-" + endTime + " @ " + room;
    }
}

package seedu.address.model.task;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.model.name.Name;
import seedu.address.model.tag.Tag;

public class Task implements Comparable<Task> {

    //Identity fields
    private final Name name;
    private final Date date;
    private final StartTime startTime;
    private final EndTime endTime;
    private String time; // Date + startTime for sorting comparisons in SortTaskByDateCommand

    //Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Name> persons = new HashSet<>(); //persons are represented by their names

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, Date date, StartTime startTime, EndTime endTime, Set<Tag> tags, Set<Name> persons) {
        requireAllNonNull(name, date, startTime, endTime, tags, persons);
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tags.addAll(tags);
        this.persons.addAll(persons);
        this.time = this.date.toString() + " " + this.startTime.toString();
    }

    //Getters
    public Name getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public StartTime getStartTime() {
        return startTime;
    }

    public EndTime getEndTime() {
        return endTime;
    }

    public String getTime() {
        return time;
    }

    /**
     * Returns the start and end time values joined together for Task Card label
     *
     * @return appended values of start and end time
     */
    public String appendStartAndEndTime() {
        return startTime.value + " - " + endTime.value;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable persons set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Name> getPersons() {
        return Collections.unmodifiableSet(persons);
    }

    /**
     * Returns true if both tasks have the same name.
     * This defines a weaker notion of equality between two tasks.
     */
    public boolean isSameTask(Task otherTask) {
        if (otherTask == this) {
            return true;
        }

        return otherTask != null
                && otherTask.getName().equals(getName());
    }

    public boolean hasStartEndDateConflict() {
        LocalTime thisTaskStart = LocalTime.parse(startTime.value);
        LocalTime thisTaskEnd = LocalTime.parse(endTime.value);
        return thisTaskEnd.compareTo(thisTaskStart) <= 0;
    }

    /**
     * Returns true if both tasks have the same date and conflicting time ranges.
     */
    public boolean hasDateTimeConflict(Task otherTask) {
        if (otherTask == this) {
            return true;
        }

        LocalTime thisTaskStart = LocalTime.parse(startTime.value);
        LocalTime thisTaskEnd = LocalTime.parse(endTime.value);
        LocalTime otherTaskStart = LocalTime.parse(otherTask.startTime.value);
        LocalTime otherTaskEnd = LocalTime.parse(otherTask.endTime.value);

        // Solution below adapted from https://stackoverflow.com/q/325933
        // checks if time ranges overlap (exclusive)
        boolean timeConflict =
                thisTaskStart.isBefore(otherTaskEnd)
                        && otherTaskStart.isBefore(thisTaskEnd);

        return otherTask != null
                && otherTask.getDate().equals(getDate()) //test for same dates
                && timeConflict;
    }

    /**
     * Returns true if both tasks have the same identity and data fields.
     * This defines a stronger notion of equality between two tasks.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Task)) {
            return false;
        }

        Task otherTask = (Task) other;
        return otherTask.getName().equals(getName())
                && otherTask.getDate().equals(getDate())
                && otherTask.getStartTime().equals(getStartTime())
                && otherTask.getEndTime().equals(getEndTime())
                && otherTask.getTags().equals(getTags())
                && otherTask.getPersons().equals(getPersons());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, startTime, endTime, tags, persons);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append("; Date: ")
                .append(getDate())
                .append("; Start Time: ")
                .append(getStartTime())
                .append("; End Time: ")
                .append(getEndTime());

        Set<Tag> tags = getTags();
        if (!tags.isEmpty()) {
            builder.append("; Tags: ");
            tags.forEach(builder::append);
        }
        Set<Name> persons = getPersons();
        if (!persons.isEmpty()) {
            builder.append("; Persons: ");
            int count = 0;
            for (Name name : persons) {
                builder.append(name);
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    @Override
    public int compareTo(Task otherTask) {
        DateTimeFormatter customFormat = DateTimeFormatter
                .ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

        LocalDateTime date1 = LocalDateTime.parse(this.getTime(), customFormat);
        LocalDateTime date2 = LocalDateTime.parse(otherTask.getTime(), customFormat);

        return date1.compareTo(date2);
    }
}

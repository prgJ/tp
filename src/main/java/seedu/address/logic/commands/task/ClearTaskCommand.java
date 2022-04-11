package seedu.address.logic.commands.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;

import java.util.List;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.TaskBook;
import seedu.address.model.task.Date;
import seedu.address.model.task.Task;

/**
 * Clears the tasks from the task list (either all tasks or only for date specified).
 */
public class ClearTaskCommand extends Command {

    public static final String COMMAND_WORD = "clear-t";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the task identified by the date used in the TaskList, or clears all tasks if date is not "
            + "provided.\n"
            + "Parameters: "
            + "[" + PREFIX_DATE + "DATE]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DATE + "08-04-2022";

    public static final String MESSAGE_SUCCESS = "Task list has been cleared!";
    public static final String MESSAGE_SUCCESS_ALT = "Tasks for date specified have been cleared: %1$s";
    public static final String MESSAGE_INVALID_DATE =
            "There are no tasks in the list that match the provided date: %1$s";
    public static final String MESSAGE_EMPTY_LIST = "Task list is empty!";

    private final Date date;
    private final boolean hasDate;

    /**
     * Creates a ClearTaskCommand to add the specified {@code Date}
     */
    public ClearTaskCommand(Date date) {
        requireNonNull(date);
        this.date = date;
        this.hasDate = true;
    }

    /**
     * Creates a ClearTaskCommand to clear all tasks in task list
     */
    public ClearTaskCommand() {
        this.date = null;
        this.hasDate = false;
    }


    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Task> lastShownList = model.getUnfilteredTaskList();
        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }

        //check if task list contains given date
        if (hasDate && !lastShownList
                .stream()
                .anyMatch((task) -> task.getDate()
                .equals(date))) {
            throw new CommandException(String.format(MESSAGE_INVALID_DATE, date));
        }

        if (!hasDate) {
            model.setTaskBook(new TaskBook());
            return new CommandResult(MESSAGE_SUCCESS);
        } else {
            for (int i = 0; i < lastShownList.size(); i++) {
                if (lastShownList.get(i).getDate().equals(date)) {
                    Task currentTask = lastShownList.get(i);
                    model.deleteTask(currentTask);
                    --i; //decrement the list to avoid skipping elements
                }
            }
            return new CommandResult(String.format(MESSAGE_SUCCESS_ALT, date));
        }
    }

    @Override
    public boolean equals(Object other) {
        //check to make sure boolean variable is correctly representing date object value
        assert (hasDate && date != null) || (!hasDate && date == null)
                : "Boolean variable does not represent date object";
        return other == this //check if both are the same object reference
                || (other instanceof ClearTaskCommand
                && hasDate == (((ClearTaskCommand) other).hasDate) //check if both have dates
                && (!hasDate || date.equals(((ClearTaskCommand) other).date))); //if they both don't have dates,
                //return true. Otherwise, check if dates are equivalent
    }
}

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.task.Task;

/**
 * Deletes a task identified using its index from the TaskList.
 */
public class SortTaskByDateCommand extends Command {

    public static final String COMMAND_WORD = "sort-date";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the task list by date, earlier dates first";

    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Sorted tasks by date";


    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SortTaskByDateCommand); // instanceof handles nulls
    }
}

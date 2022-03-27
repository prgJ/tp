package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.commands.AddTagCommand.MESSAGE_ADD_TAG_SUCCESS;
import static seedu.address.logic.commands.AddTagCommand.MESSAGE_DUPLICATE_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_TASKS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.name.Name;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.Date;
import seedu.address.model.task.EndTime;
import seedu.address.model.task.StartTime;
import seedu.address.model.task.Task;

public class AddTagToTaskCommand extends Command {
    public static final String COMMAND_WORD = "tag-add-t";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Add a tag to a task from our task list. "
            + "Parameters: "
            + "INDEX (must be a positive integer) "
            + "TAG NAME (must be non-empty)\n"
            + "Example: " + COMMAND_WORD + " "
            + "1 "
            + "important";
    public static final String MESSAGE_DUPlICATE_TAG_T = "This task already has this tag!";

    public final Index index;
    public final String tagName;

    public AddTagToTaskCommand(Index index, String tagName) {
        requireAllNonNull(index, tagName);

        this.index = index;
        this.tagName = tagName;
    }

    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Task> lastShownList = model.getFilteredTaskList();

        // Exception when index out of bounds
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
        Task taskToEdit = lastShownList.get(index.getZeroBased());
        Task editedTask = addTagToTask(taskToEdit);

        // Exception when a duplicate tag is added
        Tag testTag = new Tag(this.tagName);
        if (taskToEdit.getTags().contains(testTag)) {
            throw new CommandException(MESSAGE_DUPlICATE_TAG_T);
        }

        model.setTask(taskToEdit, editedTask);
        model.updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
        return new CommandResult(String.format(MESSAGE_ADD_TAG_SUCCESS, this.tagName));

    }


    /**
     * Creates and returns a {@code Task} with a new tag {@code tagName} added to
     * {@code taskToEdit}
     *
     * @param taskToEdit Person to be edited
     * @return New Task object with the tag added (tag list updated)
     */
    private Task addTagToTask(Task taskToEdit) throws CommandException {
        // Keep all other fields the same
        Name updatedName = taskToEdit.getName();
        Date updatedDate = taskToEdit.getDate();
        StartTime updatedStartTime = taskToEdit.getStartTime();
        EndTime updatedEndTime = taskToEdit.getEndTime();
        Set<Name> updatedPersons = taskToEdit.getPersons();

        // Changing tags
        // Make modifiable copy since Task#getTags returns an unmodifiable Set
        Set<Tag> tagList = new HashSet<>(taskToEdit.getTags());
        try {
            tagList.add(new Tag(this.tagName));
        } catch (Exception e) {
            throw new CommandException(Messages.MESSAGE_INVALID_TAG);
        }

        return new Task(updatedName, updatedDate, updatedStartTime, updatedEndTime, tagList, updatedPersons);

    }

    @Override
    public boolean equals(Object other) {

        return other == this // short circuit if same object
                || (other instanceof AddTagToTaskCommand // instanceof handles nulls
                && (index.equals(((AddTagToTaskCommand) other).index)
                && tagName.equals((((AddTagToTaskCommand) other).tagName))));
    }

}



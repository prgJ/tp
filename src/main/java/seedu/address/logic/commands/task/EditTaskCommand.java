package seedu.address.logic.commands.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ENDTIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STARTTIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_TASKS;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.EditTaskDescriptor;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.name.Name;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.Date;
import seedu.address.model.task.EndTime;
import seedu.address.model.task.StartTime;
import seedu.address.model.task.Task;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditTaskCommand extends Command {

    public static final String COMMAND_WORD = "edit-t";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_DATE + "DATE] "
            + "[" + PREFIX_STARTTIME + "START TIME] "
            + "[" + PREFIX_ENDTIME + "END TIME] "
            + "[" + PREFIX_TAG + "TAG]..."
            + "[" + PREFIX_CONTACT + "PERSON]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_DATE + "22-12-2022 "
            + PREFIX_ENDTIME + "23:59";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Edited Task: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the address book.";
    public static final String MESSAGE_CONTACT_NOT_FOUND_IN_LIST =
            "Unable to edit task as the person %1$s cannot be found in the current address book";
    public static final String MESSAGE_SCHEDULE_CONFLICT =
            "The person %1$s is already involved in a task at this date and time";
    public static final String MESSAGE_SCHEDULE_CONFLICT_START_END_TIME =
            "This task ends before or at its specified start time!";

    private final Index index;
    private final EditTaskDescriptor editTaskDescriptor;

    /**
     * @param index of the task in the filtered task list to edit
     * @param editTaskDescriptor details to edit the task with
     */
    public EditTaskCommand(Index index, EditTaskDescriptor editTaskDescriptor) {
        requireNonNull(index);
        requireNonNull(editTaskDescriptor);

        this.index = index;
        this.editTaskDescriptor = new EditTaskDescriptor(editTaskDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Task> lastShownList = model.getFilteredTaskList();
        List<Person> unfilteredPersonList = model.getUnfilteredPersonList();
        List<Task> unfilteredTaskList = model.getUnfilteredTaskList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        Task taskToEdit = lastShownList.get(index.getZeroBased());
        Task editedTask = createEditedTask(taskToEdit, editTaskDescriptor);

        if (!taskToEdit.isSameTask(editedTask) && model.hasTask(editedTask)) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }

        if (editedTask.hasStartEndTimeConflict()) {
            throw new CommandException(String.format(MESSAGE_SCHEDULE_CONFLICT_START_END_TIME));
        }

        Set<Name> originalPersons = taskToEdit.getPersons();
        Set<Name> editedPersons = editedTask.getPersons();

        for (Name name: editedPersons) {
            boolean notFound = true;
            for (Person person: unfilteredPersonList) {
                if (person.getName().equals(name)) {
                    notFound = false;
                }
            }

            if (!originalPersons.containsAll(editedPersons)) {
                for (Name names : editedPersons) {
                    for (Task task : unfilteredTaskList) {
                        Set<Name> nameList = task.getPersons();
                        if (nameList.contains(names)) {
                            boolean conflictExist = task.hasDateTimeConflict(editedTask);
                            if (conflictExist) {
                                throw new CommandException(String.format(MESSAGE_SCHEDULE_CONFLICT, name));
                            }
                        }
                    }
                }
            }


            if (notFound) {
                throw new CommandException(String.format(MESSAGE_CONTACT_NOT_FOUND_IN_LIST, name));
            }
        }

        model.setTask(taskToEdit, editedTask);
        model.updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
        return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, editedTask));
    }

    /**
     * Creates and returns a {@code Task} with the details of {@code taskToEdit}
     * edited with {@code editTaskDescriptor}.
     */
    private static Task createEditedTask(Task taskToEdit, EditTaskDescriptor editTaskDescriptor) {
        assert taskToEdit != null;

        Name updatedName = editTaskDescriptor.getName().orElse(taskToEdit.getName());
        Date updatedDate = editTaskDescriptor.getDate().orElse(taskToEdit.getDate());
        StartTime updatedStartTime = editTaskDescriptor.getStartTime().orElse(taskToEdit.getStartTime());
        EndTime updatedEndTime = editTaskDescriptor.getEndTime().orElse(taskToEdit.getEndTime());
        Set<Tag> updatedTags = editTaskDescriptor.getTags().orElse(taskToEdit.getTags());
        Set<Name> updatedPersons = editTaskDescriptor.getPersons().orElse(taskToEdit.getPersons());
        return new Task(updatedName, updatedDate, updatedStartTime, updatedEndTime, updatedTags, updatedPersons);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditTaskCommand)) {
            return false;
        }

        // state check
        EditTaskCommand e = (EditTaskCommand) other;
        return index.equals(e.index)
                && editTaskDescriptor.equals(e.editTaskDescriptor);
    }
}

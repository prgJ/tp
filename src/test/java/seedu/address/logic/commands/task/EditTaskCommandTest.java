package seedu.address.logic.commands.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_TASK1;
import static seedu.address.logic.commands.CommandTestUtil.DESC_TASK2;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DATE_TASK1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_TASK1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_MEET;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.TASK_FIRST_INDEX;
import static seedu.address.testutil.TypicalIndexes.TASK_SECOND_INDEX;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalTasks.getTypicalTaskBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.EditTaskDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.person.ClearPersonCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.StrategyBoard;
import seedu.address.model.UserPrefs;
import seedu.address.model.task.Task;
import seedu.address.testutil.EditTaskDescriptorBuilder;
import seedu.address.testutil.TaskBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditTaskCommand.
 */
public class EditTaskCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalTaskBook(),
            new StrategyBoard(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Task editedTask = new TaskBuilder().build();
        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder(editedTask).build();
        EditTaskCommand editTaskCommand = new EditTaskCommand(TASK_FIRST_INDEX, descriptor);

        String expectedMessage = String.format(EditTaskCommand.MESSAGE_EDIT_TASK_SUCCESS, editedTask);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getTaskBook(), new StrategyBoard(), new UserPrefs());
        expectedModel.setTask(model.getFilteredTaskList().get(0), editedTask);

        assertCommandSuccess(editTaskCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastTask = Index.fromOneBased(model.getFilteredTaskList().size());
        Task lastTask = model.getFilteredTaskList().get(indexLastTask.getZeroBased());

        TaskBuilder taskInList = new TaskBuilder(lastTask);
        Task editedTask = taskInList.withName(VALID_NAME_TASK1).withDate(VALID_DATE_TASK1)
                .withTags(VALID_TAG_MEET).build();

        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder().withName(VALID_NAME_TASK1)
                .withDate(VALID_DATE_TASK1).withTags(VALID_TAG_MEET).build();
        EditTaskCommand editTaskCommand = new EditTaskCommand(indexLastTask, descriptor);

        String expectedMessage = String.format(EditTaskCommand.MESSAGE_EDIT_TASK_SUCCESS, editedTask);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getTaskBook(), new StrategyBoard(), new UserPrefs());
        expectedModel.setTask(lastTask, editedTask);

        assertCommandSuccess(editTaskCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidStartEndTime_failure() {
        EditTaskDescriptor invalidTask = new EditTaskDescriptorBuilder().withStartTime("15:00")
                .withEndTime("13:00").build();
        EditTaskCommand editTaskCommand = new EditTaskCommand(TASK_FIRST_INDEX, invalidTask);

        String expectedMessage = String.format(EditTaskCommand.MESSAGE_SCHEDULE_CONFLICT_START_END_TIME);
        assertThrows(CommandException.class,
                expectedMessage, () -> editTaskCommand.execute(model));

        assertCommandFailure(editTaskCommand, model, expectedMessage);
    }

    @Test
    public void execute_duplicateTaskUnfilteredList_failure() {
        Task firstTask = model.getFilteredTaskList().get(TASK_FIRST_INDEX.getZeroBased());
        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder(firstTask).build();
        EditTaskCommand editTaskCommand = new EditTaskCommand(TASK_SECOND_INDEX, descriptor);

        assertCommandFailure(editTaskCommand, model, EditTaskCommand.MESSAGE_DUPLICATE_TASK);
    }

    @Test
    public void execute_invalidTaskIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredTaskList().size() + 1);
        EditTaskDescriptor descriptor = new EditTaskDescriptorBuilder().withName(VALID_NAME_TASK1).build();
        EditTaskCommand editTaskCommand = new EditTaskCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editTaskCommand, model, Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
    }

    @Test
    public void execute_personNotInList_throwsCommandException() {
        // task with one person not found in the address book -> throws an error
        EditTaskDescriptor invalidTask = new EditTaskDescriptorBuilder().withPersons("Alice").build();
        EditTaskCommand editTaskCommand = new EditTaskCommand(TASK_FIRST_INDEX, invalidTask);

        String expectedMessage = String.format(EditTaskCommand.MESSAGE_CONTACT_NOT_FOUND_IN_LIST, "Alice");
        assertThrows(CommandException.class,
                expectedMessage, () -> editTaskCommand.execute(model));

        // task with only one person out of the rest found in the address book -> throws an error
        invalidTask = new EditTaskDescriptorBuilder().withPersons("Alice", CARL.getName().fullName).build();
        EditTaskCommand editTaskCommand2 = new EditTaskCommand(TASK_FIRST_INDEX, invalidTask);

        assertThrows(CommandException.class,
                expectedMessage, () -> editTaskCommand2.execute(model));

        // task with multiple persons not found in the address book -> throws an error
        invalidTask = new EditTaskDescriptorBuilder().withPersons("Alice", "Kenny").build();
        EditTaskCommand editTaskCommand3 = new EditTaskCommand(TASK_FIRST_INDEX, invalidTask);

        assertThrows(CommandException.class,
                expectedMessage, () -> editTaskCommand3.execute(model));
    }

    @Test
    public void equals() {
        final EditTaskCommand standardCommand = new EditTaskCommand(TASK_FIRST_INDEX, DESC_TASK1);

        // same values -> returns true
        EditTaskDescriptor copyDescriptor = new EditTaskDescriptor(DESC_TASK1);
        EditTaskCommand commandWithSameValues = new EditTaskCommand(TASK_FIRST_INDEX, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearPersonCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditTaskCommand(TASK_SECOND_INDEX, DESC_TASK1)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditTaskCommand(TASK_FIRST_INDEX, DESC_TASK2)));
    }

}

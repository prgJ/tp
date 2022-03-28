package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.NOTE_FIRST_INDEX;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.EditPersonDescriptor;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ClearTaskCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.LoadCourtCommand;
import seedu.address.logic.commands.SortStrengthCommand;
import seedu.address.logic.commands.SortWeaknessCommand;
import seedu.address.logic.commands.notecommands.AddMiscCommand;
import seedu.address.logic.commands.notecommands.AddStrengthCommand;
import seedu.address.logic.commands.notecommands.AddWeaknessCommand;
import seedu.address.logic.commands.notecommands.DeleteMiscCommand;
import seedu.address.logic.commands.notecommands.DeleteStrengthCommand;
import seedu.address.logic.commands.notecommands.DeleteWeaknessCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.image.Image;
import seedu.address.model.name.PersonNameContainsKeywordsPredicate;
import seedu.address.model.note.Note;
import seedu.address.model.person.Person;
import seedu.address.model.tag.PersonTagContainsKeywordsPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.TestImageCreator;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();
    private final Note noteStub = new Note("Note Stub 1");

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_clearTask() throws Exception {
        assertTrue(parser.parseCommand(ClearTaskCommand.COMMAND_WORD
                + " " + PREFIX_DATE + "10-10-2022") instanceof ClearTaskCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_addStrength() throws Exception {
        AddStrengthCommand command = (AddStrengthCommand) parser.parseCommand(
                AddStrengthCommand.COMMAND_WORD + " " + INDEX_SECOND_PERSON.getOneBased()
                        + " " + noteStub);
        assertEquals(new AddStrengthCommand(INDEX_SECOND_PERSON, noteStub), command);
    }

    @Test
    public void parseCommand_addWeakness() throws Exception {
        AddWeaknessCommand command = (AddWeaknessCommand) parser.parseCommand(
                AddWeaknessCommand.COMMAND_WORD + " " + INDEX_SECOND_PERSON.getOneBased()
                        + " " + noteStub);
        assertEquals(new AddWeaknessCommand(INDEX_SECOND_PERSON, noteStub), command);
    }

    @Test
    public void parseCommand_addMisc() throws Exception {
        AddMiscCommand command = (AddMiscCommand) parser.parseCommand(
                AddMiscCommand.COMMAND_WORD + " " + INDEX_SECOND_PERSON.getOneBased()
                        + " " + noteStub);
        assertEquals(new AddMiscCommand(INDEX_SECOND_PERSON, noteStub), command);
    }

    @Test
    public void parseCommand_deleteStrength() throws Exception {
        DeleteStrengthCommand command = (DeleteStrengthCommand) parser.parseCommand(
                DeleteStrengthCommand.COMMAND_WORD + " " + INDEX_SECOND_PERSON.getOneBased()
                        + " " + NOTE_FIRST_INDEX.getOneBased());
        assertEquals(new DeleteStrengthCommand(INDEX_SECOND_PERSON, NOTE_FIRST_INDEX), command);
    }

    @Test
    public void parseCommand_deleteWeakness() throws Exception {
        DeleteWeaknessCommand command = (DeleteWeaknessCommand) parser.parseCommand(
                DeleteWeaknessCommand.COMMAND_WORD + " " + INDEX_SECOND_PERSON.getOneBased()
                        + " " + NOTE_FIRST_INDEX.getOneBased());
        assertEquals(new DeleteWeaknessCommand(INDEX_SECOND_PERSON, NOTE_FIRST_INDEX), command);
    }

    @Test
    public void parseCommand_deleteMisc() throws Exception {
        DeleteMiscCommand command = (DeleteMiscCommand) parser.parseCommand(
                DeleteMiscCommand.COMMAND_WORD + " " + INDEX_SECOND_PERSON.getOneBased()
                        + " " + NOTE_FIRST_INDEX.getOneBased());
        assertEquals(new DeleteMiscCommand(INDEX_SECOND_PERSON, NOTE_FIRST_INDEX), command);
    }

    @Test
    public void parseCommand_loadCourt() throws Exception {
        TestImageCreator.createTestImage();

        Image imageStub = TestImageCreator.getTestImage();
        String imageName = imageStub.imageName;

        LoadCourtCommand command = (LoadCourtCommand) parser.parseCommand(
                LoadCourtCommand.COMMAND_WORD + " " + imageName
        );
        assertEquals(new LoadCourtCommand(imageStub), command);

        TestImageCreator.deleteTestImage();

    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> nameList = Arrays.asList("foo", "bar", "baz");
        List<String> tagList = Arrays.asList("friends", "colleagues");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + "n/foo n/bar n/baz t/friends t/colleagues");
        assertEquals(new FindCommand(new PersonNameContainsKeywordsPredicate(nameList),
                new PersonTagContainsKeywordsPredicate(tagList)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_sortStrength() throws Exception {
        assertTrue(parser.parseCommand(SortStrengthCommand.COMMAND_WORD) instanceof SortStrengthCommand);
        assertTrue(parser.parseCommand(SortStrengthCommand.COMMAND_WORD + " 3") instanceof SortStrengthCommand);
    }

    @Test
    public void parseCommand_sortWeakness() throws Exception {
        assertTrue(parser.parseCommand(SortWeaknessCommand.COMMAND_WORD) instanceof SortWeaknessCommand);
        assertTrue(parser.parseCommand(SortWeaknessCommand.COMMAND_WORD + " 3") instanceof SortWeaknessCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}

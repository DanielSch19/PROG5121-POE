import com.example.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

public class MessageTest {

	// Reset Message static state before each test to ensure test isolation
	@BeforeEach
	public void resetStatics() throws Exception {
		// Clear sentMessage list
		Field sentField = Message.class.getDeclaredField("sentMessage");
		sentField.setAccessible(true);
		List<?> list = (List<?>) sentField.get(null);
		list.clear();

		// Reset usedMessageIDs
		Field usedField = Message.class.getDeclaredField("usedMessageIDs");
		usedField.setAccessible(true);
		HashSet<?> set = (HashSet<?>) usedField.get(null);
		set.clear();

		// Reset totalMessagesSent
		Field totalField = Message.class.getDeclaredField("totalMessagesSent");
		totalField.setAccessible(true);
		totalField.setInt(null, 0);
	}

	@AfterEach
	public void cleanup() {
		// No-op for now; files like messages.json may be overwritten by tests
	}

	@Test
	public void testTwoMessageScenario_sendThenDisregard() throws Exception {
		// Message 1: send
		Message m1 = new Message("Hi, Mike can you joi us for dinner tonight.", "+27718693002");
		// simulate sending: add and increment total
		addToSentList(m1);
		incrementTotalMessagesSent();

		// Message 2: disregard
		Message m2 = new Message("Hi Keegan, did you receive the payment.", "08575975889");
		// Do not add or increment for disregarded message

		// Assertions
		// totalMessagesSent should be 1
		int total = Message.returnTotalMessages();
		Assertions.assertEquals(1, total, "Total messages sent should be 1 after sending the first message and disregarding the second");

		// printMessages should contain the first message but not the second
		String printed = Message.printMessages();
		Assertions.assertTrue(printed.contains(m1.getMessageID()), "Printed messages should contain the first message ID");
		Assertions.assertTrue(printed.contains(m1.getRecipient()), "Printed messages should contain the first recipient");
		Assertions.assertTrue(printed.contains(m1.getMessageText()), "Printed messages should contain the first message text");

		Assertions.assertFalse(printed.contains(m2.getMessageID()), "Printed messages should NOT contain the second (disregarded) message ID");
	}

	@Test
	public void testValidateLength_success() {
		Message m = new Message("Short message", "+27710020030");
		String result = m.validateLength();
		Assertions.assertEquals("Message ready to explain", result, "Expected success message for short messages");
	}

	@Test
	public void testValidateLength_failure() {
		// create a message with length 260 (10 chars over the limit)
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 260; i++) sb.append('x');
		Message m = new Message(sb.toString(), "08512345678");
		String result = m.validateLength();
		Assertions.assertEquals("messgae exceeds 250 characters by 10 reduce the size", result, "Expected failure message reporting 10 excess chars");
	}

	@Test
	public void testRecipientValidation_success_and_failure() {
		// success case: international format
		Message good = new Message("Hello", "+27718693002");
		boolean ok1 = good.checkRecipientCell() == 1;
		Assertions.assertTrue(ok1, "Recipient should be valid in +27 format");
		if (ok1) System.out.println("Cell phone number captured successfully");

		// failure case: missing international code and invalid length/prefix
		Message bad = new Message("Hello", "857597588"); // too short / missing leading 0 or +27
		boolean ok2 = bad.checkRecipientCell() == 1;
		Assertions.assertFalse(ok2, "Recipient should be invalid");
		if (!ok2) System.out.println("Cell phone number is incorrectly formated or does not contain the international code, try again");
	}

	@Test
	public void testMessageHash_format() {
		Message m = new Message("Hello world from test", "+27710020030");
		String hash = m.createMessageHash(1);
		// Expected format: 2 digits : number : FIRSTWORDLASTWORD (uppercase, no spaces)
		// Regex: ^\d{2}:\d+:([A-Z]+)$ but allow letters/digits for words concatenation
		String regex = "^\\d{2}:\\d+:[A-Z0-9]+$";
		boolean matches = hash.matches(regex);
		Assertions.assertTrue(matches, "Message hash should match expected format; got: " + hash);
	}

	@Test
	public void testMessageID_created() {
		Message m = new Message("test", "+27710020030");
		String id = m.getMessageID();
		// assert it's non-null and matches 10 digits
		Assertions.assertTrue(id != null && id.matches("^\\d{10}$"), "MessageID should be a 10-digit numeric string: " + id);
	}

	@Test
	public void testProcessChoice_send_disregard_store() throws Exception {
		// send
		Message m1 = new Message("send this", "+27710020030");
		String r1 = m1.processChoice(1);
		Assertions.assertTrue(r1.equals("Message sent successfully"), "Expected 'Message sent successfully', got: " + r1);
		Assertions.assertEquals(1, Message.returnTotalMessages());
		Assertions.assertTrue(Message.printMessages().contains(m1.getMessageID()));

		// disregard
		Message m2 = new Message("ignore this", "08512345678");
		String r2 = m2.processChoice(3);
		Assertions.assertTrue(r2.equals("press 0 to delete mesaage"), "Expected disregard prompt, got: " + r2);

		// store
		Message m3 = new Message("store this", "+27710030040");
		String r3 = m3.processChoice(2);
		Assertions.assertTrue(r3.equals("message successfully stored"), "Expected store message, got: " + r3);
		// stored messages should include m3
		Assertions.assertTrue(Message.printMessages().contains(m3.getMessageID()));
	}

	// ===== Helper methods using reflection to manipulate package-private statics in Message =====
	@SuppressWarnings("unchecked")
	private void addToSentList(Message m) throws Exception {
		Field sentField = Message.class.getDeclaredField("sentMessage");
		sentField.setAccessible(true);
		List<Message> list = (List<Message>) sentField.get(null);
		list.add(m);
	}

	private void incrementTotalMessagesSent() throws Exception {
		Field totalField = Message.class.getDeclaredField("totalMessagesSent");
		totalField.setAccessible(true);
		int current = totalField.getInt(null);
		totalField.setInt(null, current + 1);
	}
}



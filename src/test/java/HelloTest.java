import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.danilopianini.lang.LangUtils;
import org.junit.Test;


public class HelloTest {

	@Test
	public void test() {
		// Run our "hello world" with output to string rather than standard out
		String[] args = new String[]{"string"};
		HelloMain.main(args);
		// Compare against a file containing the expected results
		checkResults("HelloTest.txt",HelloMain.outBuffer.toString());
	}
	
	private static void checkResults(final String file, final String result) {
		try {
			// Stream in the file to compare against
			final InputStream is = HelloTest.class.getResourceAsStream(file);
			final String expected = IOUtils.toString(is, Charsets.UTF_8);
			// Compare expected and observed results
	        assertEquals(expected,result);
        } catch (IOException e) {
            fail(LangUtils.stackTraceToString(e));
        }
    }
}

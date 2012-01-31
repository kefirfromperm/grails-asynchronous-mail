package grails.plugin.asyncmail;

import org.junit.*;

/**
 * @author Vitalii Samolovskikh aka Kefir
 */
public class ValidatorTests {
    @Test
    public void testMailbox(){
        Assert.assertTrue(Validator.isMailbox("test@example.com"));
        Assert.assertTrue(Validator.isMailbox("John Smith <test@example.com>"));

        Assert.assertFalse(Validator.isMailbox("abc"));
    }
}

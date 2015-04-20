package grails.plugin.asyncmail;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vitalii Samolovskikh aka Kefir
 */
public class ValidatorTests {
    @Test
    public void testMailbox(){
        Assert.assertTrue(Validator.isMailbox("test@example.com"));
        Assert.assertTrue(Validator.isMailbox("John Smith <test@example.com>"));

        Assert.assertFalse(Validator.isMailbox("abc"));
        Assert.assertFalse(Validator.isMailbox(""));
        Assert.assertFalse(Validator.isMailbox(" "));
    }
}

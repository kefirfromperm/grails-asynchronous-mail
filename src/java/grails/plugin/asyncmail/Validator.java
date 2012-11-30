package grails.plugin.asyncmail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validator for mailbox
 *
 * @author Vitalii Samolovskikh aka Kefir
 */
public class Validator {
    public static boolean isMailbox(String value) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(value);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}

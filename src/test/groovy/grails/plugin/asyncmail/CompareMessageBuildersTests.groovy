package grails.plugin.asyncmail

import grails.plugin.mail.MailMessageBuilder
import org.junit.Assert

/**
 * Check all methods of MailMessageBuilder are override by AsynchronousMailMessageBuilder.
 *
 * @author kefir
 */
class CompareMessageBuildersTests extends GroovyTestCase {
    void testBuildersMethods() {
        def ammbMethods = AsynchronousMailMessageBuilder.metaClass.methods;

        MailMessageBuilder.metaClass.methods.each {MetaMethod mbm ->
            if (mbm.isPublic()) {
                Assert.assertNotNull "Method ${mbm.name} not found.", ammbMethods.find {MetaMethod ammbm ->
                    ammbm.isPublic() &&
                            ammbm.name == mbm.name &&
                            ammbm.returnType == mbm.returnType &&
                            ammbm.signature == mbm.signature;
                }
            }
        }
    }
}

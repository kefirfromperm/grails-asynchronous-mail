package grails.plugin.asyncmail
import grails.plugins.mail.MailMessageBuilder
import spock.lang.Specification
/**
 * @author Vitalii Samolovskikh aka Kefir, Puneet Behl
 */
class CompareMessageBuilderSpec extends Specification {

    void "testing builder methods"() {
        setup:
        def ammbMethods  = AsynchronousMailMessageBuilder.metaClass.methods
        def mbMethods = MailMessageBuilder.metaClass.methods

        expect:
        mbMethods.every {MetaMethod mbm->
            mbm.isPublic() && ammbMethods.find {it.isPublic() && it.name == mbm.name && it.returnType == mbm.returnType && it.signature == mbm.signature}
        }
    }
}

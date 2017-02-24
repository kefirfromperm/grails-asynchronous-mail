package grails.plugin.asyncmail.enums

import groovy.transform.CompileStatic

/**
 * @author Vitalii Samolovskikh aka Kefir
 */
@CompileStatic
enum MessageStatus {
    CREATED, ATTEMPTED, SENT, ERROR, EXPIRED, ABORT
}

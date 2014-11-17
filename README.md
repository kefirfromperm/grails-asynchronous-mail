The Grails Asynchronous Mail plugin
====================================

Description
-----------

Grails Asynchronous Mail is a plugin for sending email messages asynchronously. It persists email messages to the database
with Grails domain classes and sends them by a scheduled Quartz job. Mail is sent on a different thread, with the
`sendAsynchronousMail` (or `sendMail`) method returning instantly, not waiting for the mail to be actually sent. If the SMTP
server isn't available, or other errors occur, the plugin can be set to retry later.

The plugin depends on the [quartz](http://www.grails.org/plugin/quartz) and [mail](http://www.grails.org/plugin/mail)
plugins. You also need a persistence provider plugin: [hibernate](http://www.grails.org/plugin/hibernate),
[hibernate4](http://www.grails.org/plugin/hibernate4) and [mongodb](http://www.grails.org/plugin/mongodb) are supported.

Links
-----

* The plugin main page: <http://grails.org/plugin/asynchronous-mail>
* The VCS repository (GitHub): <https://github.com/kefirfromperm/grails-asynchronous-mail>
* The issue tracker (Jira): <http://jira.grails.org/browse/GPASYNCHRONOUSMAIL>
* The page at OpenHUB: <https://www.openhub.net/p/grails-asynchronous-mail>

Installation
------------

To install, add the plugin to the plugins block of `BuildConfig.groovy`:
```groovy
compile ":asynchronous-mail:1.2-RC2"
```

Documentation
-------------

Full documentation is available at [the plugin page](http://grails.org/plugin/asynchronous-mail).

Also see the sample application at <https://github.com/kefirfromperm/grails-asynchronous-mail-sample>.

Contribution
------------

If you want to contribute to the plugin, open a pull request to the repository
<https://github.com/kefirfromperm/grails-asynchronous-mail>.

Unit tests are very very sweet things. They help us find bugs, and modify code without adding new bugs. It's very interesting to
see how they work. I like to see how they work. What is the better than unit tests? More unit tests!
Unit tests are good!

And comments... Comments are good also. They are not better than unit tests, but they are definitely good. If you known
Chinese or Arabic, that is good. Seriously. It's awesome! But I don't known them. So write comments in English.

Logging
-------

To enable full logging for the plugin, add the following lines to `/grails-app/conf/Config.grovy`.
```groovy
log4j = {
    ...
    // Enable Asynchronous Mail plugin logging
    trace 'grails.app.jobs.grails.plugin.asyncmail',
          'grails.app.services.grails.plugin.asyncmail',
          'grails.plugin.asyncmail'

    // Enable Quartz plugin logging
    debug 'grails.plugins.quartz'
    ...
}
```

Issue tracking
--------------

You can report bugs on [JIRA](http://jira.grails.org/browse/GPASYNCHRONOUSMAIL) or
[GitHub](https://github.com/kefirfromperm/grails-asynchronous-mail/issues?state=open).
You also can ask me questions by email [kefirfromperm@gmail.com](mailto:kefirfromperm@gmail.com).
Please, enable logs and attach them to your issue.

Please, review this project at [OpenHUB](https://www.openhub.net/p/grails-asynchronous-mail).

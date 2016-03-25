The Grails Asynchronous Mail plugin
====================================

Description
-----------

Grails Asynchronous Mail is a plugin for sending email messages asynchronously. It persists email messages to the
database with Grails domain classes and sends them by a scheduled Quartz job. Mail is sent on a different thread, with
the `sendAsynchronousMail` (or `sendMail`) method returning instantly, not waiting for the mail to be actually sent. If
the SMTP server isn't available, or other errors occur, the plugin can be set to retry later.

The plugin depends on the [quartz](http://www.grails.org/plugin/quartz) and [mail](http://www.grails.org/plugin/mail)
plugins. You also need a persistence provider plugin:
[hibernate4](https://grails.github.io/grails-data-mapping/latest/hibernate/manual/guide/index.html) and
[mongodb](https://grails.github.io/grails-data-mapping/latest/mongodb/manual/index.html) are supported.

Links
-----

* The old plugin page: <http://grails.org/plugin/asynchronous-mail>
* The VCS repository (GitHub): <https://github.com/kefirfromperm/grails-asynchronous-mail>
* The issue tracker (GitHub): <https://github.com/kefirfromperm/grails-asynchronous-mail/issues>
* The repository package (BinTray): <https://bintray.com/kefirsf/plugins/asynchronous-mail/>
* The page at OpenHUB: <https://www.openhub.net/p/grails-asynchronous-mail>

Installation
------------

To install, add the plugin to the plugins block of `build.gradle`:
```groovy
compile "org.grails.plugins:asynchronous-mail:2.0.0.RC2"
```

Documentation
-------------

Full documentation is available at [the old plugin page](http://grails.org/plugin/asynchronous-mail).

Also see the sample application at <https://github.com/kefirfromperm/grails-asynchronous-mail-sample>.

Contribution
------------

If you want to contribute to the plugin, open a pull request to the repository
<https://github.com/kefirfromperm/grails-asynchronous-mail>.

Unit tests are very very sweet things. They help us find bugs, and modify code without adding new bugs. It's very
interesting to see how they work. I like to see how they work. What is the better than unit tests? More unit tests!
Unit tests are good!

And comments... Comments are good also. They are not better than unit tests, but they are definitely good. If you known
Chinese or Arabic, that is good. Seriously. It's awesome! But I don't known them. So write comments in English.

Logging
-------

To enable full logging for the plugin, add the following lines to `/grails-app/conf/logback.groovy`.
```groovy
    ...
    // Enable Asynchronous Mail plugin logging
    logger('grails.app.jobs.grails.plugin.asyncmail', TRACE, ['STDOUT'])
    logger('grails.app.services.grails.plugin.asyncmail', TRACE, ['STDOUT'])
    logger('grails.plugin.asyncmail', TRACE, ['STDOUT'])

    // Enable Quartz plugin logging
    logger('grails.plugins.quartz', DEBUG, ['STDOUT'])
    ...
```

Issue tracking
--------------

You can report bugs on [GitHub](https://github.com/kefirfromperm/grails-asynchronous-mail/issues?state=open).
You also can ask me questions by email [kefirfromperm@gmail.com](mailto:kefirfromperm@gmail.com).
Please, enable logs and attach them to your issue.

Please, review this project at [OpenHUB](https://www.openhub.net/p/grails-asynchronous-mail).

Donation
--------

If you want to give me a beer just send some money to <https://www.paypal.me/kefir>

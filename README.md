The Grails Asynchronous Mail plug-in
====================================

Description
-----------

The Grails Asynchronous Mail is a plugin for asynchronous sending of email messages. It stores email messages in the DB
using Grails domain classes and sends them by a scheduled job. It allows to react to user's actions faster. If the SMTP
server isn't available in time then the plugin can sends message after, when the server will be available.

The plugin depends on [hibernate](http://www.grails.org/plugin/hibernate),
[quartz](http://www.grails.org/plugin/quartz) and [mail](http://www.grails.org/plugin/mail) plugins.

Links
-----

The plugin page: <http://grails.org/plugin/asynchronous-mail>
Repository (GitHub): <https://github.com/kefirfromperm/grails-asynchronous-mail>
Issue tracker (Jira): <http://jira.grails.org/browse/GPASYNCHRONOUSMAIL>

Installation
------------

For start to use the plugin just add a dependency in the `BuildConfig.groovy`.
```groovy
compile ":asynchronous-mail:1.0-RC6"
```

Documentation
-------------

Full documentation is available on [the plugin page](http://grails.org/plugin/asynchronous-mail).

Contribution
------------

If you want to contribute the plugin just open a pull request to repository
<https://github.com/kefirfromperm/grails-asynchronous-mail>.

Unit tests are very very sweet things. They help us find bugs, modify code without new bugs. It's very interesting to
see how they work. I like to see how they work. What is the better than unit tests? More unit tests!
Unit tests are good!

And comments... Comments are good also. They are not better than unit tests, but they are good, definitely. If you known
Chinese or Arabic it is good. Seriously. It's awesome! But I don't known them. So write comments in English.

Logging
-------

For enable full plugin log add following lines to the configuration (`/grails-app/conf/Config.grovy`).
```groovy
log4j = {
    ...
    // Enable the Asynchronous Mail plugin logging
    trace 'grails.app.jobs.grails.plugin.asyncmail', 'grails.app.services.grails.plugin.asyncmail'

    // Enable the Quartz plugin logging
    debug 'grails.plugins.quartz'
    ...
}
```

Issue tracking
--------------

You can report about bugs on the [JIRA](http://jira.grails.org/browse/GPASYNCHRONOUSMAIL) or
[GitHub](https://github.com/kefirfromperm/grails-asynchronous-mail/issues?state=open).
You also can ask me by email [kefir@perm.ru](mailto:kefir@perm.ru).

Please, enable logs and attach it to your issue.

Please, review this project on [Ohloh](https://www.ohloh.net/p/grails-asynchronous-mail).

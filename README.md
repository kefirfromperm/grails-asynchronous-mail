The Grails Asynchronous Mail plug-in
====================================

Description
-----------

The Grails Asynchronous Mail is a plugin for asynchronous sending of email messages. It stores email messages in the DB
using Grails domain classes and sends them by a scheduled job. It allows to react to user's actions faster. If the SMTP
server isn't available in time then the plugin can sends message after, when the server will be available.

The plugin depends on [quartz](http://www.grails.org/plugin/quartz) and [mail](http://www.grails.org/plugin/mail)
plugins. You also need a persistence provider plugin. [hibernate](http://www.grails.org/plugin/hibernate),
[hibernate4](http://www.grails.org/plugin/hibernate4) and [mongodb](http://www.grails.org/plugin/mongodb) are supported.

Links
-----

The plugin main page: <http://grails.org/plugin/asynchronous-mail>  
The VCS repository (GitHub): <https://github.com/kefirfromperm/grails-asynchronous-mail>  
The issue tracker (Jira): <http://jira.grails.org/browse/GPASYNCHRONOUSMAIL>  
The page at OpenHUB: <https://www.openhub.net/p/grails-asynchronous-mail>

Installation
------------

For start to use the plugin just add a dependency in the `BuildConfig.groovy`.
```groovy
compile ":asynchronous-mail:1.1"
```

Documentation
-------------

Full documentation is available at [the plugin page](http://grails.org/plugin/asynchronous-mail).

Also see the sample application at <https://github.com/kefirfromperm/grails-asynchronous-mail-sample>.

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
    trace 'grails.app.jobs.grails.plugin.asyncmail', 
          'grails.app.services.grails.plugin.asyncmail',
          'grails.plugin.asyncmail'

    // Enable the Quartz plugin logging
    debug 'grails.plugins.quartz'
    ...
}
```

Issue tracking
--------------

You can report about bugs on the [JIRA](http://jira.grails.org/browse/GPASYNCHRONOUSMAIL) or
[GitHub](https://github.com/kefirfromperm/grails-asynchronous-mail/issues?state=open).
You also can ask me by email [kefirfromperm@gmail.com](mailto:kefirfromperm@gmail.com).
Please, enable logs and attach it to your issue.

Please, review this project on [OpenHUB](https://www.openhub.net/p/grails-asynchronous-mail).

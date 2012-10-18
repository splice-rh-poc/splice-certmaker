Dev installation
================

These instructions are for Fedora 17. These instructions are for eclipse development.

First, do the normal candlepin buildr instructions:

 * `sudo yum install ruby rubygems ruby-devel gcc perl-Locale-Msgfmt tomcat6 java-1.7.0-openjdk-devel`
 * `sudo gem update --system`
 * `export JAVA_HOME=/usr/lib/jvm/java-1.7.0/ (WARNING: you may want 1.6.0 depending on OS version)`
 * `gem install bundler`
 * `bundle install`

At this point, you should be able to do a `buildr eclipse`, which will pull down a lot of jars. A `buildr clean package` will run the unit tests and create a jar that you can run.


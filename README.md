Dev installation
================

These instructions are for Fedora 17 with an eclipse development.

First, do the normal candlepin buildr instructions:

 * `sudo yum install ruby rubygems ruby-devel gcc perl-Locale-Msgfmt tomcat6 java-1.7.0-openjdk-devel`
 * `sudo gem update --system`
 * `export JAVA_HOME=/usr/lib/jvm/java-1.7.0/ (WARNING: you may want 1.6.0 depending on OS version)`
 * `gem install bundler`
 * `bundle install`

At this point, you should be able to do a `buildr eclipse`, which will pull
down a lot of jars. A `buildr clean package` will run the unit tests and create
a jar that you can run.

Code conventions
================

I tried to [avoid naming classes with "-er"
names](http://objology.blogspot.com/2011/09/one-of-best-bits-of-programming-advice.html),
especially "manager".  The exception is when a class inherits from a "-er"
class. Also, I tried to put as many tests in JUnit as possible, and reserved
spec tests only for concurrency testing. If you find a bug, please write a
JUnit test for it instead of a spec test if possible.

TODO
====

 * use a shared config file with other splice apps
 * _very important_ configure logging level via shared config
 * figure out how to handle product data
 * possibly write out the keypairs and serial numbers. This isn't needed, but might be worthwhile
 * get someone to review my code
 * performance test this code to see how many requests per sec it can handle

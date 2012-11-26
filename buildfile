require "buildr/checkstyle"

# Version number for this release
VERSION_NUMBER = "1.0.0"
# Group identifier for your projects
GROUP = "splice-certmaker"
COPYRIGHT = ""

# Specify Maven 2.0 remote repositories here, like this:
repositories.remote << "http://repo1.maven.org/maven2"
repositories.remote << "http://ec2-23-22-86-129.compute-1.amazonaws.com/pub/jars"

CANDLEPIN = 'candlepin:candlepin-certgen:jar:0.7.19'

BOUNCYCASTLE = group('bcprov-jdk16', :under=>'org.bouncycastle', :version=>'1.46')

LOG4J = 'log4j:log4j:jar:1.2.14'
DAEMON = transitive 'org.kohsuke:akuma:jar:1.7'

GUICE = 'com.google.inject:guice:jar:3.0'
JETTY = ['org.mortbay.jetty:jetty:jar:6.1.26',
         'org.mortbay.jetty:jetty-util:jar:6.1.26',
           'javax.servlet:servlet-api:jar:2.5']

HIBERNATE = ['org.hibernate:hibernate-core:jar:3.3.2.GA',
             'org.hibernate:hibernate-annotations:jar:3.4.0.GA',
             'org.hibernate:hibernate-commons-annotations:jar:3.3.0.ga',
             'javax.persistence:persistence-api:jar:1.0',
             'org.hibernate:hibernate-entitymanager:jar:3.4.0.GA',
             'org.hibernate:hibernate-tools:jar:3.2.4.GA']


COMMONS = ['commons-codec:commons-codec:jar:1.4',
            'commons-collections:commons-collections:jar:3.1',
            'commons-lang:commons-lang:jar:2.5']

COLLECTIONS = 'com.google.collections:google-collections:jar:1.0'

INI4J = 'org.ini4j:ini4j:jar:0.5.2'

JACKSON = [group('jackson-core-lgpl',
                 'jackson-mapper-lgpl',
                 'jackson-jaxrs',
                 'jackson-xc',
                 :under => 'org.codehaus.jackson',
                 :version => '1.9.2')]

# mockito 1.9.5 is required, to fix http://code.google.com/p/mockito/issues/detail?id=53
JUNIT = ['junit:junit:jar:4.5', 'org.mockito:mockito-all:jar:1.9.5']

SLF4J = [ 'org.slf4j:slf4j-api:jar:1.5.8',
         'org.slf4j:slf4j-log4j12:jar:1.6.1']



desc "The Splice-certmaker project"
define "splice-certmaker" do

  project.version = VERSION_NUMBER
  project.group = GROUP
  manifest["Implementation-Vendor"] = COPYRIGHT
  compile.with [DAEMON, CANDLEPIN, BOUNCYCASTLE, COMMONS, COLLECTIONS, JACKSON, LOG4J, HIBERNATE, GUICE, JETTY, SLF4J, INI4J] # Add classpath dependencies
  package(:jar)

  test.with [JUNIT, COMMONS]
end

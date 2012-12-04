module CertmakerMethods

  def create_certmaker()

    m2_repo = "../../.m2/repository"
    # please keep this sorted
    classpath="target/splice-certmaker-1.0.0.jar:" + 
              "#{m2_repo}/aopalliance/aopalliance/1.0/aopalliance-1.0.jar:" +
              "#{m2_repo}/candlepin/candlepin-certgen/0.7.21/candlepin-certgen-0.7.21.jar:" +
              "#{m2_repo}/com/google/collections/google-collections/1.0/google-collections-1.0.jar:" +
              "#{m2_repo}/com/google/inject/guice/3.0/guice-3.0.jar:" +
              "#{m2_repo}/commons-codec/commons-codec/1.4/commons-codec-1.4.jar:" +
              "#{m2_repo}/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:" +
              "#{m2_repo}/commons-lang/commons-lang/2.5/commons-lang-2.5.jar:" +
              "#{m2_repo}/com/sun/akuma/akuma/1.4/akuma-1.4.jar:" +
              "#{m2_repo}/javax/inject/javax.inject/1/javax.inject-1.jar:" +
              "#{m2_repo}/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:" +
              "#{m2_repo}/log4j/log4j/1.2.14/log4j-1.2.14.jar:" +
              "#{m2_repo}/org/apache/openejb/javaee-api/5.0-2/javaee-api-5.0-2.jar:" +
              "#{m2_repo}/org/bouncycastle/bcprov-jdk16/1.46/bcprov-jdk16-1.46.jar:" +
              "#{m2_repo}/org/codehaus/jackson/jackson-core-lgpl/1.9.2/jackson-core-lgpl-1.9.2.jar:" +
              "#{m2_repo}/org/codehaus/jackson/jackson-jaxrs/1.9.2/jackson-jaxrs-1.9.2.jar:" +
              "#{m2_repo}/org/codehaus/jackson/jackson-mapper-lgpl/1.9.2/jackson-mapper-lgpl-1.9.2.jar:" +
              "#{m2_repo}/org/ini4j/ini4j/0.5.2/ini4j-0.5.2.jar:" +
              "#{m2_repo}/org/jboss/resteasy/jaxrs-api/2.3.1.GA/jaxrs-api-2.3.1.GA.jar:" +
              "#{m2_repo}/org/jboss/resteasy/resteasy-jackson-provider/2.3.5.Final/resteasy-jackson-provider-2.3.5.Final.jar:" +
              "#{m2_repo}/org/jboss/resteasy/resteasy-jaxrs/2.3.4.Final/resteasy-jaxrs-2.3.4.Final.jar:" +
              "#{m2_repo}/org/jboss/resteasy/resteasy-multipart-provider/2.3.5.Final/resteasy-multipart-provider-2.3.5.Final.jar:" +
              "#{m2_repo}/org/mortbay/jetty/jetty/6.1.26/jetty-6.1.26.jar:" +
              "#{m2_repo}/org/mortbay/jetty/jetty-util/6.1.26/jetty-util-6.1.26.jar:" +
              "#{m2_repo}/org/quartz-scheduler/quartz/2.1.5/quartz-2.1.5.jar:" +
              "#{m2_repo}/org/scannotation/scannotation/1.0.2/scannotation-1.0.2.jar:" +
              "#{m2_repo}/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar:" +
              "#{m2_repo}/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:" +
              "/usr/share/java/jna.jar"

    exec_str = "java -cp #{classpath} org.candlepin.splice.Main"
    pipe = IO.popen(exec_str, "w+")
    #this is perlesque
    while pipe.gets()
      break if $_ =~ /server started!/
    end 
    return pipe
  end
end

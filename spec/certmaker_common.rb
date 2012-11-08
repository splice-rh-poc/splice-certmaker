module CertmakerMethods

  def create_certmaker()

    m2_repo = "../../.m2/repository"

    classpath="target/splice-certmaker-1.0.0.jar:#{m2_repo}/candlepin/candlepin-certgen/0.7.16/candlepin-certgen-0.7.16.jar:#{m2_repo}/org/quartz-scheduler/quartz/2.1.5/quartz-2.1.5.jar:#{m2_repo}/commons-lang/commons-lang/2.5/commons-lang-2.5.jar:#{m2_repo}/log4j/log4j/1.2.14/log4j-1.2.14.jar:#{m2_repo}/org/bouncycastle/bcprov-jdk16/1.46/bcprov-jdk16-1.46.jar:#{m2_repo}/org/codehaus/jackson/jackson-mapper-lgpl/1.9.2/jackson-mapper-lgpl-1.9.2.jar:#{m2_repo}/org/codehaus/jackson/jackson-core-lgpl/1.9.2/jackson-core-lgpl-1.9.2.jar:#{m2_repo}/commons-codec/commons-codec/1.4/commons-codec-1.4.jar:#{m2_repo}/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:#{m2_repo}/com/google/collections/google-collections/1.0/google-collections-1.0.jar:#{m2_repo}/com/google/inject/guice/3.0/guice-3.0.jar:#{m2_repo}/javax/inject/javax.inject/1/javax.inject-1.jar:#{m2_repo}/aopalliance/aopalliance/1.0/aopalliance-1.0.jar:#{m2_repo}/org/mortbay/jetty/jetty/6.1.26/jetty-6.1.26.jar:#{m2_repo}/org/mortbay/jetty/jetty-util/6.1.26/jetty-util-6.1.26.jar:#{m2_repo}/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:#{m2_repo}/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:#{m2_repo}/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar:#{m2_repo}/com/sun/akuma/akuma/1.4/akuma-1.4.jar:/usr/share/java/jna.jar"

    exec_str = "java -cp #{classpath} org.candlepin.splice.Main"
    pipe = IO.popen(exec_str, "w+")
    #this is perlesque
    while pipe.gets()
      break if $_ =~ /server started!/
    end 
    return pipe
  end
end
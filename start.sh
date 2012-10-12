#!/bin/bash

M2=../../.m2/repository

CP=target/splice-certmaker-1.0.0.jar:$M2/candlepin/candlepin-full/0.7.12/candlepin-full-0.7.12.jar:$M2/org/quartz-scheduler/quartz/2.1.5/quartz-2.1.5.jar:$M2/commons-lang/commons-lang/2.5/commons-lang-2.5.jar:$M2/log4j/log4j/1.2.14/log4j-1.2.14.jar:$M2/org/bouncycastle/bcprov-jdk16/1.46/bcprov-jdk16-1.46.jar:$M2/org/codehaus/jackson/jackson-mapper-lgpl/1.9.2/jackson-mapper-lgpl-1.9.2.jar:$M2/org/codehaus/jackson/jackson-core-lgpl/1.9.2/jackson-core-lgpl-1.9.2.jar:$M2/commons-codec/commons-codec/1.4/commons-codec-1.4.jar:$M2/commons-collections/commons-collections/3.1/commons-collections-3.1.jar:$M2/com/google/collections/google-collections/1.0/google-collections-1.0.jar:$M2/com/google/inject/guice/3.0/guice-3.0.jar:$M2/javax/inject/javax.inject/1/javax.inject-1.jar:$M2/aopalliance/aopalliance/1.0/aopalliance-1.0.jar

java -cp $CP org.candlepin.splice.Main $1

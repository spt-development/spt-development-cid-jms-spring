````
  ____  ____ _____   ____                 _                                  _   
 / ___||  _ \_   _| |  _ \  _____   _____| | ___  _ __  _ __ ___   ___ _ __ | |_ 
 \___ \| |_) || |   | | | |/ _ \ \ / / _ \ |/ _ \| '_ \| '_ ` _ \ / _ \ '_ \| __|
  ___) |  __/ | |   | |_| |  __/\ V /  __/ | (_) | |_) | | | | | |  __/ | | | |_ 
 |____/|_|    |_|   |____/ \___| \_/ \___|_|\___/| .__/|_| |_| |_|\___|_| |_|\__|
                                                 |_|                                           
 cid-jms-spring------------------------------------------------------------------
````

[![build_status](https://travis-ci.com/spt-development/spt-development-cid-jms-spring.svg?branch=main)](https://travis-ci.com/spt-development/spt-development-cid-jms-spring)

Library for integrating [spt-development/spt-development-cid](https://github.com/spt-development/spt-development-cid)
into a Spring JMS (listener) project.

Usage
=====

Register the Aspect as a Spring Bean manually or by adding the  
[spt-development/spt-development-cid-jms-spring-boot](https://github.com/spt-development/spt-development-cid-jms-spring-boot)
starter to your project's pom.

    @Bean
    @Order(0)
    public CorrelationIdSetter correlationIdSetter() {
        return new CorrelationIdSetter();
    }

Building locally
================

To build the library, run the following maven command:

    $ mvn clean install

Release
=======

To build a release and upload to Maven Central run the following maven command:

    $ export GPG_TTY=$(tty) # Required on Mac OS X
    $ mvn deploy -DskipTests -Prelease

NOTE. This is currently a manual step as not currently integrated into the build.
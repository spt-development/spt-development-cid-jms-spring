````
  ____  ____ _____   ____                 _                                  _   
 / ___||  _ \_   _| |  _ \  _____   _____| | ___  _ __  _ __ ___   ___ _ __ | |_ 
 \___ \| |_) || |   | | | |/ _ \ \ / / _ \ |/ _ \| '_ \| '_ ` _ \ / _ \ '_ \| __|
  ___) |  __/ | |   | |_| |  __/\ V /  __/ | (_) | |_) | | | | | |  __/ | | | |_ 
 |____/|_|    |_|   |____/ \___| \_/ \___|_|\___/| .__/|_| |_| |_|\___|_| |_|\__|
                                                 |_|                                           
 cid-jms-spring------------------------------------------------------------------
````

[![build_status](https://github.com/spt-development/spt-development-cid-jms-spring/actions/workflows/build.yml/badge.svg)](https://github.com/spt-development/spt-development-cid-jms-spring/actions)

Library for integrating [spt-development/spt-development-cid](https://github.com/spt-development/spt-development-cid)
into a Spring JMS (listener) project.

Usage
=====

Register the aspects as a Spring Beans manually or by adding the  
[spt-development/spt-development-cid-jms-spring-boot](https://github.com/spt-development/spt-development-cid-jms-spring-boot)
starter to your project's pom.

```java
@Bean
@Order(0)
public CorrelationIdSetter correlationIdSetter() {
    return new CorrelationIdSetter();
}

@Bean
@Order(1)
public MdcCorrelationIdPutter mdcCorrelationIdPutter() {
    return new MdcCorrelationIdPutter();
}
```

Building locally
================

To build the library, run the following maven command:

```shell
$ ./mvnw clean install
```

Release
=======

To build a release and upload to Maven Central push to `main`.
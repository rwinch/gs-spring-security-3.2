About
=================

Source Code and materials for Getting Started with Spring Security 3.2. To run the samples import the project into Spring Tool Suite using the Gradle Eclipse plugin. You can view the slides at http://rwinch.github.io/gs-spring-security-3.2/

Running with Gradle
======================

1. Ensure you have java installed
2. cd gs-spring-security-3.2
3. Start the application with ./gradlew tomcatRun or .\gradlew.bat tomcatRun

Browse to http://localhost:8080/sample/

Running the sample project in Spring Tool Suite
==================

The following provides information on setting up a development environment that can run the sample in [Spring Tool Suite 3.3.0+](http://www.springsource.org/sts). Other IDE's should work using Gradle's IDE support, but have not been tested.

* IDE Setup
  * Install Spring Tool Suite 3.3.0+
  * You will need the following plugins installed (can be found on the Extensions Page)
	* Gradle Eclipse
* Importing the project into Spring Tool Suite
  * File->Import...->Gradle Project
* Right click the project and click Run As -> Run on Server

The sample will be available at http://localhost:8080/sample/

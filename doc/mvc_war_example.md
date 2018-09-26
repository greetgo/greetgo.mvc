### References

 - [Concept](concept.md)
 - [Example project mvc.war.example (Quick setup and launch)]
 - [Specification of controllers](controller_spec.md)

### Example project mvc.war.example

To describe the work of the library, the developers prepared an example project, which demonstrated all the possibilities of
this library. The example project  is in `greetgo.mvc.war.example` module. All controllers are in `kz.greetgo.mvc.war.example.controllers` packageв of this module. 

`war` subproject is also defined inside `greetgo.mvc.war.example`, where war file is assembled. `com.bmuschko.tomcat` plugin was added to `war` subproject for convinient running a war file through apache-tomcat. When launching `runMvcWarExample`, war file assembling, apache-tomcat running and application deploying are carried out.

`war` subproject has `webapps/jsp/` directory, which you can find all examples of calling Rest-services. For example, `webapps/request_parameters/base_example.jsp` file has basic examples of calling Rest-services with parameters.

### Quick setup and launch

To launch the example project, first it is necessary to install the following software:
 - java jdk 1.8+
 - gradle 3.5+ (https://gradle.org/)
 - git 2.7.4+ (https://ru.wikipedia.org/wiki/Git)

If software is installed [go here](#install).

In Ubuntu, it is possible to do this by running the following commands:

    sudo apt-get install openjdk-8-jdk git

It is better not to install Gradle from the repository - there is probably too old version. For the correct installation, you should
download gradle from an official source, for example:

    wget https://downloads.gradle.org/distributions/gradle-3.5.1-bin.zip
    
Unzip, for example, with a help of the command:

    unzip path/to/gradle-3.5.1-bin.zip

Create `bin` folder in the user's home folder, for example, with a help of the command:

    mkdir ~/bin

And make a symbolic reference, for example, with a help of the command:

    ln -s path/to/gradle-3.5.1/bin/gradle ~/bin/gradle

If `~/bin` folder is not listed in the environment variable `$PATH`, then it is necessary to restart the computer
Then you can run the command:

    gradle -version

Then you should get somethimg like this

    
    ------------------------------------------------------------
    Gradle 3.5
    ------------------------------------------------------------
    
    Build time:   2017-04-10 13:37:25 UTC
    Revision:     b762622a185d59ce0cfc9cbc6ab5dd22469e18a6
    
    Groovy:       2.4.10
    Ant:          Apache Ant(TM) version 1.9.6 compiled on June 29 2015
    JVM:          1.8.0_144 (Oracle Corporation 25.144-b01)
    OS:           Linux 4.10.0-35-generic amd64

### Installing example project

After installing the necessary software, proceed directly to the example project,
to do this download the repository with the project, running the command:

    git clone https://github.com/greetgo/greetgo.mvc.git

Enter the directory:

    cd greetgo.mvc/greetgo.mvc.parent/

And ran the command:

    gradle runMvcWarExample

This command will assemble the example project and run apache-tomcat with this project. At the end a message will be displayed:

    The Server is running at http://localhost:10000/mvc_example

Clicking on the specified link, we get into the example application.

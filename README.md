 <a name="#documentr_top"></a>

> **This project requires JVM version of at least 1.7**






<a name="documentr_heading_0"></a>

# routemaster <sup><sup>[top](documentr_top)</sup></sup>



> lightweight web server with RESTful routing






<a name="documentr_heading_1"></a>

# Table of Contents <sup><sup>[top](documentr_top)</sup></sup>



 - [routemaster](#documentr_heading_0)
 - [Table of Contents](#documentr_heading_1)
 - [Now with Templar templating!](#documentr_heading_2)
 - [Now with Modules](#documentr_heading_3)
   - [Options](#documentr_heading_5)
   - [As an aside](#documentr_heading_6)


# RouteMaster

Handy routing controller for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embeddable HTTP Java server.

See [https://synapticloop.github.io/routemaster/](https://synapticloop.github.io/routemaster/) for full documentation

![routemaster home page](https://raw.github.com/synapticloop/routemaster/master/src/main/wiki/images/nano-httpd-home.png)



<a name="documentr_heading_2"></a>

# Now with Templar templating! <sup><sup>[top](documentr_top)</sup></sup>

Yes, you can now do some server side includes, using the synapticloop templar templating language!



<a name="documentr_heading_3"></a>

# Now with Modules <sup><sup>[top](documentr_top)</sup></sup>

To make it quicker and easier to get started with routemaster, we added in module 
functionality so that it can be easily extended.

### Step 1:

Download the `routemaster-<version>-server.jar`  from the [https://github.com/synapticloop/routemaster/releases](github releases directory) 
which contains all of the dependencies that are required




<a name="documentr_heading_5"></a>

## Options <sup><sup>[top](documentr_top)</sup></sup>

Following the NanoHTTPD, you may pass in the following options



```
- h      
--host   The host to bind to - default 127.0.0.1

-p
--port   The port to bind to - default 5474

-d
--dir    The directory that the html files reside in

```





<a name="documentr_heading_6"></a>

## As an aside <sup><sup>[top](documentr_top)</sup></sup>



The name ```RouteMaster``` pays homage to the original London buses [(see the wikipedia entry)](http://en.wikipedia.org/wiki/Routemaster) which would take you wherever you would like to go.

The favicon is also related, showing a depiction of the back top window of a routemaster bus.


# Building the Package

## *NIX/Mac OS X

From the root of the project, simply run

`./gradlew build`


## Windows

`./gradlew.bat build`


This will compile and assemble the artefacts into the `build/libs/` directory.

Note that this may also run tests (if applicable see the Testing notes)

# Running the Tests

## *NIX/Mac OS X

From the root of the project, simply run

`gradle --info test`

if you do not have gradle installed, try:

`gradlew --info test`

## Windows

From the root of the project, simply run

`gradle --info test`

if you do not have gradle installed, try:

`./gradlew.bat --info test`


The `--info` switch will also output logging for the tests

# Artefact Publishing - Github

This project publishes artefacts to [GitHub](https://github.com/)

> Note that the latest version can be found [https://github.com/synapticloop/routemaster/releases](https://github.com/synapticloop/routemaster/releases)

As such, this is not a repository, but a location to download files from.

# Artefact Publishing - Bintray

This project publishes artefacts to [bintray](https://bintray.com/)

> Note that the latest version can be found [https://bintray.com/synapticloop/maven/routemaster/view](https://bintray.com/synapticloop/maven/routemaster/view)

## maven setup

this comes from the jcenter bintray, to set up your repository:

```


<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd' xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>bintray</name>
          <url>http://jcenter.bintray.com</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>bintray-plugins</name>
          <url>http://jcenter.bintray.com</url>
        </pluginRepository>
      </pluginRepositories>
      <id>bintray</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>bintray</activeProfile>
  </activeProfiles>
</settings>


```

## gradle setup

Repository

```


repositories {
	maven {
		url  "http://jcenter.bintray.com" 
	}
}


```

or just

```


repositories {
	jcenter()
}


```

## Dependencies - Gradle

```


dependencies {
	runtime(group: 'synapticloop', name: 'routemaster', version: '2.0.0', ext: 'jar')

	compile(group: 'synapticloop', name: 'routemaster', version: '2.0.0', ext: 'jar')
}


```

or, more simply for versions of gradle greater than 2.1

```


dependencies {
	runtime 'synapticloop:routemaster:2.0.0'

	compile 'synapticloop:routemaster:2.0.0'
}


```

## Dependencies - Maven

```


<dependency>
	<groupId>synapticloop</groupId>
	<artifactId>routemaster</artifactId>
	<version>2.0.0</version>
	<type>jar</type>
</dependency>


```

## Dependencies - Downloads


You will also need to download the following dependencies:



### cobertura dependencies

  - `net.sourceforge.cobertura:cobertura:2.0.3`: (It may be available on one of: [bintray](https://bintray.com/net.sourceforge.cobertura/maven/cobertura/2.0.3/view#files/net.sourceforge.cobertura/cobertura/2.0.3) [mvn central](http://search.maven.org/#artifactdetails|net.sourceforge.cobertura|cobertura|2.0.3|jar))


### compile dependencies

  - `synapticloop:templar:1.4.2`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/templar/1.4.2/view#files/synapticloop/templar/1.4.2) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|templar|1.4.2|jar))
  - `org.nanohttpd:nanohttpd:2.3.1`: (It may be available on one of: [bintray](https://bintray.com/org.nanohttpd/maven/nanohttpd/2.3.1/view#files/org.nanohttpd/nanohttpd/2.3.1) [mvn central](http://search.maven.org/#artifactdetails|org.nanohttpd|nanohttpd|2.3.1|jar))


### runtime dependencies

  - `synapticloop:templar:1.4.2`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/templar/1.4.2/view#files/synapticloop/templar/1.4.2) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|templar|1.4.2|jar))
  - `org.nanohttpd:nanohttpd:2.3.1`: (It may be available on one of: [bintray](https://bintray.com/org.nanohttpd/maven/nanohttpd/2.3.1/view#files/org.nanohttpd/nanohttpd/2.3.1) [mvn central](http://search.maven.org/#artifactdetails|org.nanohttpd|nanohttpd|2.3.1|jar))


### server dependencies

  - `org.nanohttpd:nanohttpd:2.3.1`: (It may be available on one of: [bintray](https://bintray.com/org.nanohttpd/maven/nanohttpd/2.3.1/view#files/org.nanohttpd/nanohttpd/2.3.1) [mvn central](http://search.maven.org/#artifactdetails|org.nanohttpd|nanohttpd|2.3.1|jar))


### testCompile dependencies

  - `junit:junit:4.11`: (It may be available on one of: [bintray](https://bintray.com/junit/maven/junit/4.11/view#files/junit/junit/4.11) [mvn central](http://search.maven.org/#artifactdetails|junit|junit|4.11|jar))
  - `org.mockito:mockito-all:1.10.19`: (It may be available on one of: [bintray](https://bintray.com/org.mockito/maven/mockito-all/1.10.19/view#files/org.mockito/mockito-all/1.10.19) [mvn central](http://search.maven.org/#artifactdetails|org.mockito|mockito-all|1.10.19|jar))
  - `synapticloop:templar:1.4.2`: (It may be available on one of: [bintray](https://bintray.com/synapticloop/maven/templar/1.4.2/view#files/synapticloop/templar/1.4.2) [mvn central](http://search.maven.org/#artifactdetails|synapticloop|templar|1.4.2|jar))

**NOTE:** You may need to download any dependencies of the above dependencies in turn (i.e. the transitive dependencies)

# License

```


The MIT License (MIT)

Copyright (c) 2017 synapticloop

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

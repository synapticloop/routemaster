# RouteMaster

Handy routing controller for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embeddable HTTP Java server.


![routemaster home page](https://raw.github.com/synapticloop/routemaster/master/src/main/wiki/images/nano-httpd-home.png)

# Now with Templar templating!

Yes, you can now do some server side includes, using the synapticloop templar templating language!

## How do I run RouteMaster?

The quickest way is to grab a release from the [releases](https://github.com/synapticloop/routemaster/releases) page.

### Which release?

 - `routemaster-example.zip`
 - `routemaster-templar.jar`
 - `routemaster.jar`

The `routemaster-example.zip` contains `routemaster-templar.jar` with the files served from the file system with the example site. 

Unzip the folder, change to the `routemaster` directory - and run

```
java -jar routemaster-templar.jar
```

point a browser at [http://localhost:5474](http://localhost:5474)
and away you go.

All of the files are server from the directory in which you started the server.

`routemaster-templar.jar`

This is the routemaster jar file with templar templating engine bundeled in with it.

 - `routemaster.jar`

This is the plain routemaster jar file without nay other dependencies.

## How do I build RouteMaster?

you will need the following:

  + java (6 or higher)
  + ant

then run

```
ant -f build-ant-github.xml
ant dist 
```

which will download all of the nanohttpd components and build the RouteMaster, and place it in the dist directory.

You will need to create a ```routemaster.properties``` file which is required to be either

  + in the root of your classpath (e.g. src/main/java).  

or

  + in the same directory from whence the programme was launched

See ```routemaster.properties``` file for more information.

You will also need to have a mimetypes.properties file which is also required to be either

  + in the root of your classpath (e.g. src/main/java).  

or

  + in the same directory from whence the programme was launched

This will allow you to over-ride the mimetypes that are served up.

## Options

Following the NanoHTTPD, you may pass in the following options

```
- h      
--host   The host to bind to - default 127.0.0.1

-p
--port   The port to bind to - default 5474

-d
--dir    The directory that the html files reside in

```

## As an aside

The name ```RouteMaster``` pays homage to the original London buses [(see the wikipedia entry)](http://en.wikipedia.org/wiki/Routemaster) which would take you wherever you would like to go.

The favicon is also related, showing a depiction of the back top window of a routemaster bus.

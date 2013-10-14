RouteMaster
===========
Handy routing controller for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embeddable HTTP Java server.

![routemaster home page](https://raw.github.com/synapticloop/routemaster/master/src/main/wiki/images/nano-httpd-home.png)


## How do I run RouteMaster?

The quickest way to get it up and running is to download ```dist/routemaster.jar``` and run

```
java -jar routemaster.jar
```

This will get it up and running on port 5474, although this won't actually do too much

## How do I get a nice demo up and running?

  1. Download the ```dist/routemaster-full.zip``` file
  1. unzip the file, which will create a ```routemaster``` directory
  1. go into the directory
  1. and run ```java -jar routemaster.jar```
  1. point a browser at [http://localhost:5474](http://localhost:5474)
  1. done!!!

## How do I build RouteMaster?

you will need the following:

  + java (6 or higher)
  + ant

then run

```
ant download-dependencies dist 
```

which will download all of the nanohttpd components and build the RouteMaster, and place it in the dist directory.

You will need to create a ```routemaster.properties``` file which is required to be either

  + in the root of your classpath (e.g. src/main/java).  

or

  + in the same directory from whence the programme was launched

See ```routemaster.properties``` file for more information.

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

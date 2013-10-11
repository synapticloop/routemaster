RouteMaster
===========
Handy routing controller for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embeddable HTTP Java server.

## How do I run RouteMaster?

The quickest way to get it up and running is to download ```dist/routemaster.jar``` and run

```
java -jar routemaster.jar
```

This will get it up and running on port 5474, although this won't actually do too much

## How do I get a nice demo up and running?

  1 clone this repository 

```
git clone https://github.com/synapticloop/routemaster.git
```

  2 change to the routemaster directory

```
cd routemaster
```

  3 build the project

```
ant download-dependencies dist-full
```

  4 run the demo server

```
java -jar dist/routemaster-full.jar -d src/main/html/
```

This will run the server on port 5474

  5 point your browser to [http://localhost:5474/](http://localhost:5474/)

## How do I build RouteMaster?

you will need the following:

  + java (6 or higher)
  + ant

then run

```
ant download-dependencies dist 
```

which will download all of the nanohttpd components and build the RouteMaster, 
and place it in the dist directory.

You will need to create a ```routemaster.properties``` file which is required to be in the root of your classpath (e.g. src/main/java).  See ```routemaster.properties``` file for more information.

# This is a work in progress at the moment

## As an aside

The name ```RouteMaster``` pays homage to the original London buses [(see the wikipedia entry)](http://en.wikipedia.org/wiki/Routemaster) which would take you wherever you would like to go.

The favicon is also related, showing a depiction of the back top window of a routemaster bus.

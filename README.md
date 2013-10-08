RouteMaster
===========
Handy routing controller for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embeddable HTTP Java server.

## How do I use RouteMaster?

you will need the following:

  + java (6 or higher)
  + ant

then run

```
ant download-depandencies build 
```

whicch will download all of the nanohttpd components and build the RouteMaster

You will need to create a ```routemaster.properties``` file which is required to be in the root of your classpath (e.g. src/main/java).  See ```routemaster.example.properties``` file for more information.

# This is a work in progress at the moment
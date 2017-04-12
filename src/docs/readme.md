# RouteMaster

Handy routing controller for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embeddable HTTP Java server.

See [https://synapticloop.github.io/routemaster/](https://synapticloop.github.io/routemaster/) for full documentation

![routemaster home page](https://raw.github.com/synapticloop/routemaster/master/src/main/wiki/images/nano-httpd-home.png)

# Now with Templar templating!

Yes, you can now do some server side includes, using the synapticloop templar templating language!

# Now with Modules

To make it quicker and easier to get started with routemaster, we added in module 
functionality so that it can be easily extended.

### Instructions:

Download the `routemaster-<version>-server.jar`  from the [github releases directory](https://github.com/synapticloop/routemaster/releases) *(which contains all of the dependencies that are required)*, download modules that you require and place them in the `modules` directory from where you launched the routemaster server.  

These will be automatically registered and ready to go.


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

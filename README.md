#Gatekeeper: NGINX co-process for SOA-style infrastructures

Gatekeeper turns NGINX into your edge service. Gatekeeper watches your [Zookeeper](http://zookeeper.apache.org/) service
discovery paths and dynamically builds NGINX upstreams for them. Load balancers (ELBs especially) can be SPOFs and are
the first steps to madness. Cut out the middle man and let NGINX manage your upstream load balancing. 

Gatekeeper uses [Mustache.js](http://mustache.github.io) templates to build upstreams and locations using your
existing `nginx.conf`.

    events {
        worker_connections  4096;
    }

    http {
        {{#clusters}}
            upstream {{clusterName}} {
            {{#servers}}
                server {{host}}:{{port}};
            {{/servers}}
                keepalive 24;
            }
        {{/clusters}}
        server {
            port 80;

            {{#locations}}
                location {{context}} {
                {{#upstream}}
                    proxy_pass              {{protocol}}://{{clusterName}};
                {{/upstream}}
                {{#attributes}}
                    {{key}}                 {{value}}
                {{/attributes}}
                }
            {{/locations}}
        }
    }
    
With two small blocks of customizable templating, each time a node is registered or deregistered a new `nginx.conf`
is written and nginx sent a `SIGHUP`.

## Running

Build the agent as you would normally

    mvn package

A jar will be built in `gatekeeper-agent/target/gatekeeper-agent-1.0.0.jar`. Take this jar and put it on your NGINX servers.

You can run the agent as below:

    java -jar gatekeeper.jar -c /path/to/gatekeeper.properties
    
I prefer to run Gatekeeper under [Supervisord](http://supervisord.org/). A config for Gatekeeper looks like this:

    [program:library-api]
    directory=/opt/gatekeeper
    command=java -Xmx128m -Xss256k -jar gatekeeper-agent.jar -c gatekeeper.properties
    redirect_stderr=true
    stdout_logfile=/var/log/gatekeeper.log

## Disclaimer

This isn't currently used in production. It's a minorly tested utility.

## Maven
You'll generally want to compile this yourself, but it's available on Clojars if you want:

Core:

    <dependency>
        <groupId>com.xorlev.gatekeeper</groupId>
        <artifactId>gatekeeper-core</artifactId>
        <version>1.0.0</version>
    </dependency>

## Service Discovery Implementations

Currently only supports Netflix Curator Service Discovery Extensions. Other implementations are trivial and can be
 implemented by including gatekeeper-core, and including your dependency in the resulting gatekeeper-agent build.

## FAQ

### Why Java?
I'm not good at C, I believe I'd make a leaky C-module. I am confident in my ability to write a stable Java co-process.
Additionally, Netflix's Curator library is also top-notch, better than any stock Zookeeper library around.

##License
Copyright 2013 Michael Rose

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the
 License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

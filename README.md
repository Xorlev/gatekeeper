#Gatekeeper: NGINX co-process for SOA architectures

Gatekeeper watches your [Zookeeper](http://zookeeper.apache.org/) service discovery paths and dynamically builds NGINX
upstreams for them. Load balancers (ELBs especially) are SPOFs are the first steps to madness. Cut out the middle man
and let NGINX manage your upstream load balancing.

This project has similar goals to Hipache, but leveraging the scalability and proven performance of NGINX.

Gatekeeper uses [Mustache.js](http://mustache.github.io) templates to build upstreams and locations using your
existing `nginx.conf`.

	{{#clusters}}
	    upstream {{clusterName}} {
	    {{#servers}}
	        server {{host}}:{{port}};
	    {{/servers}}
	        keepalive 24;
	    }
	{{/clusters}}
	...
    {{#locations}}
        location {{context}} {
        {{#upstream}}
            proxy_pass              {{protocol}}://{{clusterName}}
        {{/upstream}}
        {{#attributes}}
            {{key}}                 {{value}}
        {{/attributes}}
        }
    {{/locations}}
	
With two small blocks of customizable templating, each time a node is registered or deregistered a new `nginx.conf`
is written and nginx sent a `SIGHUP`.

## Disclaimer

This isn't currently used in production. It's a minorly tested utility.

## Maven

Core:

	<dependency>
		<groupId>com.xorlev.gatekeeper</groupId>
		<artifactId>gatekeeper-core</artifactId>
		<version>0.1.0-SNAPSHOT</version>
	</dependency>

## Service Discovery Implementations

Currently only supports Netflix Curator Service Discovery Extensions. Other implementations are trivial and can be
 implemented by including gatekeeper-core.

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

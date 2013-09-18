package com.xorlev.gatekeeper.web.resources;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.xorlev.gatekeeper.data.Cluster;
import com.xorlev.gatekeeper.providers.ZookeeperClusterProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 2013-09-18
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/clusters")
public class ClusterResource {
    private final ZookeeperClusterProvider zookeeperClusterProvider;

    public ClusterResource(ZookeeperClusterProvider zookeeperClusterProvider) {
        this.zookeeperClusterProvider = zookeeperClusterProvider;
    }

    @GET
    public List<Cluster> clusters() {
        return zookeeperClusterProvider.clusters();
    }

    @GET
    @Path("/{cluster-name}")
    public Optional<Cluster> clusters(@PathParam("cluster-name") final String clusterName) {
        return Iterables.tryFind(zookeeperClusterProvider.clusters(), new Predicate<Cluster>() {
            public boolean apply(Cluster cluster) {
                return cluster.getClusterName().equals(clusterName);
            }
        });
    }
}

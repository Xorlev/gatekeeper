package com.xorlev.gatekeeper.data;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Structure representing an entire upstream server group
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Getter
@EqualsAndHashCode
@ToString
public class Cluster implements Serializable {
    private final String protocol;
    private final String clusterName;
    private final int port;
    private final ImmutableSet<Server> servers;

    public Cluster(Builder builder) {
        checkArgument(!Strings.isNullOrEmpty(builder.protocol.trim()), "protocol must be set");
        checkArgument(!Strings.isNullOrEmpty(builder.clusterName.trim()), "clusterName must be set");
        checkNotNull(builder.servers, "servers must be set");

        this.protocol = builder.protocol;
        this.clusterName = builder.clusterName;
        this.port = builder.port;
        this.servers = ImmutableSet.copyOf(builder.servers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String protocol = "http";
        private String clusterName;
        private int port = 80;
        private Collection<Server> servers;

        private Builder() {}

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder clusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder servers(Collection<Server> servers) {
            this.servers = servers;
            return this;
        }

        public Cluster build() {
            return new Cluster(this);
        }
    }
}

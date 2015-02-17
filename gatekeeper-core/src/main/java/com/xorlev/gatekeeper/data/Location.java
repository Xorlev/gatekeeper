package com.xorlev.gatekeeper.data;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;

/**
 * 2013-07-27
 *
 * @author Michael Rose <elementation@gmail.com>
 */
@Data
public class Location implements Serializable {

	private static final long serialVersionUID = -2376121548547370212L;
	
	@NonNull String context;
    @NonNull Cluster upstream;
    Map<String,String> attributes = Maps.newHashMap();
}

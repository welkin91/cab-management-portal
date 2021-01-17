package org.example.cab_management_portal.models.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

    @JsonProperty("lat")
    Double latitude;

    @JsonProperty("lng")
    Double longitude;
}

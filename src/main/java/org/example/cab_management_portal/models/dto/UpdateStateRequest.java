package org.example.cab_management_portal.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.cab_management_portal.models.CabState;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStateRequest {

    @JsonProperty("cab_id")
    String registrationNumber;

    @JsonProperty("state")
    CabState state;
}

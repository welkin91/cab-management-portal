package org.example.cab_management_portal.models.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.example.cab_management_portal.models.CabState;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusUpdate {

    @JsonProperty("cab_id")
    String registrationNumber;

    @JsonProperty("state")
    CabState state;
}

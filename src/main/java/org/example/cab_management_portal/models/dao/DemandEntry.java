package org.example.cab_management_portal.models.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DemandEntry {

    @JsonProperty("city")
    String cityId;

    @JsonProperty("priority")
    Integer priority;

    @JsonProperty("max_priority")
    Integer maxPriority;

    @JsonProperty("max_priority_at")
    Long maxPriorityTimestamp;
}

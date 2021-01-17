package org.example.cab_management_portal.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityInput {

    @JsonProperty("allowed_cities")
    Set<String> allowedCities;
}

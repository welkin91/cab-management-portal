package org.example.cab_management_portal.core.storage;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.models.CityInput;
import org.example.cab_management_portal.utils.ClassTransformationUtil;
import org.example.cab_management_portal.utils.CommonUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class CityHandler {

    public Set<String> getBaseAllowedCities() {
        Set<String> response = new HashSet<>();

        try {
            String input = CommonUtils.getJsonStringFromFile(
                    "/dummy_data/baseCities.json"
            );
            CityInput cityInput = ClassTransformationUtil.fromString(input, CityInput.class);
            response = cityInput.getAllowedCities();
        } catch (IOException e) {
            log.error("Unable to find cities file. Initializing empty city list.");
        }

        return response;
    }
}

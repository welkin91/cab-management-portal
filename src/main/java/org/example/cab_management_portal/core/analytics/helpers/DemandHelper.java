package org.example.cab_management_portal.core.analytics.helpers;

import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dao.DemandEntry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public interface DemandHelper {

    public List<DemandEntry> getHighDemandCityData(PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap) throws AnalyticsException;

    public boolean addDemand(
            Map<String, DemandEntry> _cityLevelHighDemands,
            PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap,
            String city
    );

    public boolean removeDemand (
            Map<String, DemandEntry> _cityLevelHighDemands,
            PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap,
            String city
    );
}

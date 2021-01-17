package org.example.cab_management_portal.core.analytics.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dao.DemandEntry;
import org.example.cab_management_portal.utils.CommonUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class DemandHelperImpl implements DemandHelper {
    @Override
    public List<DemandEntry> getHighDemandCityData(PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap) throws AnalyticsException {
        if(_highDemandMaxHeap.size() == 0) {
            throw new AnalyticsException("Demand is not high in any of the registered cities.");
        }

        List<DemandEntry> response = new ArrayList<>();

        Iterator iterator = _highDemandMaxHeap.iterator();
        while (iterator.hasNext()) {
            DemandEntry entry = (DemandEntry) iterator.next();
            response.add(entry);
        }

        return response;
    }

    @Override
    public boolean addDemand(Map<String, DemandEntry> _cityLevelHighDemands, PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap, String city) {
        if(CommonUtils.isEmpty(city)) {
            return false;
        }

        DemandEntry demandEntry = _cityLevelHighDemands.get(city);
        if(demandEntry != null) {
            _highDemandMaxHeap.remove(demandEntry);
        }
        else {
            demandEntry = DemandEntry.builder()
                    .cityId(city)
                    .priority(0)
                    .maxPriority(0)
                    .build();
        }

        demandEntry.setPriority(demandEntry.getPriority() + 1);
        if(demandEntry.getPriority() > demandEntry.getMaxPriority()) {
            demandEntry.setMaxPriority(demandEntry.getPriority());
            demandEntry.setMaxPriorityTimestamp(System.currentTimeMillis());
        }

        _highDemandMaxHeap.offer(demandEntry);
        _cityLevelHighDemands.put(city, demandEntry);

        return true;
    }

    @Override
    public boolean removeDemand(Map<String, DemandEntry> _cityLevelHighDemands, PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap, String city) {
        if(CommonUtils.isEmpty(city)) {
            return false;
        }

        DemandEntry demandEntry = _cityLevelHighDemands.get(city);
        if(demandEntry == null) {
            return false;
        }

        _highDemandMaxHeap.remove(demandEntry);

        demandEntry.setPriority(0);

        _cityLevelHighDemands.put(city, demandEntry);
        _highDemandMaxHeap.offer(demandEntry);

        return true;
    }
}

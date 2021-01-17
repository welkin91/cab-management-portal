package org.example.cab_management_portal.core.analytics;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.core.analytics.helpers.DemandHelper;
import org.example.cab_management_portal.core.analytics.helpers.StateHistoryHelper;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.CabEntry;
import org.example.cab_management_portal.models.dao.DemandEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Slf4j
public class AnalyticsStorageImpl implements AnalyticsStorage {

    @Autowired
    StateHistoryHelper stateHelper;

    @Autowired
    DemandHelper demandHelper;

    Map<String, ConcurrentNavigableMap<Long, CabEntry>> _allStates;
    Map<String, DemandEntry> _cityLevelHighDemands;
    PriorityBlockingQueue<DemandEntry> _highDemandMaxHeap;

    @Override
    public void init() {
        _allStates = new ConcurrentHashMap<>();
        _cityLevelHighDemands = new ConcurrentHashMap<>();
        _highDemandMaxHeap = new PriorityBlockingQueue<>(
                2^31 -1,
                (a, b) -> b.getPriority() - a.getPriority()
        );
    }

    /**
     * Updates latest status of a cab.
     * This is used in to find total IDLE time of a cab, All states of a cab.
     * @param cab
     * @return  Return True in case of success.
     *          Return False in case of any error.
     */
    @Override
    public boolean updateCabStatus(Cab cab) {
        return stateHelper.updateCabStatus(_allStates, cab);
    }

    /**
     *
     * @param registrationNumber
     * @param startTime
     * @param endTime
     * @return  Total time a cab was IDLE in the provided timeslots.
     *          Returns exception in case, no data point is found in the time interval.
     * @throws AnalyticsException
     */
    public Long getCabIdleTimeInMillis(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException {
        return stateHelper.getCabIdleTimeInMillis(_allStates, registrationNumber, startTime, endTime);
    }

    /**
     *
     * @param registrationNumber
     * @param startTime
     * @param endTime
     * @return List<CabEntry> between the provided time interval.
     * @throws AnalyticsException
     */
    @Override
    public List<CabEntry> getCabStates(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException {
        return stateHelper.getCabStates(_allStates, registrationNumber, startTime, endTime);
    }

    @Override
    public List<DemandEntry> getHighDemandCityData() throws AnalyticsException {
        return demandHelper.getHighDemandCityData(_highDemandMaxHeap);
    }

    @Override
    public boolean addDemand(String city) {
        return demandHelper.addDemand(_cityLevelHighDemands, _highDemandMaxHeap, city);
    }

    @Override
    public boolean removeDemand(String city) {
        return demandHelper.removeDemand(_cityLevelHighDemands, _highDemandMaxHeap, city);
    }
}

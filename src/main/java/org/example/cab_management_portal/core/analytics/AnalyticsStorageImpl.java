package org.example.cab_management_portal.core.analytics;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.CabEntry;
import org.example.cab_management_portal.service.transformers.CabTransformer;
import org.example.cab_management_portal.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Slf4j
public class AnalyticsStorageImpl implements AnalyticsStorage {

    @Autowired
    CabTransformer cabTransformer;

    Map<String, ConcurrentNavigableMap<Long, CabEntry>> _allStates;

    @Override
    public void init() {
        _allStates = new ConcurrentHashMap<>();
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
        /**
         * Check if cab is not empty.
         */
        if(cab == null || CommonUtils.isEmpty(cab.getRegistrationNumber())) {
            return false;
        }

        String registrationNumber = cab.getRegistrationNumber();
        if(!_allStates.containsKey(registrationNumber)) {
            _allStates.put(registrationNumber, new ConcurrentSkipListMap<>());
        }

        CabEntry entry = null;
        try {
            entry = cabTransformer.transformIntoEntry(cab, cab.getState());
        } catch (TransformationException exception) {
            log.error("Error while transforming into Cab Entry object. error: {}", exception.getMessage());
        }

        if(entry == null) {
            return false;
        }

        /**
         * Add cab entry in all states map.
         */
        _allStates.get(registrationNumber).put(entry.getLastUpdatedAt(), entry);

        return true;
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
        Long response = 0L;

        /**
         * Check if start time exists.
         */
        if(startTime == null) {
            throw new AnalyticsException("start time not present.");
        }

        /**
         * check if end time exists.
         */
        if(endTime == null) {
            throw new AnalyticsException("end time not present.");
        }

        /**
         * check if cab id is presnt.
         */
        if(CommonUtils.isEmpty(registrationNumber)) {
            throw new AnalyticsException("Cab Id can not be empty.");
        }

        /**
         * check if data is present for a cab or not.
         */
        if(!_allStates.containsKey(registrationNumber)) {
            throw new AnalyticsException("No data present for cabId: '" + registrationNumber + "'.");
        }

        ConcurrentNavigableMap<Long, CabEntry> timeIntervals = null;

        try {
            /**
             * Extract all the cab entry objects between given time interval.
             */
            timeIntervals = _allStates.get(registrationNumber).subMap(startTime, true, endTime, true);
        }
        catch (Exception e) {
            throw new AnalyticsException("Something went wrong. error: " + e.toString());
        }

        Iterator iterator = timeIntervals.navigableKeySet().iterator();

        return getIdleTime(iterator, registrationNumber, startTime, endTime);
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
        /**
         * check if start time is present.
         */
        if(startTime == null) {
            throw new AnalyticsException("start time not present.");
        }

        /**
         * check if end time is present.
         */
        if(endTime == null) {
            throw new AnalyticsException("end time not present.");
        }

        /**
         * check if car id is present.
         */
        if(CommonUtils.isEmpty(registrationNumber)) {
            throw new AnalyticsException("Cab Id can not be empty.");
        }

        if(!_allStates.containsKey(registrationNumber)) {
            throw new AnalyticsException("No data present for cabId: '" + registrationNumber + "'.");
        }

        ConcurrentNavigableMap<Long, CabEntry> timeIntervals = null;

        try {
            /**
             * extract all the data between the time interval.
             * Please note that the time intervals are inclusive in nature.
             */
            timeIntervals = _allStates.get(registrationNumber).subMap(startTime, true, endTime, true);
        }
        catch (Exception e) {
            throw new AnalyticsException("Something went wrong. error: " + e.toString());
        }

        List<CabEntry> response = new ArrayList<>();
        Iterator iterator = timeIntervals.navigableKeySet().iterator();

        /**
         * Iterate over all the data in sorted fashion of time.
         */
        while (iterator.hasNext()) {
            Long time = (Long) iterator.next();
            CabEntry entry = _allStates.get(registrationNumber).get(time);

            if(response.size() > (2^31 -1)) {
                response.remove(0);
            }

            response.add(entry);
        }

        return response;
    }

    /**
     *
     * @param iterator
     * @param registrationNumber
     * @param startTime
     * @param endTime
     * @return Total time a cab was in IDLE state.
     */
    private Long getIdleTime(Iterator iterator, String registrationNumber, Long startTime, Long endTime) {
        Long response = 0L;

        long currTime = System.currentTimeMillis();
        if(endTime > currTime) {
            endTime = currTime;
        }

        CabEntry start = null;
        CabEntry end = null;

        while (iterator.hasNext()) {
            Long time = (Long) iterator.next();
            CabEntry entry = _allStates.get(registrationNumber).get(time);

            switch (entry.getState()) {
                case IDLE:
                    start = entry;
                    break;

                case TRIP_ASSIGNED:
                    end = entry;
                    response += getTimeDiff(start, end);
                    start = null;
                    end = null;
                    break;
            }
        }

        if(start != null) {
            response += (endTime - start.getLastUpdatedAt());
        }

        return response;
    }

    private Long getTimeDiff(CabEntry start, CabEntry end) {
        if(start == null || end == null) {
            return 0L;
        }

        return end.getLastUpdatedAt() - start.getLastUpdatedAt();
    }
}

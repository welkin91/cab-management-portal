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

    @Override
    public boolean updateCabStatus(Cab cab) {
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

        _allStates.get(registrationNumber).put(entry.getLastUpdatedAt(), entry);

        return true;
    }

    public Long getCabIdleTimeInMillis(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException {
        Long response = 0L;

        if(startTime == null) {
            throw new AnalyticsException("start time not present.");
        }

        if(endTime == null) {
            throw new AnalyticsException("end time not present.");
        }

        if(CommonUtils.isEmpty(registrationNumber)) {
            throw new AnalyticsException("Cab Id can not be empty.");
        }

        if(!_allStates.containsKey(registrationNumber)) {
            throw new AnalyticsException("No data present for cabId: '" + registrationNumber + "'.");
        }

        ConcurrentNavigableMap<Long, CabEntry> timeIntervals = null;

        try {
            timeIntervals = _allStates.get(registrationNumber).subMap(startTime, true, endTime, true);
        }
        catch (Exception e) {
            throw new AnalyticsException("Something went wrong. error: " + e.toString());
        }

        Iterator iterator = timeIntervals.navigableKeySet().iterator();

        return getIdleTime(iterator, registrationNumber, startTime, endTime);
    }

    @Override
    public List<CabEntry> getCabStates(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException {
        if(startTime == null) {
            throw new AnalyticsException("start time not present.");
        }

        if(endTime == null) {
            throw new AnalyticsException("end time not present.");
        }

        if(CommonUtils.isEmpty(registrationNumber)) {
            throw new AnalyticsException("Cab Id can not be empty.");
        }

        if(!_allStates.containsKey(registrationNumber)) {
            throw new AnalyticsException("No data present for cabId: '" + registrationNumber + "'.");
        }

        ConcurrentNavigableMap<Long, CabEntry> timeIntervals = null;

        try {
            timeIntervals = _allStates.get(registrationNumber).subMap(startTime, true, endTime, true);
        }
        catch (Exception e) {
            throw new AnalyticsException("Something went wrong. error: " + e.toString());
        }

        List<CabEntry> response = new ArrayList<>();
        Iterator iterator = timeIntervals.navigableKeySet().iterator();

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

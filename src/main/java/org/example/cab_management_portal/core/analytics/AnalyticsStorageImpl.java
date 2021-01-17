package org.example.cab_management_portal.core.analytics;

import lombok.extern.slf4j.Slf4j;
import org.example.cab_management_portal.exceptions.AnalyticsException;
import org.example.cab_management_portal.exceptions.TransformationException;
import org.example.cab_management_portal.models.CabState;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.CabEntry;
import org.example.cab_management_portal.service.transformers.CabTransformer;
import org.example.cab_management_portal.utils.CommonUtils;
import org.example.cab_management_portal.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Slf4j
public class AnalyticsStorageImpl implements AnalyticsStorage {

    @Autowired
    CabTransformer cabTransformer;

    Map<String, ConcurrentNavigableMap<Long, CabEntry>> _idleCabs;

    @Override
    public void init() {
        _idleCabs = new ConcurrentHashMap<>();
    }

    @Override
    public boolean updateCabIdleStatus(Cab cab, CabState state) {
        if(cab == null || CommonUtils.isEmpty(cab.getRegistrationNumber())) {
            return false;
        }

        String registrationNumber = cab.getRegistrationNumber();
        if(!_idleCabs.containsKey(registrationNumber)) {
            _idleCabs.put(registrationNumber, new ConcurrentSkipListMap<>());
        }

        CabEntry entry = null;
        try {
            entry = cabTransformer.transformIntoEntry(cab, state);
        } catch (TransformationException exception) {
            log.error("Error while transforming into Cab Entry object. error: {}", exception.getMessage());
        }

        if(entry == null) {
            return false;
        }

        _idleCabs.get(registrationNumber).put(entry.getLastUpdatedAt(), entry);

        iterate();

        return true;
    }

    public Long getCabIdleTimeInMillis(String registrationNumber, Long startTime, Long endTime) throws AnalyticsException {

        iterate();

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

        if(!_idleCabs.containsKey(registrationNumber)) {
            throw new AnalyticsException("No data present for cabId: '" + registrationNumber + "'.");
        }

        ConcurrentNavigableMap<Long, CabEntry> timeIntervals = null;

        try {
            timeIntervals = _idleCabs.get(registrationNumber).subMap(startTime, true, endTime, true);
        }
        catch (Exception e) {
            throw new AnalyticsException("Something went wrong. error: " + e.toString());
        }

        Iterator iterator = timeIntervals.navigableKeySet().iterator();

        return getIdleTime(iterator, registrationNumber, startTime, endTime);
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
            CabEntry entry = _idleCabs.get(registrationNumber).get(time);

            switch (entry.getState()) {
                case IDLE_START:
                    start = entry;
                    break;

                case IDLE_END:
                    end = entry;
                    response += getTimeDiff(start, end);
                    start = null;
                    end = null;
                    break;
            }

            log.error(GsonUtils.toPrettyString(entry));
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

    public void iterate() {
        for(Map.Entry<String, ConcurrentNavigableMap<Long, CabEntry>> entry: _idleCabs.entrySet()) {
            String cabId = entry.getKey();
            ConcurrentNavigableMap<Long, CabEntry> map = entry.getValue();

            log.error("cabId: {}",cabId);
            Iterator iterator = map.navigableKeySet().iterator();
            while (iterator.hasNext()) {
                Long time = (Long) iterator.next();
                log.error(GsonUtils.toString(time));
                log.error(GsonUtils.toString(_idleCabs.get(cabId).get(time)));
            }
            log.error("---------------------------------");
        }
    }
}

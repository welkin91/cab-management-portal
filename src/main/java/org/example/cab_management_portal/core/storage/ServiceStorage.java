package org.example.cab_management_portal.core.storage;

import org.example.cab_management_portal.exceptions.LocationException;
import org.example.cab_management_portal.exceptions.RegistrationException;
import org.example.cab_management_portal.exceptions.StateException;
import org.example.cab_management_portal.exceptions.TripException;
import org.example.cab_management_portal.models.dao.Cab;
import org.example.cab_management_portal.models.dao.LocationUpdate;
import org.example.cab_management_portal.models.dao.StatusUpdate;

public interface ServiceStorage {

    public void init();

    public boolean registerCab(Cab cab) throws RegistrationException;

    public boolean registerCity(String city) throws RegistrationException;

    public boolean updateLocation(LocationUpdate locationUpdate) throws LocationException;

    public boolean updateCabState(StatusUpdate statusUpdate) throws StateException;

    public Cab bookIdleCab(String city) throws TripException;
}

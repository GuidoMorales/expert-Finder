package com.egg.expertfinder.service;

import com.egg.expertfinder.entity.Location;
import com.egg.expertfinder.exception.EntityNotFoundException;
import com.egg.expertfinder.repository.LocationRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public Location createLocation(String country, String address) throws IllegalArgumentException {

        validate(address);
        
        Location location = new Location(country, address);

        return locationRepository.save(location);
    }

    @Transactional
    public void updateLocation(Long id, String country, String address) throws EntityNotFoundException {
        Optional<Location> response = locationRepository.findById(id);
        if (response.isPresent()) {
            Location location = response.get();
            location.updateLocation(country, address);
            locationRepository.save(location);
        } else {
            throw new EntityNotFoundException(Location.class, id);
        }
    }

    public Location getLocation(Long id) throws EntityNotFoundException {
        Optional<Location> response = locationRepository.findById(id);
        if (response.isPresent()) {
            return response.get();
        } else {
            throw new EntityNotFoundException(Location.class, id);
        }
    }

    private void validate(String address) throws IllegalArgumentException {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la dirección.");
        }
    }
}

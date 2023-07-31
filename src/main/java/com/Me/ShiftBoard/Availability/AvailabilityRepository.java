package com.Me.ShiftBoard.Availability;

import com.Me.ShiftBoard.Availability.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface AvailabilityRepository extends MongoRepository<Availability,Integer> , QuerydslPredicateExecutor<Availability> {

    Availability findAvailabilityByAvailableId(long availableId);

    boolean existsAvailabilityByDayAndEmployeeId(String day,long employeeId);

    boolean existsAvailabilityByAvailableId(long avilableId);

    Availability deleteAvailabilityByAvailableId(long availableId);

    void deleteAllByEmployeeId(long employeeId);

    List<Availability> findAvailabilitiesByEmployeeId(long employeeId);

}

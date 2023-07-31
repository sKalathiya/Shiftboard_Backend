package com.Me.ShiftBoard.Availability;


import com.Me.ShiftBoard.Employee.Employee;
import com.Me.ShiftBoard.Employee.EmployeeService;
import com.Me.ShiftBoard.Schedule.Schedule;
import com.Me.ShiftBoard.Schedule.ScheduleService;
import com.Me.ShiftBoard.Sequence.SequenceGeneratorService;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.State;
import com.Me.ShiftBoard.Util.Status;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AvailabilityService {

    @Autowired
    private  AvailabilityRepository availabilityRepository;
    @Autowired
    private  EmployeeService employeeService;
    @Autowired
    private  SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    @Lazy
    private  ScheduleService scheduleService;

    public Response addAvailability(Availability availability) {
        System.out.println("Here in add");
        Response response = new Response();

        if(availability.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }

        if(availabilityRepository.existsAvailabilityByDayAndEmployeeId(availability.getDay(), availability.getEmployeeId()))
        {
           response.setOperationStatus(Status.Failure,"Availability for the same day already exists, pls modify it.");
           return response;
        }
        if(!employeeService.existsEmployee(availability.getEmployeeId()))
        {
            response.setOperationStatus(Status.Failure,"No such employee found!!");
            return response;
        }

        availability.setAvailableId(sequenceGeneratorService.generateSequence(Availability.SEQUENCE_NAME));
        availability.setState(State.PENDING);
        availabilityRepository.save(availability);
        response.setOperationStatus(Status.Success,availability);

        return response;
    }

    public Response updateAvailability(Availability availability) {

        Response response = new Response();
        if(availability.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if(!availabilityRepository.existsAvailabilityByAvailableId(availability.getAvailableId()))
        {
            response.setOperationStatus(Status.Failure,"no such availability found!!");
            return response;
        }

        Availability a = availabilityRepository.findAvailabilityByAvailableId(availability.getAvailableId());

        if(!Objects.equals(a.getDay(), availability.getDay()) && availabilityRepository.existsAvailabilityByDayAndEmployeeId(availability.getDay(), availability.getEmployeeId()))
        {
            response.setOperationStatus(Status.Failure,"availability for the given day already exists pls modify that!");
            return response;
        }

        a.setDay(availability.getDay());
        a.setReason(availability.getReason());
        a.setStartTime(availability.getStartTime());
        a.setEndTime(availability.getEndTime());
        a.setState(State.PENDING);
        availabilityRepository.save(a);
        response.setOperationStatus(Status.Success,a);

        return response;
    }

    public Response deleteAvailability(long availableId) {

        Response response = new Response();

        if(availabilityRepository.existsAvailabilityByAvailableId(availableId))
        {
            response.setOperationStatus(Status.Success,availabilityRepository.deleteAvailabilityByAvailableId(availableId));

        }else{
            response.setOperationStatus(Status.Failure,"no such availability exists.");
        }

        return response;
    }

    public Response getAll() {

        Response response = new Response();
        response.setOperationStatus(Status.Success,availabilityRepository.findAll());
        return response;
    }

    public Response getAForEmployee(long employeeId) {

        Response response = new Response();

        if(employeeService.existsEmployee(employeeId))
        {
            response.setOperationStatus(Status.Success,availabilityRepository.findAvailabilitiesByEmployeeId(employeeId));
        }
        else {
            response.setOperationStatus(Status.Failure,"No such employee Found!!");
        }

        return response;
    }

    public Response getAvailabilityBySearch(Map<String,String> params)
    {
        Response response = new Response();

        BooleanBuilder builder = AvailabilityPredicateBuilder.getSearchPredicates(params);

        List<Availability> availabilities = new ArrayList<>();


        availabilityRepository.findAll(builder).iterator().forEachRemaining(availabilities::add);

        if(availabilities.size() == 0)
        {
            response.setOperationStatus(Status.Failure,"No such Availabilities found!!");

        }else {
            response.setOperationStatus(Status.Success,availabilities);
        }

        return response;
    }

    public Response changeStateOfAvailability(long availableId, State state) {

        Response response = new Response();

        if(!availabilityRepository.existsAvailabilityByAvailableId(availableId))
        {
            response.setOperationStatus(Status.Failure,"No such availability found!!");
            return response;
        }

        Availability a  = availabilityRepository.findAvailabilityByAvailableId(availableId);
        a.setState(state);
        availabilityRepository.save(a);

        if(a.getState().equals(State.DECLINED)){
            response.setOperationStatus(Status.Success,a);
            return response;
        }

        Employee e = employeeService.getEmployeeByExternalId(a.getEmployeeId());
        HashMap<LocalDate,Long> sc = e.getSchedule();
        HashMap<LocalDate,Long> o = e.getSchedule();
        o.keySet().stream().filter(d -> d.getDayOfWeek().toString().equals(a.getDay()))
                        .map(d ->{
                            Schedule daySchedule = (Schedule) scheduleService.getScheduleFromId(o.get(d)).getData();

                            if (!((daySchedule.getStartTime().isBefore(a.getStartTime()) &&
                                    daySchedule.getEndTime().isBefore(a.getStartTime())) ||
                                    daySchedule.getStartTime().isAfter(a.getEndTime()))
                            ) {
                               sc.replace(d, (long) -1);
                            }
                            return "";
                        }).count();
        e.setSchedule(sc);
        employeeService.saveEmployee(e);
        response.setOperationStatus(Status.Success,a);

        return response;
    }

    public Response getAvailabilityById(long aId) {

        Response response = new Response();
        if(availabilityRepository.existsAvailabilityByAvailableId(aId))
        {
            response.setOperationStatus(Status.Success,availabilityRepository.findAvailabilityByAvailableId(aId));
        }else {
            response.setOperationStatus(Status.Failure,"No such availability found!!");
        }
        return response;
    }

    public void deleteAllByEmployeeId(long externalId)
    {
        availabilityRepository.deleteAllByEmployeeId(externalId);

    }
}

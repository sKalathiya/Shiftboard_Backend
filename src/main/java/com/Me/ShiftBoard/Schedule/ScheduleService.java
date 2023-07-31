package com.Me.ShiftBoard.Schedule;


import com.Me.ShiftBoard.Availability.Availability;
import com.Me.ShiftBoard.Availability.AvailabilityService;
import com.Me.ShiftBoard.Employee.Employee;

import com.Me.ShiftBoard.Employee.EmployeeService;
import com.Me.ShiftBoard.Sequence.SequenceGeneratorService;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.State;
import com.Me.ShiftBoard.Util.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private  final EmployeeService employeeService;
    private final ScheduleRepository scheduleRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    private final AvailabilityService availabilityService;

    public Response setDaySchedule(long employeeId, Schedule daySchedule, LocalDate date) {

        Response response = new Response();

        if(daySchedule.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if (date.isBefore(LocalDate.now())) {
            response.setOperationStatus(Status.Failure, "No longer available to change");
            return response;
        }

        if (!daySchedule.getStartTime().isBefore(daySchedule.getEndTime())) {
            response.setOperationStatus(Status.Failure, "Invalid shift time");
            return response;
        }

        if (!employeeService.existsEmployee(employeeId)) {
            response.setOperationStatus(Status.Failure, "No such Employee found!!");
            return response;
        }

        if (!scheduleRepository.existsScheduleByScheduleId(daySchedule.getScheduleId())) {
            daySchedule.setScheduleId(sequenceGeneratorService.generateSequence(Schedule.SEQUENCE_NAME));
            daySchedule.setTotalHour();
            scheduleRepository.save(daySchedule);
        }

        List<Availability> availabilities = (List<Availability>) availabilityService.getAForEmployee(employeeId).getData();

        availabilities.stream().filter(a -> a.getDay().equals(date.getDayOfWeek().toString()))
                .map(
                    a -> {

                        if ((!((daySchedule.getStartTime().isBefore(a.getStartTime()) &&
                                daySchedule.getEndTime().isBefore(a.getStartTime())) ||
                                daySchedule.getStartTime().isAfter(a.getEndTime())) &&
                                    a.getState().equals(State.APPROVED))
                        ) {

                            response.setOperationStatus(Status.Failure, "No availability");
                        }
                    return "";
                    }
                )
                .count();
        if(response.getOperationStatus() != null){return  response;}

        Employee e = employeeService.getEmployeeByExternalId(employeeId);
        HashMap<LocalDate, Long> sch = e.getSchedule();
        if (sch.containsKey(date)) {
            sch.replace(date, daySchedule.getScheduleId());
        } else {

            sch.put(date, daySchedule.getScheduleId());
        }
        e.setSchedule(sch);
        employeeService.saveEmployee(e);

        response.setOperationStatus(Status.Success, e);
        return response;
    }


    public Response getSchedule(long employeeId) {

        Response response = new Response();

        if(!employeeService.existsEmployee(employeeId))
        {
            response.setOperationStatus(Status.Failure,"No such Employee found!!");
            return response;
        }

        Employee e = employeeService.getEmployeeByExternalId(employeeId);

        HashMap<LocalDate,Schedule> schedule = new HashMap<>();

        e.getSchedule().forEach((day,scheduleId) -> schedule.put(day,scheduleRepository.findScheduleByScheduleId(scheduleId)));

        response.setOperationStatus(Status.Success,schedule);

        return response;
    }

    public Response getALlSchedules() {

        Response response = new Response();

        response.setOperationStatus(Status.Success,scheduleRepository.findAll());

        return response;
    }

    public Response deleteDaySchedule(long employeeId, LocalDate day) {

        Response response = new Response();

        if(day.isBefore(LocalDate.now()))
        {
            response.setOperationStatus(Status.Failure,"No longer available to change");
            return response;
        }

        if(!employeeService.existsEmployee(employeeId))
        {
            response.setOperationStatus(Status.Failure,"No such Employee found!!");
            return response;
        }

        Employee e = employeeService.getEmployeeByExternalId(employeeId);
        HashMap<LocalDate,Long> schedule = e.getSchedule();

        if(!schedule.containsKey(day)){
            response.setOperationStatus(Status.Failure,"No schedule found for the day!");
            return response;
        }
        schedule.replace(day, (long) -1);
        e.setSchedule(schedule);
        employeeService.saveEmployee(e);
        response.setOperationStatus(Status.Success,e);

        return response;
    }


    public Response getScheduleForDay(LocalDate date) {

        Response response = new Response();
        List<Employee> employees = employeeService.getEmployeesForOtherService();
        HashMap<Long,Schedule> finals = new HashMap<>();
        employees.stream()
                .filter(employee -> {
                        HashMap<LocalDate,Long> sc = employee.getSchedule();
                        if(!sc.containsKey(date))
                        {
                            return false;
                        }else{
                            if(sc.get(date)!= -1)
                            {
                                finals.put(employee.getId(),scheduleRepository.findScheduleByScheduleId(sc.get(date)));
                                return true;
                            }else return false;
                    }
                }).count();



        response.setOperationStatus(Status.Success,finals);

        return  response;
    }

    public Response getScheduleFromId(long scheduleId) {

        Response response = new Response();
        if(scheduleRepository.existsScheduleByScheduleId(scheduleId))
        {
            response.setOperationStatus(Status.Success,scheduleRepository.findScheduleByScheduleId(scheduleId));
        }else {
            response.setOperationStatus(Status.Failure,"No such schedule present!");
        }
        return response;
    }

    public Response getScheduleForWeekFromId(long employeeId) {
        Response response = new Response();
        if (employeeService.existsEmployee(employeeId)) {
            Employee e = employeeService.getEmployeeByExternalId(employeeId);
            HashMap<LocalDate, Long> s = e.getSchedule();
            HashMap<LocalDate, Schedule> WeeklySchedule = new HashMap<>();
            LocalDate currentDate = LocalDate.now();
            // Find the first and last day of the current week
            LocalDate firstDayOfCurrentWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastDayOfCurrentWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Calculate the first and last day of next week
            LocalDate firstDayOfNextWeek = firstDayOfCurrentWeek.plusWeeks(1);
            LocalDate lastDayOfNextWeek = lastDayOfCurrentWeek.plusWeeks(1);

            // Store all the dates of both weeks in a list
            List<LocalDate> datesOfThisAndNextWeek = new ArrayList<>();
            LocalDate currentDateOfThisWeek = firstDayOfCurrentWeek;

            while (!currentDateOfThisWeek.isAfter(lastDayOfNextWeek)) {
                datesOfThisAndNextWeek.add(currentDateOfThisWeek);
                currentDateOfThisWeek = currentDateOfThisWeek.plusDays(1);
            }

          datesOfThisAndNextWeek.forEach(date -> {

              if (s.containsKey(date)) {
                  WeeklySchedule.put(date, scheduleRepository.findScheduleByScheduleId(s.get(date)));
              } else {
                  WeeklySchedule.put(date, scheduleRepository.findScheduleByScheduleId(-1));
              }
          });



            response.setOperationStatus(Status.Success, WeeklySchedule);

        } else {
            response.setOperationStatus(Status.Failure, "No such employee Found!");
        }
        return response;
    }
}

package com.Me.ShiftBoard.Schedule;


import com.Me.ShiftBoard.Util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {


    private final ScheduleService scheduleService;

    @PostMapping("/admin/employee/{employeeId}")
    public ResponseEntity<Response> setDaySchedule(@PathVariable("employeeId") long employeeId, @RequestBody Schedule daySchedule, @RequestParam(value = "dt") String dt){
        LocalDate date1 = LocalDate.parse(dt);
        return ResponseEntity.ok(scheduleService.setDaySchedule(employeeId,daySchedule, date1));
    }


    //client and admin
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Response>  getScheduleForEmployee(@PathVariable long employeeId)
    {
        return  ResponseEntity.ok(scheduleService.getSchedule(employeeId));
    }


    @DeleteMapping("/admin/employee/{employeeId}")
    public ResponseEntity<Response>  deleteScheduleForEmployee(@PathVariable long employeeId,@RequestParam(value = "dt") String dt)
    {
        return ResponseEntity.ok(scheduleService.deleteDaySchedule(employeeId,LocalDate.parse(dt)));
    }

    @GetMapping("/admin")
    public ResponseEntity<Response>  getAllSchedule()
    {
        return ResponseEntity.ok(scheduleService.getALlSchedules());
    }

    @GetMapping("/admin/day")
    public ResponseEntity<Response>  getScheduleForDay(@RequestParam(value = "dt") String dt)
    {
        return ResponseEntity.ok(scheduleService.getScheduleForDay(LocalDate.parse(dt)));
    }


    @GetMapping("/{scheduleId}")
    public ResponseEntity<Response>  getScheduleFromId(@PathVariable long scheduleId)
    {
        return ResponseEntity.ok(scheduleService.getScheduleFromId(scheduleId));
    }

    @GetMapping("/week/{employeeId}")
    public ResponseEntity<Response> getSceduleForWeekFromEmployee(@PathVariable long employeeId){
        return ResponseEntity.ok(scheduleService.getScheduleForWeekFromId(employeeId));
    }

}

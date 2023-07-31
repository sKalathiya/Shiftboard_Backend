package com.Me.ShiftBoard.Availability;


import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.State;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    //client and admin
    @PostMapping("")
    public ResponseEntity<Response> addAvailability(@RequestBody Availability availability)
    {
        System.out.println("here in controller");
        return ResponseEntity.ok(availabilityService.addAvailability(availability));
    }


    //client and admin
    @PutMapping("")
    public ResponseEntity<Response> updateAvailability(@RequestBody Availability availability)
    {
        return  ResponseEntity.ok(availabilityService.updateAvailability(availability));
    }

    //client and admin
    @DeleteMapping("/{availableId}")
    public ResponseEntity<Response> deleteAvaialbility(@PathVariable long availableId)
    {
        return ResponseEntity.ok(availabilityService.deleteAvailability(availableId));
    }

    @GetMapping("/admin")
    public ResponseEntity<Response> getAllAvailability()
    {
        return ResponseEntity.ok(availabilityService.getAll());
    }

    //client and admin
    @GetMapping("/{employeeId}")
    public ResponseEntity<Response> getAvailabilityOfEmployee(@PathVariable long employeeId)
    {
        return ResponseEntity.ok(availabilityService.getAForEmployee(employeeId));
    }

    //some restriction
    @GetMapping("/search")
    public ResponseEntity<Response> getAvailabilityBySearch(@RequestParam Map<String, String> params)
    {
        return ResponseEntity.ok(availabilityService.getAvailabilityBySearch(params));
    }

    @PutMapping("/admin/status/{availableId}")
    public ResponseEntity<Response> changeAvailabilityStatus(@PathVariable long availableId, @RequestParam State state)
    {
        return ResponseEntity.ok(availabilityService.changeStateOfAvailability(availableId,state));
    }

    @GetMapping("/admin/id/{aId}")
    public ResponseEntity<Response> getAvailabilityByID(@PathVariable long aId)
    {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(aId));
    }
}

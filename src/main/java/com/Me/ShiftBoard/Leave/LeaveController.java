package com.Me.ShiftBoard.Leave;

import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.State;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leaves")
public class LeaveController {

    private final LeaveService leaveService;


    @GetMapping("/admin")
    public ResponseEntity<Response> getAllLeaves()
    {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }


    //client and admin
    @PostMapping("")
    public ResponseEntity<Response> addLeave(@RequestBody Leave leave)
    {
        return ResponseEntity.ok(leaveService.addLeave(leave));
    }

    //client and admin
    @GetMapping("/{employeeId}")
    public ResponseEntity<Response> getLeaveByEmployee(@PathVariable long employeeId)
    {
        return ResponseEntity.ok(leaveService.getLeaveByEmployee(employeeId));
    }

    //client and admin
    @PutMapping("")
    public ResponseEntity<Response> updateLeave(@RequestBody Leave leave)
    {
        return  ResponseEntity.ok(leaveService.updateLeave(leave));
    }


    //client and admin
    @DeleteMapping("/{leaveId}")
    public ResponseEntity<Response> deleteLeave(@PathVariable long leaveId)
    {
        return  ResponseEntity.ok(leaveService.deleteLeave(leaveId));
    }

    //some restriction
    @GetMapping("/search")
    public ResponseEntity<Response> getLeavesBySearch(@RequestParam Map<String, String> params)
    {
        return ResponseEntity.ok(leaveService.getLeavesBySearch(params));
    }

    @PutMapping("/admin/status/{leaveId}")
    public ResponseEntity<Response> changeLeaveStatus(@PathVariable long leaveId, @RequestParam State state)
    {
        return ResponseEntity.ok(leaveService.changeStateOfLeave(leaveId,state));
    }

    @GetMapping("/admin/id/{leaveId}")
    public ResponseEntity<Response> getLeaveById(@PathVariable long leaveId)
    {
        return ResponseEntity.ok(leaveService.getLeaveById(leaveId));
    }
}

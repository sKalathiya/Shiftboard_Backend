package com.Me.ShiftBoard.Department;

import com.Me.ShiftBoard.Generator;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/departments/admin")
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/{departmentId}")
    public ResponseEntity<Response> getDepartmentById(@PathVariable long departmentId)
    {
        return ResponseEntity.ok(departmentService.findDepartmentById(departmentId));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> getDepartmentBySearch(@RequestParam Map<String ,String> params)
    {
        return ResponseEntity.ok(departmentService.getDepartmentsBySearch(params));
    }

    @GetMapping("")
    public ResponseEntity<Response> getAllDepartments()
    {

        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PostMapping("")
    public ResponseEntity<Response> addDepartment(@RequestBody Department department)
    {
        return ResponseEntity.ok(departmentService.addDepartment(department));
    }

    @PutMapping("")
    public ResponseEntity<Response> updateDepartment(@RequestBody Department department)
    {

       return ResponseEntity.ok(departmentService.updateDepartment(department));
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Response> deleteDepartment(@PathVariable long departmentId)
    {
        return ResponseEntity.ok(departmentService.deleteDepartment(departmentId));
    }

    @DeleteMapping("/remove-employee")
    public ResponseEntity<Response> deleteEmployeeInDepartment(@RequestBody JsonNode requestBody )
    {
        ArrayNode employees = (ArrayNode) requestBody.get("employeesId");
        List<Long> employeesId = new ArrayList<>();
        employees.elements().forEachRemaining(id -> employeesId.add(Long.parseLong(id.asText())));
        long departmentId = requestBody.get("departmentId").asLong();
        Response response = new Response();
        boolean check = departmentService.deleteEmployeeInDepartment(employeesId,departmentId,true);
        System.out.println(check);
        if(check)
        {
            response.setOperationStatus(Status.Success);
        }
        else {
            response.setOperationStatus(Status.Failure);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/{departmentId}")
    public ResponseEntity<Response> getEmployeesByDepartmentId(@PathVariable long departmentId)
    {
        return ResponseEntity.ok(departmentService.getEmployeesByDepartmentId(departmentId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Response> getDepartmentByEmployeeId(@PathVariable long employeeId){
        return ResponseEntity.ok(departmentService.getDepartmentByEmployeeId(employeeId));
    }


}

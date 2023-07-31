package com.Me.ShiftBoard.Employee;

import com.Me.ShiftBoard.Generator;
import com.Me.ShiftBoard.Util.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {


    @Autowired
     EmployeeService employeeService;


   @PostMapping("/admin")
    public ResponseEntity<Response> addEmployee(@RequestBody Employee employee)
   {

       return ResponseEntity.ok(employeeService.addEmployee(employee));
   }


   //client and admin
   @PutMapping("/update")
   public ResponseEntity<Response>  updateEmployee(@RequestBody Employee employee)
   {
        return ResponseEntity.ok(employeeService.updateEmployee(employee));
   }

   @PutMapping("/admin/transfer")
   public ResponseEntity<Response>  transferEmployees(@RequestBody JsonNode requestBody)
   {
       ArrayNode employees = (ArrayNode) requestBody.get("employeesId");
       List<Long> employeesId = new ArrayList<>();
       employees.elements().forEachRemaining(id -> employeesId.add(Long.parseLong(id.asText())));


       return ResponseEntity.ok(employeeService.transferEmployee(employeesId,requestBody.get("newDepartment").asLong()));
   }

   @DeleteMapping("/admin/{externalId}")
    public ResponseEntity<Response>  deleteEmployee(@PathVariable("externalId") long eId)
   {
       return ResponseEntity.ok(employeeService.deleteEmployee(eId));
   }

   // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Response>  getEmployees()
    {

        return ResponseEntity.ok(employeeService.getAllEmployee());
    }

    //client and admin
    @GetMapping("/{externalId}")
    public ResponseEntity<Response>  getEmployeeByeId(@PathVariable("externalId") long eId)
    {
        return ResponseEntity.ok(employeeService.getEmployee(eId));
    }

    @GetMapping("/admin/search")
    public ResponseEntity<Response>  getEmployeeBySearch(@RequestParam Map<String, String> params)
    {
        return  ResponseEntity.ok(employeeService.getEmployeesBySearch(params));
    }

}

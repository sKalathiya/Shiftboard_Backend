package com.Me.ShiftBoard.Department;

import com.Me.ShiftBoard.Employee.Employee;
import com.Me.ShiftBoard.Employee.EmployeeService;
import com.Me.ShiftBoard.Sequence.SequenceGeneratorService;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.Status;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepartmentService {



    private final DepartmentRepository departmentRepository;

    private final EmployeeService employeeService;

    private final SequenceGeneratorService sequenceGeneratorService;


    public boolean transferEmployee(Long newDept, Employee tmp)
    {
        List<Long> eIds;
        if(newDept == tmp.getDepartmentId()) return true;
        if(!departmentRepository.existsDepartmentByDepartmentId(newDept))
        {
            return false;
        }

        Department newDepartment = departmentRepository.findDepartmentByDepartmentId(newDept);

        Department oldDepartment = departmentRepository.findDepartmentByDepartmentId(tmp.getDepartmentId());
        oldDepartment.setNoOfEmployees(oldDepartment.getNoOfEmployees()-1);
        eIds = oldDepartment.getEmployeesId();
        eIds.remove(tmp.getId());
        oldDepartment.setEmployeesId(eIds);
        departmentRepository.save(oldDepartment);

        newDepartment.setNoOfEmployees(newDepartment.getNoOfEmployees() + 1);
        eIds = newDepartment.getEmployeesId();
        eIds.add(tmp.getId());
        newDepartment.setEmployeesId(eIds);

       departmentRepository.save(newDepartment);

       tmp.setDepartmentId(newDept);
       employeeService.saveEmployee(tmp);

       return true;
    }


    public boolean addEmployeeInDepartment(long externalId,long departmentId) {

        if(departmentRepository.existsDepartmentByDepartmentId(departmentId))
        {
            Department tmp = departmentRepository.findDepartmentByDepartmentId(departmentId);
            List<Long> eIds = tmp.getEmployeesId();
            eIds.add(externalId);
            tmp.setEmployeesId(eIds);
            tmp.setNoOfEmployees(tmp.getNoOfEmployees() + 1);
            departmentRepository.save(tmp);
            return true;
        }
        return  false;
    }

    public boolean deleteEmployeeInDepartment(List<Long> externalIds, long departmentId , boolean inActive)
    {
        final boolean[] check = {false};

            if(departmentRepository.existsDepartmentByDepartmentId(departmentId)) {
                if(inActive) {
                    externalIds.forEach( externalId -> {
                    if (!employeeService.existsEmployee(externalId)) {

                    }else {
                        Employee employee = employeeService.getEmployeeByExternalId(externalId);
                        if (transferEmployee((long) -1, employee)) check[0] = true;
                    }
                    });
                }
                else{
                    Department d = departmentRepository.findDepartmentByDepartmentId(departmentId);
                    List<Long> tmp = d.getEmployeesId();
                    tmp.removeAll(externalIds);
                    d.setEmployeesId(tmp);
                    d.setNoOfEmployees(d.getNoOfEmployees() - externalIds.size());
                    departmentRepository.save(d);
                    check[0] = true;
                }
            }


        return check[0];
    }




    public Response findDepartmentById(long departmentId)
    {   Response response = new Response();

        if(departmentRepository.existsDepartmentByDepartmentId(departmentId))
        {   response.setOperationStatus(Status.Success,departmentRepository.findDepartmentByDepartmentId(departmentId));
        }else{
            response.setOperationStatus(Status.Failure,"No such Department found!");
        }

        return response;
    }

    public Response getAllDepartments() {

        Response response = new Response();

        response.setOperationStatus(Status.Success,departmentRepository.findAll());
        return  response;
    }

    public Response addDepartment(Department department) {
        Response response = new Response();
        if(department.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        List<Long> employeeIds = department.getEmployeesId();
        department.setEmployeesId(new ArrayList<>());
        department.setNoOfEmployees(0);
        if(departmentRepository.existsDepartmentByName(department.getName()))
        {

            response.setOperationStatus(Status.Failure,"Department with the given name already exists!!");
        }
        else {

            department.setDepartmentId(sequenceGeneratorService.generateSequence(Department.SEQUENCE_NAME));
            long dId  = department.getDepartmentId();
            departmentRepository.save(department);

            for (long eId: employeeIds) {
                if(employeeService.existsEmployee(eId))
                {
                    Employee e = employeeService.getEmployeeByExternalId(eId);
                    transferEmployee(dId,e);
                }
                else{
                    response.setWarning("Some employee does not exists");
                }
            }

            response.setOperationStatus(Status.Success,departmentRepository.findDepartmentByDepartmentId(dId));
        }
        return response;

    }

    public Response updateDepartment(Department department) {

        Response response = new Response();
        if(department.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if(departmentRepository.existsDepartmentByDepartmentId(department.getDepartmentId()))
        {
            if(departmentRepository.existsDepartmentByName(department.getName())){
                response.setOperationStatus(Status.Failure,"Such department name already exists");
                return response;
            }
            Department tmp = departmentRepository.findDepartmentByDepartmentId(department.getDepartmentId());

            tmp.setEmail(department.getEmail());
            tmp.setName(department.getName());
            departmentRepository.save(tmp);
            response.setOperationStatus(Status.Success,tmp);
        }else {
            response.setOperationStatus(Status.Failure,"No such department exists!");
        }

        return response;
    }

    public Response deleteDepartment(long departmentId) {
        Response response = new Response();

        if(departmentRepository.existsDepartmentByDepartmentId(departmentId))
        {
            Department tmp = departmentRepository.findDepartmentByDepartmentId(departmentId);

            tmp.getEmployeesId().forEach(eId -> {
                Employee e = employeeService.getEmployeeByExternalId(eId);
               transferEmployee((long) -1,e);
                employeeService.saveEmployee(e);
            });
            departmentRepository.deleteDepartmentByDepartmentId(departmentId);
            response.setOperationStatus(Status.Success,tmp);
        }
        else{
            response.setOperationStatus(Status.Failure,"No such department exists!");
        }
    return response;
    }

    public Response getDepartmentsBySearch(Map<String, String> params) {

        Response response = new Response();

        BooleanBuilder builder = DepartmentPredicateBuilder.getSearchPredicates(params);

        List<Department> departments = new ArrayList<>();


       departmentRepository.findAll(builder).iterator().forEachRemaining(departments::add);

        if(departments.size() == 0)
        {
            response.setOperationStatus(Status.Failure,"No such Department found!!");

        }else {
            response.setOperationStatus(Status.Success,departments);
        }

        return response;


    }

    public Response getEmployeesByDepartmentId(long departmentId) {

        Response response = new Response();

        if(departmentRepository.existsDepartmentByDepartmentId(departmentId))
        {
            Department department = departmentRepository.findDepartmentByDepartmentId(departmentId);

            response.setOperationStatus(Status.Success,department.getEmployeesId().stream().map(employeeService::getEmployeeByExternalId).toList());
        }else{
            response.setOperationStatus(Status.Failure,"No such Department found!");
        }

        return response;


    }

    public Response getDepartmentByEmployeeId(long employeeId) {
        Response response = new Response();
        if(employeeService.existsEmployee(employeeId))
        {
            Employee e = employeeService.getEmployeeByExternalId(employeeId);
            Department d = departmentRepository.findDepartmentByDepartmentId(e.getDepartmentId());

            response.setOperationStatus(Status.Success , d);
        }else{
            response.setOperationStatus(Status.Failure,"No such Employee found!");
        }
        return response;
    }
}

package com.Me.ShiftBoard.Employee;

import com.Me.ShiftBoard.Availability.AvailabilityService;
import com.Me.ShiftBoard.Department.DepartmentService;
import com.Me.ShiftBoard.Leave.LeaveService;
import com.Me.ShiftBoard.Sequence.SequenceGeneratorService;
import com.Me.ShiftBoard.User.User;
import com.Me.ShiftBoard.User.UserRepository;
import com.Me.ShiftBoard.Util.HistoryGenerator;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.Role;
import com.Me.ShiftBoard.Util.Status;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRespository employeeRespository;
    @Lazy
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public Response addEmployee(Employee employee){

        Response response = new Response();
        if(employee.checkAnyNull()) {

            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if(userRepository.findUserByEmail(employee.getEmail()).isPresent())
        {
            response.setOperationStatus(Status.Failure,"Email already exists!");
            return response;
        }
        if(employeeRespository.existsEmployeeByIdOrEmail(employee.getId(),employee.getEmail()))
        {
            response.setOperationStatus(Status.Failure,"Employee already exists");

        }else{
            Calendar cal = Calendar.getInstance();
            HashMap<LocalDate, Long> schedule = employee.getSchedule();
            for(int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                cal.set(Calendar.DAY_OF_WEEK, i);
                schedule.put(LocalDate.from(LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault())), (long) -1);
            }

            employee.setSchedule(schedule);

            employeeRespository.save(employee);
            userRepository.save(User.builder()
                    .userId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                    .email(employee.getEmail())
                    .password(new BCryptPasswordEncoder().encode("password"))
                    .role(Role.EMPLOYEE)
                    .build());

            if(departmentService.addEmployeeInDepartment(employee.getId(),employee.getDepartmentId()))
            {
                response.setOperationStatus(Status.Success,employee);

            } else{
                employeeRespository.deleteEmployeeById(employee.getId());
                userRepository.deleteUserByEmail(employee.getEmail());
                response.setOperationStatus(Status.Failure,"Cannot add employee to the mentioned department!");
            }

        }


        return response;
    }

    public Response getAllEmployee()
    {
        Response response = new Response();
        response.setOperationStatus(Status.Success,employeeRespository.findAll());
        return response;
    }


    public Response getEmployee(long eId)
    {
        Response response = new Response();

        if(employeeRespository.existsById(eId))
        {
            response.setOperationStatus(Status.Success,employeeRespository.findEmployeeById(eId));
        }
        else{
            response.setOperationStatus(Status.Failure,"No such employee found!");
        }

        return response;

    }


    public Response getEmployeesBySearch(Map<String,String> params)
    {
        Response response = new Response();

        BooleanBuilder builder = EmployeePredicateBuilder.getSearchPredicates(params);

        List<Employee> employees = new ArrayList<>();


        employeeRespository.findAll(builder).iterator().forEachRemaining(employees::add);

        if(employees.size() == 0)
        {
            response.setOperationStatus(Status.Failure,"No such Employee found!!");

        }else {
            response.setOperationStatus(Status.Success,employees);
        }

        return response;
    }


    public Response deleteEmployee(long eId)
    {
        Response response = new Response();

        if(!employeeRespository.existsById(eId))
        {
            response.setOperationStatus(Status.Failure,"Employee with the external Id" + eId + " does not exists!");
            return response;
        }

        Employee e  = employeeRespository.deleteEmployeeById(eId);
        if(departmentService.deleteEmployeeInDepartment(List.of(eId),e.getDepartmentId() , false)){

            userRepository.deleteUserByEmail(e.getEmail());
            leaveService.deleteAllLeaveByEmployeeId(eId);
            availabilityService.deleteAllByEmployeeId(eId);
            response.setOperationStatus(Status.Success,e);
        }
        else{
            employeeRespository.save(e);
            response.setOperationStatus(Status.Failure,"Cannot remove Employee from the mentioned department!");
        }

        return response;
    }

    public Response updateEmployee(Employee employee)
    {
        Response response = new Response();
        if(employee.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if(!employeeRespository.existsById(employee.getId()))
        {
            response.setOperationStatus(Status.Failure,"No such employee found!!");
            return response;
        }

        Employee e = employeeRespository.findEmployeeById(employee.getId());

        e.setFirstName(employee.getFirstName());
        e.setAddress(employee.getAddress());
        e.setContactNumber(employee.getContactNumber());
        e.setLastName(employee.getLastName());

        response.setOperationStatus(Status.Success, employeeRespository.save(e));

        return response;
    }

    public Response transferEmployee(List<Long> employeesId, Long newDepartment) {

        Response response = new Response();
        response.setOperationStatus(Status.Failure);
        employeesId.forEach(employeeId -> {

        if (employeeRespository.existsById(employeeId)) {
            Employee e = employeeRespository.findEmployeeById(employeeId);

            if (!departmentService.transferEmployee(newDepartment, e)) {

                response.setWarning(response.getWarning() +" " + employeeId);
            } else {

                employeeRespository.save(e);
                response.setOperationStatus(Status.Success, "S");
            }
        } else {

            response.setWarning(response.getWarning() +" " + employeeId);
        }
            });


        return response;
    }



    public void updateScheduleWeekly()
    {
        List<Employee> employees = employeeRespository.findAll();

        System.out.println("running");
        employees.forEach(employee -> {

                HashMap<LocalDate,Long> s = employee.getSchedule();
                HashMap<LocalDate,Long> r = new HashMap<>();
            final int[] i = {0};
                s.forEach(((date, aLong) -> {
                    r.put(LocalDate.now().plusDays(i[0]),aLong);
                    i[0]++;
                }));

                employee.setSchedule(r);
                employeeRespository.save(employee);
        });

  }




    //

    public Employee getEmployeeByExternalId(long eId)
    {
        return employeeRespository.findEmployeeById(eId);
    }

    public boolean existsEmployee(long eId)
    {
        return employeeRespository.existsById(eId);
    }

    public void saveEmployee(Employee e)
    {
        employeeRespository.save(e);
    }

    public List<Employee> getEmployeesForOtherService(){return employeeRespository.findAll();}

    public Employee getEmployeeByEmail(String email){return employeeRespository.findEmployeeByEmail(email);}

    public boolean existsEmployeeByEmail(String email) {return  employeeRespository.existsEmployeeByEmail(email);}
}

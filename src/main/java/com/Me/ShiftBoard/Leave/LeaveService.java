package com.Me.ShiftBoard.Leave;



import com.Me.ShiftBoard.Employee.Employee;
import com.Me.ShiftBoard.Employee.EmployeeService;
import com.Me.ShiftBoard.Sequence.SequenceGeneratorService;
import com.Me.ShiftBoard.Util.Response;
import com.Me.ShiftBoard.Util.State;
import com.Me.ShiftBoard.Util.Status;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository leaveRepository;

    private  final EmployeeService employeeService;
    private final SequenceGeneratorService sequenceGeneratorService;
    public Response getAllLeaves()
    {
        Response response = new Response();
        response.setOperationStatus(Status.Success,leaveRepository.findAll());
        return response;
    }

    public Response addLeave(Leave leave) {

        Response response = new Response();
        if(leave.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if(leave.getDate().isBefore(LocalDate.now()))
        {
            response.setOperationStatus(Status.Failure,"The date is no longer available");
            return response;
        }

        if(leaveRepository.existsLeaveByDateAndEmployeeId(leave.getDate(), leave.getEmployeeId())){
            response.setOperationStatus(Status.Failure,"Such leave already present!");
            return response;
        }

        if(!employeeService.existsEmployee(leave.getEmployeeId()))
        {
            response.setOperationStatus(Status.Failure,"No such employee found!!");
            return response;
        }

        leave.setLeaveId(sequenceGeneratorService.generateSequence(Leave.SEQUENCE_NAME));
        leave.setState(State.PENDING);
        leaveRepository.save(leave);
        response.setOperationStatus(Status.Success,leave);

        return response;
    }

    public Response getLeaveByEmployee(long employeeId) {

        Response response = new Response();
        if(employeeService.existsEmployee(employeeId))
        {
            response.setOperationStatus(Status.Success,leaveRepository.findAllByEmployeeId(employeeId));
        }else {
            response.setOperationStatus(Status.Failure,"No such employee found!");
        }
        return response;
    }

    public Response updateLeave(Leave leave) {

        Response response = new Response();
        if(leave.checkAnyNull()) {
            response.setOperationStatus(Status.Failure,"Missing Information!!");
            return response;
        }
        if(!leaveRepository.existsLeaveByLeaveId(leave.getLeaveId())) {

            response.setOperationStatus(Status.Failure,"No such leave found!!");
            return response;
        }
        Response r = checkSate(leave.getLeaveId());
        if(r.getOperationStatus().equals(Status.Failure))
        {
            return r;
        }
        if(leave.getDate().isBefore(LocalDate.now()))
        {
            response.setOperationStatus(Status.Failure,"The date is no longer available");
            return response;
        }


        if(!employeeService.existsEmployee(leave.getEmployeeId()))
        {
            response.setOperationStatus(Status.Failure,"No Such employee found!!");
            return response;
        }
        Leave l = leaveRepository.findLeaveByLeaveId(leave.getLeaveId());

        if(!leave.getDate().equals(l.getDate())) {
            if (leaveRepository.existsLeaveByDateAndEmployeeId(leave.getDate(), leave.getEmployeeId())) {
                response.setOperationStatus(Status.Failure, "Such leave already present!");
                return response;
            }
        }
        l.setCategory(leave.getCategory());
        l.setDate(leave.getDate());
        l.setReason(leave.getReason());
        leaveRepository.save(l);
        response.setOperationStatus(Status.Success,l);

        return response;
    }

    public Response deleteLeave(long leaveId) {
        Response response = new Response();
        if(!leaveRepository.existsLeaveByLeaveId(leaveId))
        {
            response.setOperationStatus(Status.Failure,"No such leave exists!");
            return response;
        }
        Response r = checkSate(leaveId);
        if(r.getOperationStatus().equals(Status.Failure))
        {
            return r;
        }
        Leave l = leaveRepository.deleteLeaveByLeaveId(leaveId);
        response.setOperationStatus(Status.Success,l);

        return response;
    }


    public Response checkSate(long leaveId)
    {
        Response response = new Response();
        Leave l = leaveRepository.findLeaveByLeaveId(leaveId);
        if(!l.getState().equals(State.PENDING))
        {
            response.setOperationStatus(Status.Failure,"This leave cannot be modified as decision already has been made!");
            return  response;
        }
        response.setOperationStatus(Status.Success,"");
        return response;
    }

    public Response getLeavesBySearch(Map<String,String> params)
    {
        Response response = new Response();

        BooleanBuilder builder = LeavePredictorBuilder.getSearchPredicates(params);

        List<Leave> leaves = new ArrayList<>();


        leaveRepository.findAll(builder).iterator().forEachRemaining(leaves::add);

        if(leaves.size() == 0)
        {
            response.setOperationStatus(Status.Failure,"No such Leaves found!!");

        }else {
            response.setOperationStatus(Status.Success,leaves);
        }

        return response;
    }


    public Response changeStateOfLeave(long leaveId, State state) {

        Response response = new Response();

        if(leaveRepository.existsLeaveByLeaveId(leaveId))
        {
            Leave a  =leaveRepository.findLeaveByLeaveId(leaveId);
            a.setState(state);
            leaveRepository.save(a);

            if(a.getState().equals(State.APPROVED))
            {
                Employee e = employeeService.getEmployeeByExternalId(a.getEmployeeId());
                HashMap<LocalDate,Long> sc = e.getSchedule();
                HashMap<LocalDate,Long> o = sc;

                o.keySet().stream().filter(date -> date.isEqual(a.getDate())).map(date -> {
                    sc.replace(date, (long) -1);
                    return "";
                }).count();

                e.setSchedule(sc);
                employeeService.saveEmployee(e);
            }


            response.setOperationStatus(Status.Success,a);
        }
        else {
            response.setOperationStatus(Status.Failure,"No such leave found!!");
        }
        return response;
    }

    public Response getLeaveById(long leaveId) {
        Response response = new Response();
        if(leaveRepository.existsLeaveByLeaveId(leaveId))
        {
            response.setOperationStatus(Status.Success,leaveRepository.findLeaveByLeaveId(leaveId));

        }
        else {
            response.setOperationStatus(Status.Failure,"No such leave Found!");
        }
        return response;
    }

    public void deleteAllLeaveByEmployeeId(long externalId)
    {
        leaveRepository.deleteAllByEmployeeId(externalId);
    }

}

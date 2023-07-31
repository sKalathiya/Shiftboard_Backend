package com.Me.ShiftBoard.Leave;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends MongoRepository<Leave,Integer>, QuerydslPredicateExecutor<Leave> {


    boolean existsLeaveByLeaveId(long leaveId);
    Leave findLeaveByLeaveId(long leaveId);
    Leave deleteLeaveByLeaveId(long leaveId);

    List<Leave> findAllByEmployeeId(long employeeId);

    boolean existsLeaveByDateAndEmployeeId(LocalDate date, long employeeId);


    void deleteAllByEmployeeId(long employeeId);
}

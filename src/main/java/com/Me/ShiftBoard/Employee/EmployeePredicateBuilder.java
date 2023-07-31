package com.Me.ShiftBoard.Employee;

import com.Me.ShiftBoard.Util.Criteria;
import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class EmployeePredicateBuilder {




    public static BooleanBuilder getSearchPredicates(Map<String,String> params)
    {
        BooleanBuilder builder = new BooleanBuilder();

        QEmployee qEmployee = new QEmployee("employee");

        List<Criteria> criterias = Criteria.getEqualCriteria(params);

        criterias.forEach( criteria -> {

            if(criteria.getKey().equals("firstName"))
            {
                    builder.and(qEmployee.firstName.contains(criteria.getValue()));
            }
            if(criteria.getKey().equals("lastName"))
            {
                    builder.and(qEmployee.lastName.contains(criteria.getValue()));
            }
            if(criteria.getKey().equals("email"))
            {
                    builder.and(qEmployee.email.eq(criteria.getValue()));
            }
            if(criteria.getKey().equals("contactNumber"))
            {
                    builder.and(qEmployee.contactNumber.eq(criteria.getValue()));
            }
            if(criteria.getKey().equals("externalId"))
            {
                    builder.and(qEmployee.externalId.eq(Long.valueOf(criteria.getValue())));

            }
            if(criteria.getKey().equals("city"))
            {
                    builder.and(qEmployee.address.city.contains(criteria.getValue()));
            }
            if(criteria.getKey().equals("country"))
            {
                    builder.and(qEmployee.address.country.contains(criteria.getValue()));
            }
            if(criteria.getKey().equals("zipCode"))
            {
                    builder.and(qEmployee.address.zipCode.eq(criteria.getValue()));
            }
            if(criteria.getKey().equals("street"))
            {
                    builder.and(qEmployee.address.street.contains(criteria.getValue()));
            }

            if(criteria.getKey().equals("departmentId"))
            {
                    builder.and(qEmployee.departmentId.eq(Long.valueOf(criteria.getValue())));
            }

        });
        return builder;
    }



}


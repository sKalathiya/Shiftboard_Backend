package com.Me.ShiftBoard.Availability;

import com.Me.ShiftBoard.Util.Criteria;
import com.Me.ShiftBoard.Util.State;
import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AvailabilityPredicateBuilder {
    public static BooleanBuilder getSearchPredicates(Map<String,String> params)
    {
        BooleanBuilder builder = new BooleanBuilder();

        QAvailability qAvailability = new QAvailability("avialability");

        List<Criteria> criterias = Criteria.getEqualCriteria(params);

        criterias.forEach( criteria -> {

            if(criteria.getKey().equals("employeeId"))
            {
                builder.and(qAvailability.employeeId.eq(Long.valueOf(criteria.getValue())));
            }
            if(criteria.getKey().equals("day"))
            {
                builder.and(qAvailability.day.eq(criteria.getValue()));
            }
            if(criteria.getKey().equals("state"))
            {
                String value = criteria.getValue();
                if(value.equals("PENDING"))
                {
                    builder.and(qAvailability.state.eq(State.PENDING));

                }
                if(value.equals("APPROVED"))
                {
                    builder.and(qAvailability.state.eq(State.APPROVED));
                }
                if(value.equals("DECLINED"))
                {
                    builder.and(qAvailability.state.eq(State.DECLINED));
                }

            }
        });
        return builder;
    }
}

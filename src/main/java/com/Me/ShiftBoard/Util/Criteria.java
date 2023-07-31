package com.Me.ShiftBoard.Util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Criteria {

    private String key;
    private String value;
    private Operator operation;

    public enum Operator{
        GreaterThan,LessThan,Equal
    }
    public static List<Criteria> getEqualCriteria(Map<String ,String> params)
    {
        List<Criteria> criterias = new ArrayList<>();

        params.forEach(((key,value) -> {
            Criteria c = new Criteria();
            c.setKey(key);
            c.setValue(value);
            c.setOperation(Criteria.Operator.Equal);
            criterias.add(c);
        }));

        return criterias;
    }
}

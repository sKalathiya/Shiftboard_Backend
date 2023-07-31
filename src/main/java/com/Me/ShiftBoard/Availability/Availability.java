package com.Me.ShiftBoard.Availability;


import com.Me.ShiftBoard.Util.State;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@QueryEntity
@Document(collection = "Availabilities")
public class Availability {

    @Transient
    public static final String SEQUENCE_NAME = "availability_sequence";


    @Id
    private long availableId;
    private long employeeId;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private State state;

    public boolean checkAnyNull()
    {
        if(
                this.getStartTime() != null &&
                        this.getDay() != null &&
                        this.getEndTime() != null &&
                        this.getReason() != null
        ){
            return false;
        }else return true;
    }
}

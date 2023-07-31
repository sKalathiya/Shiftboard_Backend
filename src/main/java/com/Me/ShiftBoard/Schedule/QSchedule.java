package com.Me.ShiftBoard.Schedule;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedule is a Querydsl query type for Schedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedule extends EntityPathBase<Schedule> {

    private static final long serialVersionUID = 231242138L;

    public static final QSchedule schedule = new QSchedule("schedule");

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> lunchEndTime = createTime("lunchEndTime", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> lunchStartTime = createTime("lunchStartTime", java.time.LocalTime.class);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final NumberPath<Long> totalHour = createNumber("totalHour", Long.class);

    public QSchedule(String variable) {
        super(Schedule.class, forVariable(variable));
    }

    public QSchedule(Path<? extends Schedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedule(PathMetadata metadata) {
        super(Schedule.class, metadata);
    }

}


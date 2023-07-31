package com.Me.ShiftBoard.Availability;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAvailability is a Querydsl query type for Availability
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAvailability extends EntityPathBase<Availability> {

    private static final long serialVersionUID = -850345250L;

    public static final QAvailability availability = new QAvailability("availability");

    public final NumberPath<Long> availableId = createNumber("availableId", Long.class);

    public final StringPath day = createString("day");

    public final NumberPath<Long> employeeId = createNumber("employeeId", Long.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final StringPath reason = createString("reason");

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final EnumPath<com.Me.ShiftBoard.Util.State> state = createEnum("state", com.Me.ShiftBoard.Util.State.class);

    public QAvailability(String variable) {
        super(Availability.class, forVariable(variable));
    }

    public QAvailability(Path<? extends Availability> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAvailability(PathMetadata metadata) {
        super(Availability.class, metadata);
    }

}


package com.Me.ShiftBoard.Leave;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLeave is a Querydsl query type for Leave
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLeave extends EntityPathBase<Leave> {

    private static final long serialVersionUID = 1216446100L;

    public static final QLeave leave = new QLeave("leave");

    public final EnumPath<Leave.LeaveCategory> category = createEnum("category", Leave.LeaveCategory.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> employeeId = createNumber("employeeId", Long.class);

    public final NumberPath<Long> leaveId = createNumber("leaveId", Long.class);

    public final StringPath reason = createString("reason");

    public final EnumPath<com.Me.ShiftBoard.Util.State> state = createEnum("state", com.Me.ShiftBoard.Util.State.class);

    public QLeave(String variable) {
        super(Leave.class, forVariable(variable));
    }

    public QLeave(Path<? extends Leave> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLeave(PathMetadata metadata) {
        super(Leave.class, metadata);
    }

}


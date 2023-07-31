package com.Me.ShiftBoard.Sequence;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection = "DatabaseSequence")
@Data
public class DatabaseSequence {

    @Id
    private String id;

    private long seq;
}

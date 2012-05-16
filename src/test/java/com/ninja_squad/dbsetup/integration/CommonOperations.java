package com.ninja_squad.dbsetup.integration;

import static com.ninja_squad.dbsetup.Operations.*;

import com.ninja_squad.dbsetup.operation.Operation;
public class CommonOperations {
    public static final Operation DROP_TABLES =
        sequenceOf(sql("drop table if exists A cascade"),
                   sql("drop table if exists B cascade"));

    public static final Operation CREATE_TABLES =
        sequenceOf(sql("create table A (a_id bigint primary key, va varchar(100), nu numeric(10, 2), bo boolean, da date, tis timestamp, tim time)"),
                   sql("create table B (b_id bigint primary key, a_id SMALLINT, va varchar(100), foreign key (a_id) references A (a_id))"));

    public static final Operation INSERT_ROWS =
       sequenceOf(
           insertInto("A").columns("a_id").values(1L).build(),
           insertInto("B").columns("b_id", "a_id").values(1L, 1L).build());
}

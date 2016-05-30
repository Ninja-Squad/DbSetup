/*
 * The MIT License
 *
 * Copyright (c) 2012, Ninja Squad
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ninja_squad.dbsetup.integration;

import com.ninja_squad.dbsetup.operation.Operation;

import static com.ninja_squad.dbsetup.Operations.*;

/**
 * @author JB Nizet
 */
public class CommonOperations {
    public static final Operation DROP_TABLES =
        sequenceOf(sql("drop table if exists A cascade"),
                   sql("drop table if exists B cascade"));

    public static final Operation CREATE_TABLES =
        sequenceOf(sql("create table A (a_id bigint primary key, va varchar(100), nu numeric(10, 2), bo boolean, da date, tis timestamp, tim time, seq numeric)"),
                   sql("create table B (b_id bigint primary key, a_id SMALLINT, va varchar(100), foreign key (a_id) references A (a_id))"));

    public static final Operation INSERT_ROWS =
       sequenceOf(
           insertInto("A").columns("a_id").values(1L).build(),
           insertInto("B").columns("b_id", "a_id").values(1L, 1L).build());
}

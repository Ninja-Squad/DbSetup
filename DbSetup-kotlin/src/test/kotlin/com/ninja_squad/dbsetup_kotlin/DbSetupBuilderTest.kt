package com.ninja_squad.dbsetup_kotlin;

import com.ninja_squad.dbsetup.bind.BinderConfiguration
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration
import com.ninja_squad.dbsetup.destination.Destination
import com.ninja_squad.dbsetup.operation.Operation
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

class DbSetupBuilderTest {

    private lateinit var mockDestination: Destination
    private lateinit var mockConnection: Connection
    private lateinit var mockOperation: Operation
    private lateinit var mockConfig: BinderConfiguration
    private lateinit var mockStatement: Statement

    @Before
    fun prepare() {
        mockDestination = mock()
        mockConnection = mock()
        whenever(mockDestination.connection).thenReturn(mockConnection)
        mockOperation = mock()
        mockConfig = mock()
        mockStatement = mock<Statement>()
        whenever(mockConnection.createStatement()).thenReturn(mockStatement)
    }

    @Test
    fun `should execute an operation`() {
        dbSetup {
            destination = mockDestination
            binderConfiguration = mockConfig
            execute(mockOperation)
        }.launch()

        verify(mockOperation).execute(mockConnection, mockConfig)
    }

    @Test
    fun `should use the default binder configuration if not set`() {
        dbSetup {
            destination = mockDestination
            execute(mockOperation)
        }.launch()

        verify(mockOperation).execute(mockConnection, DefaultBinderConfiguration.INSTANCE)
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw if destination not set`() {
        dbSetup {
            execute(mockOperation)
        }.launch()
    }

    @Test
    fun `should delete all from one table`() {
        dbSetup {
            destination = mockDestination
            deleteAllFrom("user")
        }.launch()

        verify(mockStatement).executeUpdate("delete from user")
    }

    @Test
    fun `should delete all from multiple tables passed as vararg`() {
        dbSetup {
            destination = mockDestination
            deleteAllFrom("country", "user")
        }.launch()

        verify(mockStatement).executeUpdate("delete from country")
        verify(mockStatement).executeUpdate("delete from user")
    }

    @Test
    fun `should delete all from multiple tables passed as list`() {
        dbSetup {
            destination = mockDestination
            deleteAllFrom(listOf("country", "user"))
        }.launch()

        verify(mockStatement).executeUpdate("delete from country")
        verify(mockStatement).executeUpdate("delete from user")
    }

    @Test
    fun `should truncate one table`() {
        dbSetup {
            destination = mockDestination
            truncate("user")
        }.launch()

        verify(mockStatement).executeUpdate("truncate table user")
    }

    @Test
    fun `should truncate multiple tables passed as vararg`() {
        dbSetup {
            destination = mockDestination
            truncate("country", "user")
        }.launch()

        verify(mockStatement).executeUpdate("truncate table country")
        verify(mockStatement).executeUpdate("truncate table user")
    }

    @Test
    fun `should truncate multiple tables passed as list`() {
        dbSetup {
            destination = mockDestination
            truncate(listOf("country", "user"))
        }.launch()

        verify(mockStatement).executeUpdate("truncate table country")
        verify(mockStatement).executeUpdate("truncate table user")
    }

    @Test
    fun `should execute one sql statement`() {
        val query = "update foo where bar = 1"
        dbSetup {
            destination = mockDestination
            sql(query)
        }.launch()

        verify(mockStatement).executeUpdate(query)
    }

    @Test
    fun `should execute multiple sql statements passed as vararg`() {
        val query1 = "update foo where bar = 1"
        val query2 = "update baz where qux = 1"
        dbSetup {
            destination = mockDestination
            sql(query1, query2)
        }.launch()

        verify(mockStatement).executeUpdate(query1)
        verify(mockStatement).executeUpdate(query2)
    }

    @Test
    fun `should execute multiple sql statements passed as list`() {
        val query1 = "update foo where bar = 1"
        val query2 = "update baz where qux = 1"
        dbSetup {
            destination = mockDestination
            sql(listOf(query1, query2))
        }.launch()

        verify(mockStatement).executeUpdate(query1)
        verify(mockStatement).executeUpdate(query2)
    }

    @Test
    fun `should insert`() {
        val mockPreparedStatement = mock<PreparedStatement>()
        whenever(mockConnection.prepareStatement("insert into user (id, name) values (?, ?)"))
                .thenReturn(mockPreparedStatement)

        dbSetup {
            destination = mockDestination

            insertInto("user") {
                columns("id", "name")
                values(1, "John")
            }
        }.launch()

        verify(mockPreparedStatement).setObject(1, 1)
        verify(mockPreparedStatement).setObject(2, "John")
        verify(mockPreparedStatement).executeUpdate()
    }
}

package com.ninja_squad.dbsetup_kotlin

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

/**
 * @author JB Nizet
 */
fun <T> whenever(methodCall: T): OngoingStubbing<T> = Mockito.`when`(methodCall)

inline fun <reified T: Any> mock(): T = Mockito.mock(T::class.java)

package com.example.sqlcipherroom


import androidx.room.*

@Dao
interface PersonDao {
    @Query("SELECT * FROM person")
    fun getAllPersons(): List<Person>

    @Query("SELECT * FROM person WHERE id = :id")
    fun getById(id: Long): Person

    @Query("SELECT * FROM person WHERE first_name LIKE :find")
    fun findByFirstName(find: String): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersons(vararg persons: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonList(persons: List<Person>)

    @Update
    fun updatePersons(vararg persons: Person)

    @Delete
    fun deletePersons(vararg persons: Person)

    @Query("DELETE FROM person")
    fun deleteAll()
}
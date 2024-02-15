package com.example.sqlcipherroom

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlcipherroom.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            createAndAddAUser()
        }

    }

    private suspend fun createAndAddAUser() {
        // secure the database with a password
        val makeSecureWithPassword = true
        // encrypt all the data as well
        val makeMemorySecure = true


        val dbInstance = AppDatabase.getInstance(this, makeSecureWithPassword, makeMemorySecure)
        val personDao = dbInstance.personDao()

        val firstName = "test"
        val secondName = "test last name"
        val id: Long = 121212
        val user = Person(id, firstName, secondName)

        personDao.insertPersons(user)
    }
}
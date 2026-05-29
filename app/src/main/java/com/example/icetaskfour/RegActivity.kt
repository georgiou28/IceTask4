package com.example.icetaskfour

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest

class RegActivity: AppCompatActivity() {
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val name = findViewById<EditText>(R.id.nametxt)
        val surname = findViewById<EditText>(R.id.surnametxt)
        val regusername = findViewById<EditText>(R.id.usernametxt)
        val regpassword = findViewById<EditText>(R.id.passwordtxt)
        val regbtn = findViewById<Button>(R.id.registerbtn)
        val regclear = findViewById<Button>(R.id.clearbtn)
        val regclose = findViewById<Button>(R.id.closebtn)
        val tologinbtn = findViewById<Button>(R.id.backbtn)

        database = FirebaseDatabase
            .getInstance()
            .getReference("users")

        fun hashPassword(regpassword: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(regpassword.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        regbtn.setOnClickListener {
            val regname = name.text.toString()
            val regsurname = surname.text.toString()
            val username = regusername.text.toString()
            val password = regpassword.text.toString()

            if (regname.isEmpty()){
                Toast.makeText(this@RegActivity, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else if (!regname.matches(Regex("^[a-zA-Z]+$"))) {
                Toast.makeText(this@RegActivity,"Name can only contain letters",Toast.LENGTH_SHORT).show()
            }
            else if (regsurname.isEmpty()){
                Toast.makeText(this@RegActivity, "Surname cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else if (!regsurname.matches(Regex("^[a-zA-Z]+$"))) {
                Toast.makeText(this@RegActivity,"Surname can only contain letters",Toast.LENGTH_SHORT).show()
            }
            else if(username.isEmpty()) {
                Toast.makeText(this@RegActivity, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                Toast.makeText(this@RegActivity, "Username can only contain letters, numbers, and underscores", Toast.LENGTH_SHORT).show()
            }
            else if (password.isEmpty()) {
                Toast.makeText(this@RegActivity, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else if (!password.matches(Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$"))) {
                Toast.makeText(this@RegActivity, "Password must be at least 8 characters, include uppercase, lowercase, digit, and special character", Toast.LENGTH_LONG).show()
            }
            else {
                database.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Username already taken
                            Toast.makeText(this@RegActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            val hashedPassword = hashPassword(password)
                            val id = database.push().key!!
                            if (id != null) {
                                val user = Register(regname, regsurname, username, hashedPassword)
                                database.child(id).setValue(user).addOnCompleteListener {
                                    name.text.clear()
                                    surname.text.clear()
                                    regusername.text.clear()
                                    regpassword.text.clear()
                                    Toast.makeText(this@RegActivity, "User registered successfully", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@RegActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@RegActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }



        regclear.setOnClickListener {
            name.setText(null)
            surname.setText(null)
            regusername.setText(null)
            regpassword.setText(null)
        }

        regclose.setOnClickListener {
            finishAffinity()
        }

        tologinbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
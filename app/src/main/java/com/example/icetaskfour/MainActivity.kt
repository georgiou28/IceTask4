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


class MainActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uname = findViewById<EditText>(R.id.usernametxt)
        val pass = findViewById<EditText>(R.id.passwordtxt)
        val loginbtn = findViewById<Button>(R.id.loginbtn)
        val clearbtn = findViewById<Button>(R.id.clearbtn)
        val closebtn = findViewById<Button>(R.id.closebtn)
        val registerbtn = findViewById<Button>(R.id.toregisterbtn)

        database = FirebaseDatabase
            .getInstance()
            .getReference("users")


        fun hashPassword(pass: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(pass.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        loginbtn.setOnClickListener {
            val username = uname.text.toString()
            val password = pass.text.toString()
            val hashedPassword = hashPassword(password)
            if(username.isEmpty()) {
                Toast.makeText(this@MainActivity, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else if (password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
            database.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(Register::class.java)
                        if (user?.password == hashedPassword) {
                            Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivity, LoggedinActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Wrong password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }



        clearbtn.setOnClickListener {
            uname.setText(null)
            pass.setText(null)
        }

        closebtn.setOnClickListener {
            finishAffinity()
        }

        registerbtn.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }
    }
}

package com.example.builingact.data

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.buildingact.data.AuthRepository
import com.example.buildingact.models.Specialist
import com.example.buildingact.navigation.ROUTE_LOGIN
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SpecialistRepository(var navController:NavHostController, var context:Context) {
    var authRepository: AuthRepository
    var progress:ProgressDialog
    var specialists:ArrayList<Specialist>
    init {
        authRepository = AuthRepository(navController,context)
        if (!authRepository.isLoggedIn()){
            navController.navigate(ROUTE_LOGIN)
        }
        progress = ProgressDialog(context)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")

        specialists = mutableListOf<Specialist>() as ArrayList<Specialist>
    }

    fun saveSpecialist(specialistName:String, specialistphoneNumber:String, specialistemail:String, specialistyearsofexperience:String){
        var id = System.currentTimeMillis().toString()
        var specialistData = Specialist(specialistName,specialistphoneNumber,specialistemail,specialistyearsofexperience,id)
        var specialistRef = FirebaseDatabase.getInstance().getReference()
            .child("Specialists/$id")
        progress.show()
        specialistRef.setValue(specialistData).addOnCompleteListener {
            progress.dismiss()
            if (it.isSuccessful){
                Toast.makeText(context, "Saving successful", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "ERROR: ${it.exception!!.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun viewSpecialists():ArrayList<Specialist>{
        var ref = FirebaseDatabase.getInstance().getReference().child("Specialists")
        progress.show()
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progress.dismiss()
                specialists.clear()
                for (snap in snapshot.children){
                    var specialist = snap.getValue(Specialist::class.java)
                    specialists.add(specialist!!)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                progress.dismiss()
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        return specialists
    }

    fun deleteSpecialist(id:String){
        var delRef = FirebaseDatabase.getInstance().getReference()
            .child("Specialists/$id")
        progress.show()
        delRef.removeValue().addOnCompleteListener {
            progress.dismiss()
            if (it.isSuccessful){
                Toast.makeText(context, "Specialist deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateSpecialist(
        name: String,
        phoneNumber: String,
        email: String,
        yearsofexperience: String,
        id: String,
        id1: String,
    ){
        var updateRef = FirebaseDatabase.getInstance().getReference()
            .child("Specialists/$id")
        progress.show()
        var updateData = Specialist(name, phoneNumber, email, yearsofexperience, id)
        updateRef.setValue(updateData).addOnCompleteListener {
            progress.dismiss()
            if (it.isSuccessful){
                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
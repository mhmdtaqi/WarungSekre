package com.pb.warungsekre

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AdminActivity : AppCompatActivity() {

    private lateinit var orderListLayout: LinearLayout
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        orderListLayout = findViewById(R.id.orderListLayout)
        dbRef = FirebaseDatabase.getInstance().getReference("orders")

        loadOrders()
    }

    private fun loadOrders() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderListLayout.removeAllViews()

                for (orderSnap in snapshot.children) {
                    val order = orderSnap.value as Map<String, Any>

                    val code = order["code"] as? String ?: "???"
                    val menu = order["menu"] as? String ?: "-"
                    val status = order["status"] as? String ?: "-"

                    val itemLayout = LinearLayout(this@AdminActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(0, 16, 0, 16)
                    }

                    val infoText = TextView(this@AdminActivity).apply {
                        text = "Kode: $code\nMenu: $menu\nStatus: $status"
                    }

                    val doneButton = Button(this@AdminActivity).apply {
                        text = "Tandai Selesai"
                        isEnabled = status != "selesai"
                        setOnClickListener {
                            dbRef.child(code).child("status").setValue("selesai")
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@AdminActivity,
                                        "Transaksi $code selesai.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }

                    itemLayout.addView(infoText)
                    itemLayout.addView(doneButton)
                    orderListLayout.addView(itemLayout)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

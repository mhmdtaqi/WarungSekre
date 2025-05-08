package com.pb.warungsekre

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.android.material.card.MaterialCardView

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

                    val cardView = MaterialCardView(this@AdminActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(16, 8, 16, 8)
                        }
                        radius = resources.getDimension(R.dimen.card_corner_radius)
                        cardElevation = resources.getDimension(R.dimen.card_elevation)
                    }

                    val cardContent = LinearLayout(this@AdminActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(32, 24, 32, 24)
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

                    cardContent.addView(infoText)
                    cardContent.addView(doneButton)
                    cardView.addView(cardContent)
                    orderListLayout.addView(cardView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

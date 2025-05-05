package com.pb.warungsekre

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : AppCompatActivity() {
    private lateinit var menuGroup: RadioGroup
    private lateinit var orderButton: Button
    private lateinit var codeResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        menuGroup = findViewById(R.id.menuGroup)
        orderButton = findViewById(R.id.orderButton)
        codeResult = findViewById(R.id.codeResult)

        orderButton.setOnClickListener {
            Toast.makeText(this, "Tombol ditekan!", Toast.LENGTH_SHORT).show()
            val selectedId = menuGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Pilih menu dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val menu = findViewById<RadioButton>(selectedId).text.toString()
            val transactionCode = generateTransactionCode()

            val orderData = mapOf(
                "code" to transactionCode,
                "status" to "pending",
                "menu" to menu,
                "timestamp" to System.currentTimeMillis()
            )

            val ref = FirebaseDatabase.getInstance().getReference("orders").child(transactionCode)
            ref.setValue(orderData).addOnSuccessListener {
                codeResult.text = "Transaksi berhasil!\nKode: $transactionCode"
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal mengirim pesanan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateTransactionCode(): String {
        val sdf = SimpleDateFormat("HHmmss", Locale.getDefault())
        val time = sdf.format(Date())
        return "TXN$time"
    }
}

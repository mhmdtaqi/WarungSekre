package com.pb.warungsekre

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : AppCompatActivity() {
    private lateinit var menuGroup: RadioGroup
    private lateinit var orderButton: MaterialButton
    private lateinit var codeResult: MaterialTextView
    private lateinit var resultCard: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        menuGroup = findViewById(R.id.menuGroup)
        orderButton = findViewById(R.id.orderButton)
        codeResult = findViewById(R.id.codeResult)
        resultCard = findViewById(R.id.resultCard)

        // Sembunyikan card hasil di awal
        resultCard.visibility = View.GONE

        orderButton.setOnClickListener {
            val selectedId = menuGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Pilih menu dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val menu = findViewById<MaterialRadioButton>(selectedId).text.toString()
            val transactionCode = generateTransactionCode()

            // Tampilkan loading state
            orderButton.isEnabled = false
            orderButton.text = "Memproses..."

            val orderData = mapOf(
                "code" to transactionCode,
                "status" to "pending",
                "menu" to menu,
                "timestamp" to System.currentTimeMillis()
            )

            val ref = FirebaseDatabase.getInstance().getReference("orders").child(transactionCode)
            ref.setValue(orderData)
                .addOnSuccessListener {
                    codeResult.text = "Transaksi berhasil!\nKode: $transactionCode"
                    resultCard.visibility = View.VISIBLE
                    orderButton.text = "Pesan Lagi"
                    orderButton.isEnabled = true
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengirim pesanan", Toast.LENGTH_SHORT).show()
                    orderButton.text = "Pesan"
                    orderButton.isEnabled = true
                }
        }
    }

    private fun generateTransactionCode(): String {
        val sdf = SimpleDateFormat("HHmmss", Locale.getDefault())
        val time = sdf.format(Date())
        return "TXN$time"
    }
}

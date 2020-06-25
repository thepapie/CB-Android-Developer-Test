package com.example.inventoryapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Compares two elementData by date
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        /** Date Edit **/
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currYear = calendar.get(Calendar.YEAR)
        val currMonth = calendar.get(Calendar.MONTH)
        val currDay = calendar.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener {
                    _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                dateEdit.setText(format.format(calendar.time))
            }, currYear, currMonth, currDay)

        dateEdit.showSoftInputOnFocus = false
        dateEdit.setOnTouchListener { view, event ->
            if (event?.action == MotionEvent.ACTION_DOWN)
                picker.show()
            view?.onTouchEvent(event) ?: true
        }

        /** List **/
        val data: MutableList<ElementData> = arrayListOf()
        val adapter = ListAdapter(this, data, formatter)
        recyclerList.adapter = adapter
        recyclerList.setHasFixedSize(false)
        recyclerList.layoutManager = LinearLayoutManager(this)
        recyclerList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        /** Add Button **/
        addButton.setOnClickListener {
            val gtin = gtinEdit.text.toString()
            val date = LocalDate.parse(dateEdit.text.toString(), formatter)

            // Check input validity
            if (gtin == "")
                Toast.makeText(this, "Please enter a GTIN", Toast.LENGTH_LONG).show()
            else if (!isGtinFormat(gtin))
                Toast.makeText(this, "Invalid GTIN format", Toast.LENGTH_LONG).show()
            else if (date == null)
                Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_LONG).show()

            else {
                // Add datas to corresponding element in list
                var added = false
                for (element in data) {
                    if (gtin == element.gtin) {
                        if (date < element.date) {
                            Toast.makeText(
                                this, "Date changed for reference " + gtin,
                                Toast.LENGTH_LONG
                            ).show()
                            element.date = date
                        } else
                            Toast.makeText(
                                this, "Date not changed for reference " + gtin,
                                Toast.LENGTH_LONG
                            ).show()
                        added = true
                        break
                    }
                }
                if (!added) {
                    Toast.makeText(this, "Added reference " + gtin, Toast.LENGTH_LONG).show()
                    data.add(ElementData(gtin, date))
                }

                // Update adapter for display
                data.sortBy { it.date }
                adapter.data = data
                adapter.notifyDataSetChanged()
            }
        }
    }

    // Return true if str is a valid GTIN
    private fun isGtinFormat(str: String) : Boolean {
        if (str.length != 8 || str.length !in 12..14)
            return false
        str.forEach {
            if (!it.isDigit())
                return false
        }
        return true
    }
}

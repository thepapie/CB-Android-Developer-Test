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
import java.time.format.DateTimeParseException
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init date formatting tools
        val myDateFormat = "dd/MM/yyyy"
        val format = SimpleDateFormat(myDateFormat, Locale.getDefault())
        val formatter = DateTimeFormatter.ofPattern(myDateFormat)

        /** DateEdit **/
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

            // Get GTIN and check validity
            val gtin = gtinEdit.text.toString()
            if (gtin == "") {
                Toast.makeText(this, "Please enter a GTIN", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if (!isGtinFormat(gtin)) {
                Toast.makeText(this, "Invalid GTIN format", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Get date and check validity
            val date: LocalDate
            try {
                date = LocalDate.parse(dateEdit.text.toString(), formatter)
            } catch (e: DateTimeParseException) {
                Toast.makeText(this, "Please enter a valid date\n(\"dd/mm/yyy\")", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Add datas to corresponding element in list and notify the change
            var added = false
            for (element in data) {
                if (gtin == element.gtin) {
                    if (date < element.date) {
                        Toast.makeText(this, "Date changed for reference " + gtin,
                            Toast.LENGTH_LONG).show()
                        element.date = date
                    }
                    else
                        Toast.makeText(this, "Date not changed for reference " + gtin,
                            Toast.LENGTH_LONG).show()
                    added = true
                    break
                }
            }

            // Notify if no data where added and why
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

    // Return true if str is a valid GTIN
    private fun isGtinFormat(str: String) : Boolean {
        if (str.length != 8 && str.length !in 12..14)
            return false
        str.forEach {
            if (!it.isDigit())
                return false
        }
        return true
    }
}

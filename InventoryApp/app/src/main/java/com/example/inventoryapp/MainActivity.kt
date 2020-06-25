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
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Compares two elementData by date
        val dateComparator = Comparator {rhs: ElementData, lhs: ElementData ->
            if (rhs.date[3] == lhs.date[3])
                if (rhs.date[4] == lhs.date[4])
                    if (rhs.date[0] == lhs.date[0])
                        if (rhs.date[1] == lhs.date[1])
                            rhs.date[1] - lhs.date[1]
                        else
                            rhs.date[1] - lhs.date[1]
                    else
                        rhs.date[0] - lhs.date[0]
                else
                    rhs.date[4] - lhs.date[4]
            else
                rhs.date[3] - lhs.date[3]
        }
        val format = SimpleDateFormat("dd/MM", Locale.getDefault())

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
        val adapter = ListAdapter(this, data,"$currDay/$currMonth")
        recyclerList.adapter = adapter
        recyclerList.setHasFixedSize(false)
        recyclerList.layoutManager = LinearLayoutManager(this)
        recyclerList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        /** Add Button **/
        addButton.setOnClickListener {
            val gtin = gtinEdit.text.toString()
            val date = dateEdit.text.toString()

            // Check input validity
            if (gtin == "")
                Toast.makeText(this, "Please enter a GTIN", Toast.LENGTH_LONG).show()
            else if (!isGtinFormat(gtin))
                Toast.makeText(this, "Invalid GTIN format", Toast.LENGTH_LONG).show()
            else if (date == "")
                Toast.makeText(this, "Please enter a date", Toast.LENGTH_LONG).show()
            else if (!isDateFormat(date, format))
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_LONG).show()

            else {
                // Add datas to corresponding element in list
                var added = false
                for (element in data) {
                    if (gtin == element.gtin) {
                        if (isSooner(date, element.date)) {
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
                data.sortWith(dateComparator)
                adapter.data = data
                adapter.notifyDataSetChanged()
            }
        }
    }

    // Return true if str is a valid GTIN
    private fun isGtinFormat(str: String) : Boolean {
        if (str.length < 8 || str.length > 14)
            return false
        str.forEach {
            if (!it.isDigit())
                return false
        }
        return true
    }

    // Retrun true if str is formated as 'format'
    private fun isDateFormat(str: String, format: SimpleDateFormat) : Boolean {
        if (str.length != 5)
            return false
        return format.parse(str) != null
    }

    // Return true if rhs is sooner than lhs (requiered format : 'dd/mm')
    private fun isSooner(rhs: String, lhs: String) : Boolean {
        if (rhs[3] == lhs[3])
            if (rhs[4] == lhs[4])
                if (rhs[0] == lhs[0])
                    if (rhs[1] == lhs[1])
                        return false
                    else
                        return rhs[1] < lhs[1]
                else
                    return rhs[0] < lhs[0]
            else
                return rhs[4] < lhs[4]
        else
            return rhs[3] < lhs[3]
    }
}

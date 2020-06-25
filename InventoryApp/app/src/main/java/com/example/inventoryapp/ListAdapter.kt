package com.example.inventoryapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(val context: Activity, var data: List<ElementData>, val currentDate: String)
    : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val gtin: TextView = itemview.findViewById(R.id.gtin)
        val date: TextView = itemview.findViewById(R.id.expiration_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_list_element, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elementData = data[position]

        // Set text informations
        holder.gtin.text = elementData.gtin
        holder.date.text = elementData.date

        // Change date color
        if (isWarning(currentDate, elementData.date))
            holder.date.setBackgroundResource(R.color.warning)
        else
            holder.date.setBackgroundResource(R.color.safe)
    }

}

fun isWarning(rhs: String, lhs: String) : Boolean {
    val iRhsDay = rhs.substring(0,1).toInt()
    val iRhsMonth = rhs.substring(3,4).toInt()
    val iLhsDay = lhs.substring(0,1).toInt()
    val iLhsMonth = lhs.substring(3, 4).toInt()

    val iRhs = iRhsMonth * 30 + iRhsDay
    val iLhs = iLhsMonth * 30 + iLhsDay

    return (iRhs - iLhs) > 2
}
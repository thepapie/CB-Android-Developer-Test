package com.example.inventoryapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListAdapter(private val context: Activity, var data: List<ElementData>, private val formatter: DateTimeFormatter)
    : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val currentDate: LocalDate = LocalDate.now()
    private val twoDayslater: LocalDate = currentDate.plusDays(2)

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
        holder.date.text = elementData.date.format(formatter)

        // Change date color to warn user
        when {
            elementData.date <= currentDate -> holder.date.setBackgroundResource(R.color.expired)
            elementData.date <= twoDayslater -> holder.date.setBackgroundResource(R.color.warning)
            else -> holder.date.setBackgroundResource(R.color.safe)
        }
    }

}
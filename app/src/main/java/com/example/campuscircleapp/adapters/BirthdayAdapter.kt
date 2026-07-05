package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.BirthdayResponse

class BirthdayAdapter(private val items: List<BirthdayResponse>) :
    RecyclerView.Adapter<BirthdayAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.birthdayName)
        val dob: TextView = view.findViewById(R.id.birthdayDate)
        val avatar: TextView = view.findViewById(R.id.birthdayAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_birthday, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.dob.text = item.birthdayDay?.let { "${item.birthdayMonth} $it" } ?: ""
        holder.avatar.text = item.name.firstOrNull()?.uppercase() ?: "?"
    }
}

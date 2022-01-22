package com.nullvoid.crimson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.nullvoid.crimson.R

class ContactChooseAdapter(
    private val context: Context,
    private var data: ArrayList<Pair<String, String>>
) : RecyclerView.Adapter<ContactChooseAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameField: MaterialTextView = itemView.findViewById(R.id.name)
        val phoneField: MaterialTextView = itemView.findViewById(R.id.phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater =
            LayoutInflater.from(context).inflate(R.layout.layout_contact_item, parent, false)
        return ContactViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        with(holder) {
            nameField.text = data[position].first
            phoneField.text = data[position].second
        }
    }

    override fun getItemCount() = data.size

    fun setData(data: ArrayList<Pair<String, String>>) {
        this.data = data
        notifyDataSetChanged()
    }

}
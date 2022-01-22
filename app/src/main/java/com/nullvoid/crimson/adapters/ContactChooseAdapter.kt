package com.nullvoid.crimson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.nullvoid.crimson.R
import com.nullvoid.crimson.customs.DbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactChooseAdapter(
    private val context: Context,
    private var data: ArrayList<Pair<String, String>>
) : RecyclerView.Adapter<ContactChooseAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameField: MaterialTextView = itemView.findViewById(R.id.name)
        val phoneField: MaterialTextView = itemView.findViewById(R.id.phone)
        val root: MaterialCardView = itemView.findViewById(R.id.root)
        val progress: CircularProgressIndicator = itemView.findViewById(R.id.progress)
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
            root.setOnClickListener {
                progress.visibility = View.VISIBLE
                GlobalScope.launch {
                    try {
                        DbHelper(context).sendRequest("+91" + data[position].second)
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "Request Sent", Toast.LENGTH_LONG).show()
                            progress.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            progress.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() = data.size

    fun setData(data: ArrayList<Pair<String, String>>) {
        this.data = data
        notifyDataSetChanged()
    }

}
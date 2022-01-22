package com.nullvoid.crimson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.nullvoid.crimson.R
import com.nullvoid.crimson.RequestActivity
import com.nullvoid.crimson.customs.DbHelper
import com.nullvoid.crimson.data.model.CrimsonUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RequestAdapter(private var context: Context) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    private var requests = arrayListOf<CrimsonUser>()
    private lateinit var dbHelper: DbHelper

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: MaterialTextView = itemView.findViewById(R.id.req_phone)
        val accept: MaterialButton = itemView.findViewById(R.id.req_accept)
        val decline: MaterialButton = itemView.findViewById(R.id.req_decline)
        val progress:CircularProgressIndicator = itemView.findViewById(R.id.progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_request_item, parent, false)
        dbHelper = DbHelper(context)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.title.text = requests[position].userPhone
        holder.accept.setOnClickListener {
            holder.accept.visibility = View.GONE
            holder.progress.visibility = View.VISIBLE
            holder.decline.isEnabled = false
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    dbHelper.allowRequest(requests[position].userId)
                    launch(Dispatchers.Main) {
                        requests.remove(requests[position])
                        setData(requests)
                        Toast.makeText(context, "Request Accepted", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                        holder.accept.visibility = View.VISIBLE
                        holder.progress.visibility = View.GONE
                        holder.decline.isEnabled = true
                    }
                }
            }
        }
        holder.decline.setOnClickListener {
            holder.decline.visibility = View.GONE
            holder.progress.visibility = View.VISIBLE
            holder.accept.isEnabled = false
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    dbHelper.denyRequest(requests[position].userId)
                    launch(Dispatchers.Main) {
                        requests.remove(requests[position])
                        setData(requests)
                        Toast.makeText(context, "Request Denied", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                        holder.decline.visibility = View.VISIBLE
                        holder.progress.visibility = View.GONE
                        holder.accept.isEnabled = true
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = requests.size

    fun setData(list: ArrayList<CrimsonUser>) {
        requests = list
        notifyDataSetChanged()
        if (list.isEmpty()) {
            (context as RequestActivity).showNullView()
        } else {
            (context as RequestActivity).hideNullView()
        }
    }

}
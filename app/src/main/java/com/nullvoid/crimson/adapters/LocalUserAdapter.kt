package com.nullvoid.crimson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.nullvoid.crimson.R
import com.nullvoid.crimson.data.model.LocalCrimsonUser

class LocalUserAdapter : RecyclerView.Adapter<LocalUserAdapter.SepiaViewHolder>() {

    private var users: List<LocalCrimsonUser> = listOf()
    private lateinit var context: Context

    inner class SepiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: AppCompatTextView = itemView.findViewById(R.id.userItem_title)
        val endIcon: MaterialButton = itemView.findViewById(R.id.userItem_endIcon)
        val root: MaterialCardView = itemView.findViewById(R.id.userItem_root)
        val locIcon: MaterialButton = itemView.findViewById(R.id.userItem_locIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SepiaViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.layout_user_item, parent, false)
        return SepiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SepiaViewHolder, position: Int) {
        holder.titleView.text = users[position].basic.userPhone
        if (users[position].crimsonExtras?.inEmergency == false) {
            holder.endIcon.icon = AppCompatResources.getDrawable(context, R.drawable.ic_check_b)
            TooltipCompat.setTooltipText(holder.endIcon, context.getString(R.string.safe))
        } else {
            holder.endIcon.icon = AppCompatResources.getDrawable(context, R.drawable.ic_error)
            TooltipCompat.setTooltipText(
                holder.endIcon,
                context.getString(R.string.emergency_alert)
            )
        }
        holder.root.setOnClickListener {
//            context.startActivity(Intent(context, SepiaContactActivity::class.java).also {
//                it.putExtra(Constant.EXTRAS_ID,
//                    users[position])
//            })
        }
    }

    override fun getItemCount(): Int = users.size

    fun setData(list: List<LocalCrimsonUser>) {
        users = list
        notifyDataSetChanged()
    }

}
package net.ommoks.azza.android.app.pass_on_messages.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.ommoks.azza.android.app.pass_on_messages.R
import net.ommoks.azza.android.app.pass_on_messages.common.Utils
import net.ommoks.azza.android.app.pass_on_messages.data.model.getStringRes
import net.ommoks.azza.android.app.pass_on_messages.ui.model.AddFilterItem
import net.ommoks.azza.android.app.pass_on_messages.ui.model.FilterItem
import net.ommoks.azza.android.app.pass_on_messages.ui.model.ListItem

class FilterAdapter(
    private val listener: OnFilterActionsListener
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(FilterDiffCallback()) {

    interface OnFilterActionsListener {
        fun onFilterClick(filter: FilterItem)
        fun onDeleteClick(filter: FilterItem)
        fun onAddFilterClick()
    }

    companion object {
        private const val TYPE_FILTER = 0
        private const val TYPE_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FilterItem -> TYPE_FILTER
            is AddFilterItem -> TYPE_ADD
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_FILTER -> {
                val view = inflater.inflate(R.layout.item_filter, parent, false)
                FilterViewHolder(view)
            }
            TYPE_ADD -> {
                val view = inflater.inflate(R.layout.item_add_filter, parent, false)
                AddViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) // 현재 아이템 가져오기
        when (holder) {
            is FilterViewHolder -> {
                bindFilterViewHolder(holder, item as FilterItem)
            }
            is AddViewHolder -> {
                holder.itemView.setOnClickListener {
                    listener.onAddFilterClick()
                }
            }
        }
    }

    private fun bindFilterViewHolder(holder: FilterViewHolder, filter: FilterItem) {
        holder.filterName.text = filter.name
        holder.passOnTo.text = filter.passOnTo
        val sb = StringBuilder()
        filter.rules.forEach {
            sb.append("(")
            sb.append(it.type.getStringRes(holder.itemView.context))
            sb.append(") ")
            sb.append(it.phrase)
            sb.append("\n")
        }
        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
        }
        holder.rules.text = sb.toString()
        holder.recent.text = Utils.dateTimeFromMillSec(filter.recent)

        holder.itemView.setOnClickListener {
            listener.onFilterClick(filter)
        }

        holder.deleteFilterIcon.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle(R.string.dialog_title_delete_filter)
                .setMessage(R.string.dialog_message_delete_filter)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.delete) { dialog, _ ->
                    listener.onDeleteClick(filter)
                    dialog.dismiss()
                }
                .show()
        }
    }

    class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filterName: TextView = view.findViewById(R.id.filter_name)
        val passOnTo: TextView = view.findViewById(R.id.pass_on_to)
        val rules: TextView = view.findViewById(R.id.rules)
        val recent: TextView = view.findViewById(R.id.recent)
        val deleteFilterIcon: ImageView = view.findViewById(R.id.delete_filter_icon)
    }

    class AddViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

class FilterDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return if (oldItem is FilterItem && newItem is FilterItem) {
            oldItem.id == newItem.id
        } else {
            oldItem::class == newItem::class
        }
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}

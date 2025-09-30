package net.ommoks.azza.android.app.passonnotificationsimport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.ommoks.azza.android.app.passonnotifications.AddFilterItem
import net.ommoks.azza.android.app.passonnotifications.Filter
import net.ommoks.azza.android.app.passonnotifications.ListItem
import net.ommoks.azza.android.app.passonnotifications.R

class FilterAdapter(
    private val items: MutableList<ListItem>,
    private val listener: OnFilterActionsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnFilterActionsListener {
        fun onModifyFilterClick(filter: Filter, position: Int)
    }

    companion object {
        private const val TYPE_FILTER = 0
        private const val TYPE_ADD = 1
    }

    // ViewHolder for the complex Filter item
    class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filterName: TextView = view.findViewById(R.id.filter_name)
        val passOnTo: TextView = view.findViewById(R.id.pass_on_to)
        val rules: TextView = view.findViewById(R.id.rules)
        val deleteFilterIcon: ImageView = view.findViewById(R.id.delete_filter_icon)
    }

    // ViewHolder for the 'Add' button item
    class AddViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Filter -> TYPE_FILTER
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
        when (holder) {
            is FilterViewHolder -> {
                val filter = items[position] as Filter
                bindFilterViewHolder(holder, filter, position)
            }
            is AddViewHolder -> {
                holder.itemView.setOnClickListener {
                    val newFilter = Filter(name = "", rules = mutableListOf(), passOnTo = "")
                    val insertPosition = items.size - 1
                    items.add(insertPosition, newFilter)
                    notifyItemInserted(insertPosition)
                    listener.onModifyFilterClick(newFilter, insertPosition)
                }
            }
        }
    }

    private fun bindFilterViewHolder(holder: FilterViewHolder, filter: Filter, position: Int) {
        holder.filterName.text = filter.name
        holder.passOnTo.text = filter.passOnTo
        val sb = StringBuilder()
        filter.rules.forEach {
            sb.append(it.type.text)
            sb.append(": ")
            sb.append(it.phrase)
            sb.append("\n")
        }
        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
        }
        holder.rules.text = sb.toString()

        holder.itemView.setOnClickListener {
            listener.onModifyFilterClick(filter, position)
        }

        holder.deleteFilterIcon.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle(R.string.dialog_title_delete_filter)
                .setMessage(R.string.dialog_message_delete_filter)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.delete) { dialog, _ ->
                    items.removeAt(currentPosition)
                    notifyItemRemoved(currentPosition)
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun getFilterItems(): List<Filter> {
        return items.filterIsInstance<Filter>()
    }

    fun updateFilter(filter: Filter, position: Int) {
        items[position] = filter
        notifyItemChanged(position)
    }
}

package aepsapp.easypay.com.aepsandroid.adapters

import aepsapp.easypay.com.aepsandroid.R
import aepsapp.easypay.com.aepsandroid.entities.ServiceEntity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Created by Viral on 12-03-2018.
 */
class MenuAdapter(context: Context, val services: List<ServiceEntity>, val clicked: (Int) -> Unit) : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    private var inflater: LayoutInflater? = null

    init {
        inflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        return MenuHolder(inflater!!.inflate(R.layout.menu_item, null))
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {

        val service = services[position]
        holder.txtMenu.text = service.subcategory
        if (!TextUtils.isEmpty(service.imagePath))
            holder.draweeView.setImageURI(service.imagePath)
        else
            holder.draweeView.setImageResource(service.imgRes)

            holder.itemView.setOnClickListener {
                clicked(position)
            }
    }


    class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val draweeView = itemView.findViewById<SimpleDraweeView>(R.id.menuitem_img)
        val txtMenu = itemView.findViewById<TextView>(R.id.menuitem_txtname)
    }
}
package com.example.projemanag.activities.activites.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.activities.activites.models.Board

class BoardItemsAdapter(private var context:Context,private var list:ArrayList<Board>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnclickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return myViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if(holder is myViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_board_image))

            holder.itemView.findViewById<TextView>(R.id.tv_name).text=model.name

            holder.itemView.findViewById<TextView>(R.id.tv_created_by).text="created by ${model.createdBy}"

            holder.itemView.setOnClickListener{
                if(onClickListener!=null){
                    onClickListener!!.onclick(position,model)
                }
            }
        }
    }

    interface  OnclickListener{
        fun onclick(position: Int,model:Board)
    }

    fun setOnClickListener(onClickListener: OnclickListener){
        this.onClickListener=onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class myViewHolder(view:View):RecyclerView.ViewHolder(view)

}
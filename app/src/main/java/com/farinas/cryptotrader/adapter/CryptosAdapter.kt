package com.farinas.cryptotrader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farinas.cryptotrader.R
import com.farinas.cryptotrader.databinding.CryptoRowBinding
import com.farinas.cryptotrader.model.Crypto
import com.squareup.picasso.Picasso

/**
 * Created by efarinas on $ 6/17/21.
 */
class CryptosAdapter (val cryptosAdapterListener: CryptosAdapterListener):
        RecyclerView.Adapter<CryptosAdapter.ViewHolder>() {

    var cryptoList: List<Crypto> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ViewHolder(layoutInflater.inflate(R.layout.crypto_row, parent, false))
    }

    override fun getItemCount(): Int {
        return cryptoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crypto = cryptoList[position]

        Picasso.get().load(crypto.imageUrl).into(holder.binding.image)
        holder.binding.nameTextView.text = crypto.name
        holder.binding.availableTextView.text = holder.itemView.context.getString(R.string.available_message, crypto.available.toString())
        holder.binding.buyButton.setOnClickListener {
            cryptosAdapterListener.onBuyCryptoClicked(crypto)
        }
    }

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val binding = CryptoRowBinding.bind(view)
    }
}
package com.example.cryptohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptohub.R
import com.example.cryptohub.databinding.ItemRecyclerMarketBinding
import com.example.cryptohub.model.Coin
import java.math.RoundingMode

class CoinsAdapter(private val coinsEvents: CoinsEvents) : RecyclerView.Adapter<CoinsAdapter.CoinsViewHolder>() {

    private val data: ArrayList<Coin> = arrayListOf()

    inner class CoinsViewHolder(private val binding: ItemRecyclerMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindData(coin: Coin) {

            binding.txtCoinName.text = coin.coinName
            setTxtChange(coin.change, binding, coin)
            binding.txtPrice.text = coin.coinPrice

            val marketCap = coin.marketCap / 1000000000
            binding.txtMarketCap.text =
                marketCap.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString() + " B"

            Glide
                .with(binding.root)
                .load( coin.coinImg)
                .into(binding.imgItem)


            binding.root.setOnClickListener {
                coinsEvents.onCoinItemClicked(coin)
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        val binding =
            ItemRecyclerMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() = data.size


    fun setData(data: List<Coin>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }


    private fun setTxtChange(change: Double, binding: ItemRecyclerMarketBinding, coin: Coin) {

        if (change > 0) {
            binding.txtChange.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )
            binding.txtChange.text =
                coin.change.toString().substring(0, 4) + "%"
        } else if (change < 0) {
            binding.txtChange.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )
            binding.txtChange.text =
                coin.change.toString().substring(0, 5) + "%"
        } else {
            binding.txtChange.text = "0%"
            binding.txtChange.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.secondaryTextColor
                )
            )
        }


    }


    interface CoinsEvents {
        fun onCoinItemClicked(coin: Coin)
    }

}
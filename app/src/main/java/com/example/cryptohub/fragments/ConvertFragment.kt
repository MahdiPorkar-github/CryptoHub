package com.example.cryptohub.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cryptohub.App
import com.example.cryptohub.R
import com.example.cryptohub.databinding.FragmentConvertBinding
import com.example.cryptohub.model.GetExchangeResponse
import com.example.cryptohub.model.Success
import com.example.cryptohub.networking.NetworkChecker
import java.math.RoundingMode

class ConvertFragment : Fragment(R.layout.fragment_convert) {


    lateinit var binding: FragmentConvertBinding
    private var destPosition = 0
    private var originPosition = 0
    private val remoteApi = App.remoteApi
    private val exchangeArr = ArrayList<Double>()
    private val networkChecker by lazy { NetworkChecker(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConvertBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initListeners()

    }

    private fun initUi() {


        binding.spOrigin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                setOriginSpinner(adapterView?.getItemAtPosition(position).toString())
                originPosition = position
                binding.edtOriginCurrencyAmount.setText("")
                binding.destCurrencyRate.text = ""
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // always an item is selected
            }

        }
        binding.spDest.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                setDestSpinner(adapterView?.getItemAtPosition(position).toString())
                destPosition = position
                binding.destCurrencyAmount.text = ""
                binding.destCurrencyRate.text = ""

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // always an item is selected
            }

        }
        getExchangeRates()

    }

    private fun initListeners() {

        binding.btnConvert.setOnClickListener {
            if (checkIfValid()) {
                convertCurrency(binding.spOrigin.adapter.getItem(originPosition).toString())

                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            } else {
                Toast.makeText(context, "Please Enter The Amount", Toast.LENGTH_SHORT).show()
            }
        }

        binding.convertSwipeRefresh.setOnRefreshListener {

            initUi()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.convertSwipeRefresh.isRefreshing = false
            }, 1500)

        }
    }


    private fun checkIfValid(): Boolean {
        return binding.edtOriginCurrencyAmount.text.toString() != ""
    }

    private fun convertCurrency(originCurrency: String) {
        binding.edtOriginCurrencyAmount.clearFocus()
        if (originCurrency == "USD") {
            val result = convertCurrencyFromUSD(
                binding.edtOriginCurrencyAmount.text.toString().toDouble()
            )
            binding.destCurrencyRate.text = exchangeArr[destPosition].toString()
            binding.destCurrencyAmount.text =
                result.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
            binding.resTvOriginAmount.text = binding.edtOriginCurrencyAmount.text.toString()
            binding.resTvOriginCurrency.text = "USD"
            binding.textView3.text = "="
            binding.resDestCurrencyAmount.text =
                result.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
            binding.resTvDestCurrency.text = binding.spDest.adapter.getItem(destPosition).toString()

        } else {
            val temp = convertCurrencyToUSD(
                binding.edtOriginCurrencyAmount.text.toString().toDouble()
            )
            val res = convertCurrencyFromUSD(temp)
            binding.destCurrencyAmount.text =
                res.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
            binding.resTvOriginAmount.text = binding.edtOriginCurrencyAmount.text.toString()
            binding.resTvOriginCurrency.text =
                binding.spDest.adapter.getItem(originPosition).toString()
            binding.textView3.text = "="
            binding.resDestCurrencyAmount.text =
                res.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
            binding.resTvDestCurrency.text = binding.spDest.adapter.getItem(destPosition).toString()

            binding.destCurrencyRate.text =
                convertCurrencyFromUSD(convertCurrencyToUSD(1.0)).toBigDecimal()
                    .setScale(1, RoundingMode.HALF_UP).toString()

            binding.tvDestCurrency.text = binding.spDest.adapter.getItem(destPosition).toString()


        }
    }


    private fun convertCurrencyFromUSD(amount: Double): Double {
        return amount * exchangeArr[destPosition]
    }


    private fun convertCurrencyToUSD(amount: Double): Double {
        return amount / exchangeArr[originPosition]
    }


    private fun setOriginSpinner(symbol: String) {
        when (symbol) {
            "USD" -> {
                binding.originName.text = "US Dollar"
                binding.originCurrencySymbol.text = "$"
                binding.imgOriginCurrency.setImageResource(R.drawable.us_flag)
                binding.tvOriginCurrency.text = "USD"
            }
            "CAD" -> {
                binding.originName.text = "Canadian dollar"
                binding.originCurrencySymbol.text = "CA$"
                binding.imgOriginCurrency.setImageResource(R.drawable.ca_flag)
                binding.tvOriginCurrency.text = "CAD"
            }
            "EUR" -> {
                binding.originName.text = "Euro"
                binding.originCurrencySymbol.text = "€"
                binding.imgOriginCurrency.setImageResource(R.drawable.eur_flag)
                binding.tvOriginCurrency.text = "EUR"
            }

            "HKD" -> {
                binding.originName.text = "Hong Kong dollar"
                binding.originCurrencySymbol.text = "“元”"
                binding.imgOriginCurrency.setImageResource(R.drawable.hk_flag)
                binding.tvOriginCurrency.text = "HKD"
            }

            "ISK" -> {
                binding.originName.text = "Icelandic króna"
                binding.originCurrencySymbol.text = "Íkr"
                binding.imgOriginCurrency.setImageResource(R.drawable.ic_flag)
                binding.tvOriginCurrency.text = "ISK"

            }

            "PHP" -> {
                binding.originName.text = "Philippine peso"
                binding.originCurrencySymbol.text = "₱"
                binding.imgOriginCurrency.setImageResource(R.drawable.flp_flag)
                binding.tvOriginCurrency.text = "PHP"
            }

            "DKK" -> {
                binding.originName.text = "Danish krone"
                binding.originCurrencySymbol.text = "kr"
                binding.imgOriginCurrency.setImageResource(R.drawable.da_flag)
                binding.tvOriginCurrency.text = "DKK"
            }

            "HUF" -> {
                binding.originName.text = "Hungarian forint"
                binding.originCurrencySymbol.text = "ft"
                binding.imgOriginCurrency.setImageResource(R.drawable.hu_flag)
                binding.tvOriginCurrency.text = "HUF"
            }

            "CZK" -> {
                binding.originName.text = "Czech koruna"
                binding.originCurrencySymbol.text = "Kč"
                binding.imgOriginCurrency.setImageResource(R.drawable.cz_flag)
                binding.tvOriginCurrency.text = "CZK"
            }

            "AUD" -> {
                binding.originName.text = "Australian dollar"
                binding.originCurrencySymbol.text = "A$"
                binding.imgOriginCurrency.setImageResource(R.drawable.as_flag)
                binding.tvOriginCurrency.text = "AUD"
            }

            "RON" -> {
                binding.originName.text = "Romanian leu"
                binding.originCurrencySymbol.text = "lei"
                binding.imgOriginCurrency.setImageResource(R.drawable.ro_flag)
                binding.tvOriginCurrency.text = "RON"
            }

            "SEK" -> {
                binding.originName.text = "Swedish krona"
                binding.originCurrencySymbol.text = "kr"
                binding.imgOriginCurrency.setImageResource(R.drawable.sw_flag)
                binding.tvOriginCurrency.text = "SEK"
            }

            "IDR" -> {
                binding.originName.text = "Indonesian rupiah"
                binding.originCurrencySymbol.text = "Rp"
                binding.imgOriginCurrency.setImageResource(R.drawable.id_flag)
                binding.tvOriginCurrency.text = "IDR"
            }

            "INR" -> {
                binding.originName.text = "Indian rupee"
                binding.originCurrencySymbol.text = "₹"
                binding.imgOriginCurrency.setImageResource(R.drawable.in_flag)
                binding.tvOriginCurrency.text = "INR"
            }

            "BRL" -> {
                binding.originName.text = "Brazilian real"
                binding.originCurrencySymbol.text = "R$"
                binding.imgOriginCurrency.setImageResource(R.drawable.br_flag)
                binding.tvOriginCurrency.text = "BRL"
            }

            "RUB" -> {
                binding.originName.text = "Russian ruble"
                binding.originCurrencySymbol.text = "₽"
                binding.imgOriginCurrency.setImageResource(R.drawable.rs_flag)
                binding.tvOriginCurrency.text = "RUB"
            }

            "HRK" -> {
                binding.originName.text = "Croatian kuna"
                binding.originCurrencySymbol.text = "kn"
                binding.imgOriginCurrency.setImageResource(R.drawable.rs_flag)
                binding.tvOriginCurrency.text = "HRK"
            }

            "JPY" -> {
                binding.originName.text = "Japanese yen"
                binding.originCurrencySymbol.text = "¥"
                binding.imgOriginCurrency.setImageResource(R.drawable.jp_flag)
                binding.tvOriginCurrency.text = "JPY"
            }

            "THB" -> {
                binding.originName.text = "Thai baht"
                binding.originCurrencySymbol.text = "฿"
                binding.imgOriginCurrency.setImageResource(R.drawable.th_flag)
                binding.tvOriginCurrency.text = "THB"
            }

            "CHF" -> {
                binding.originName.text = "Swiss franc"
                binding.originCurrencySymbol.text = "CHF"
                binding.imgOriginCurrency.setImageResource(R.drawable.sz_flag)
                binding.tvOriginCurrency.text = "CHF"
            }

            "SGD" -> {
                binding.originName.text = "Singapore dollar"
                binding.originCurrencySymbol.text = "S$"
                binding.imgOriginCurrency.setImageResource(R.drawable.sn_flag)
                binding.tvOriginCurrency.text = "SGD"
            }

            "PLN" -> {
                binding.originName.text = "Polish zloty"
                binding.originCurrencySymbol.text = "zł"
                binding.imgOriginCurrency.setImageResource(R.drawable.pl_flag)
                binding.tvOriginCurrency.text = "PLN"
            }


            "BGN" -> {
                binding.originName.text = "Bulgarian lev"
                binding.originCurrencySymbol.text = "лв"
                binding.imgOriginCurrency.setImageResource(R.drawable.bu_flag)
                binding.tvOriginCurrency.text = "BGN"
            }
            "CNY" -> {
                binding.originName.text = "Chinese yuan"
                binding.originCurrencySymbol.text = "¥ /元"
                binding.imgOriginCurrency.setImageResource(R.drawable.ch_flag)
                binding.tvOriginCurrency.text = "CNY"
            }
            "NOK" -> {
                binding.originName.text = "Norwegian krone"
                binding.originCurrencySymbol.text = "kr"
                binding.imgOriginCurrency.setImageResource(R.drawable.no_flag)
                binding.tvOriginCurrency.text = "NOK"
            }
            "NZD" -> {
                binding.originName.text = "New Zealand dollar"
                binding.originCurrencySymbol.text = "$"
                binding.imgOriginCurrency.setImageResource(R.drawable.nz_flag)
                binding.tvOriginCurrency.text = "NZD"
            }
            "ZAR" -> {
                binding.originName.text = "South african rand"
                binding.originCurrencySymbol.text = "R"
                binding.imgOriginCurrency.setImageResource(R.drawable.sf_flag)
                binding.tvOriginCurrency.text = "ZAR"
            }
            "MXN" -> {
                binding.originName.text = "Mexican peso"
                binding.originCurrencySymbol.text = "$"
                binding.imgOriginCurrency.setImageResource(R.drawable.mx_flag)
                binding.tvOriginCurrency.text = "MXN"
            }

            "GBP" -> {
                binding.originName.text = "Pounds sterling"
                binding.originCurrencySymbol.text = "£"
                binding.imgOriginCurrency.setImageResource(R.drawable.uk_flag)
                binding.tvOriginCurrency.text = "GBP"
            }
            "KRW" -> {
                binding.originName.text = "South Korean won"
                binding.originCurrencySymbol.text = "₩"
                binding.imgOriginCurrency.setImageResource(R.drawable.ks_flag)
                binding.tvOriginCurrency.text = "KRW"
            }
            "MYR" -> {
                binding.originName.text = "Malaysian ringgit"
                binding.originCurrencySymbol.text = "RM"
                binding.imgOriginCurrency.setImageResource(R.drawable.my_flag)
                binding.tvOriginCurrency.text = "MYR"
            }
        }

    }

    private fun setDestSpinner(symbol: String) {
        when (symbol) {
            "USD" -> {
                binding.originName.text = "US Dollar"
                binding.originCurrencySymbol.text = "$"
                binding.imgOriginCurrency.setImageResource(R.drawable.us_flag)
                binding.tvOriginCurrency.text = "USD"
            }
            "CAD" -> {
                binding.destName.text = "Canadian dollar"
                binding.destCurrencySymbol.text = "CA$"
                binding.imgDestCurrency.setImageResource(R.drawable.ca_flag)
                binding.tvDestCurrency.text = "CAD"
            }
            "EUR" -> {
                binding.destName.text = "Euro"
                binding.destCurrencySymbol.text = "€"
                binding.imgDestCurrency.setImageResource(R.drawable.eur_flag)
                binding.tvDestCurrency.text = "EUR"
            }

            "HKD" -> {
                binding.destName.text = "Hong Kong dollar"
                binding.destCurrencySymbol.text = "“元”"
                binding.imgDestCurrency.setImageResource(R.drawable.hk_flag)
                binding.tvDestCurrency.text = "HKD"
            }

            "ISK" -> {
                binding.destName.text = "Icelandic króna"
                binding.destCurrencySymbol.text = "Íkr"
                binding.imgDestCurrency.setImageResource(R.drawable.ic_flag)
                binding.tvDestCurrency.text = "ISK"

            }

            "PHP" -> {
                binding.destName.text = "Philippine peso"
                binding.destCurrencySymbol.text = "₱"
                binding.imgDestCurrency.setImageResource(R.drawable.flp_flag)
                binding.tvDestCurrency.text = "PHP"
            }

            "DKK" -> {
                binding.destName.text = "Danish krone"
                binding.destCurrencySymbol.text = "kr"
                binding.imgDestCurrency.setImageResource(R.drawable.da_flag)
                binding.tvDestCurrency.text = "DKK"
            }

            "HUF" -> {
                binding.destName.text = "Hungarian forint"
                binding.destCurrencySymbol.text = "ft"
                binding.imgDestCurrency.setImageResource(R.drawable.hu_flag)
                binding.tvDestCurrency.text = "HUF"
            }

            "CZK" -> {
                binding.destName.text = "Czech koruna"
                binding.destCurrencySymbol.text = "Kč"
                binding.imgDestCurrency.setImageResource(R.drawable.cz_flag)
                binding.tvDestCurrency.text = "CZK"
            }

            "AUD" -> {
                binding.destName.text = "Australian dollar"
                binding.destCurrencySymbol.text = "A$"
                binding.imgDestCurrency.setImageResource(R.drawable.as_flag)
                binding.tvDestCurrency.text = "AUD"
            }

            "RON" -> {
                binding.destName.text = "Romanian leu"
                binding.destCurrencySymbol.text = "lei"
                binding.imgDestCurrency.setImageResource(R.drawable.ro_flag)
                binding.tvDestCurrency.text = "RON"
            }

            "SEK" -> {
                binding.destName.text = "Swedish krona"
                binding.destCurrencySymbol.text = "kr"
                binding.imgDestCurrency.setImageResource(R.drawable.sw_flag)
                binding.tvDestCurrency.text = "SEK"
            }

            "IDR" -> {
                binding.destName.text = "Indonesian rupiah"
                binding.destCurrencySymbol.text = "Rp"
                binding.imgDestCurrency.setImageResource(R.drawable.id_flag)
                binding.tvDestCurrency.text = "IDR"
            }

            "INR" -> {
                binding.destName.text = "Indian rupee"
                binding.destCurrencySymbol.text = "₹"
                binding.imgDestCurrency.setImageResource(R.drawable.in_flag)
                binding.tvDestCurrency.text = "INR"
            }

            "BRL" -> {
                binding.destName.text = "Brazilian real"
                binding.destCurrencySymbol.text = "R$"
                binding.imgDestCurrency.setImageResource(R.drawable.br_flag)
                binding.tvDestCurrency.text = "BRL"
            }

            "RUB" -> {
                binding.destName.text = "Russian ruble"
                binding.destCurrencySymbol.text = "₽"
                binding.imgDestCurrency.setImageResource(R.drawable.rs_flag)
                binding.tvDestCurrency.text = "RUB"
            }

            "HRK" -> {
                binding.destName.text = "Croatian kuna"
                binding.destCurrencySymbol.text = "kn"
                binding.imgDestCurrency.setImageResource(R.drawable.rs_flag)
                binding.tvDestCurrency.text = "HRK"
            }

            "JPY" -> {
                binding.destName.text = "Japanese yen"
                binding.destCurrencySymbol.text = "¥"
                binding.imgDestCurrency.setImageResource(R.drawable.jp_flag)
                binding.tvDestCurrency.text = "JPY"
            }

            "THB" -> {
                binding.destName.text = "Thai baht"
                binding.destCurrencySymbol.text = "฿"
                binding.imgDestCurrency.setImageResource(R.drawable.th_flag)
                binding.tvDestCurrency.text = "THB"
            }

            "CHF" -> {
                binding.destName.text = "Swiss franc"
                binding.destCurrencySymbol.text = "CHF"
                binding.imgDestCurrency.setImageResource(R.drawable.sz_flag)
                binding.tvDestCurrency.text = "CHF"
            }

            "SGD" -> {
                binding.destName.text = "Singapore dollar"
                binding.destCurrencySymbol.text = "S$"
                binding.imgDestCurrency.setImageResource(R.drawable.sn_flag)
                binding.tvDestCurrency.text = "SGD"
            }

            "PLN" -> {
                binding.destName.text = "Polish zloty"
                binding.destCurrencySymbol.text = "zł"
                binding.imgDestCurrency.setImageResource(R.drawable.pl_flag)
                binding.tvDestCurrency.text = "PLN"
            }


            "BGN" -> {
                binding.destName.text = "Bulgarian lev"
                binding.destCurrencySymbol.text = "лв"
                binding.imgDestCurrency.setImageResource(R.drawable.bu_flag)
                binding.tvDestCurrency.text = "BGN"
            }
            "CNY" -> {
                binding.destName.text = "Chinese yuan"
                binding.destCurrencySymbol.text = "¥ /元"
                binding.imgDestCurrency.setImageResource(R.drawable.ch_flag)
                binding.tvDestCurrency.text = "CNY"
            }
            "NOK" -> {
                binding.destName.text = "Norwegian krone"
                binding.destCurrencySymbol.text = "kr"
                binding.imgDestCurrency.setImageResource(R.drawable.no_flag)
                binding.tvDestCurrency.text = "NOK"
            }
            "NZD" -> {
                binding.destName.text = "New Zealand dollar"
                binding.destCurrencySymbol.text = "$"
                binding.imgDestCurrency.setImageResource(R.drawable.nz_flag)
                binding.tvDestCurrency.text = "NZD"
            }
            "ZAR" -> {
                binding.destName.text = "South african rand"
                binding.destCurrencySymbol.text = "R"
                binding.imgDestCurrency.setImageResource(R.drawable.sf_flag)
                binding.tvDestCurrency.text = "ZAR"
            }
            "MXN" -> {
                binding.destName.text = "Mexican peso"
                binding.destCurrencySymbol.text = "$"
                binding.imgDestCurrency.setImageResource(R.drawable.mx_flag)
                binding.tvDestCurrency.text = "MXN"
            }

            "GBP" -> {
                binding.destName.text = "Pounds sterling"
                binding.destCurrencySymbol.text = "£"
                binding.imgDestCurrency.setImageResource(R.drawable.uk_flag)
                binding.tvDestCurrency.text = "GBP"
            }
            "KRW" -> {
                binding.destName.text = "South Korean won"
                binding.destCurrencySymbol.text = "₩"
                binding.imgDestCurrency.setImageResource(R.drawable.ks_flag)
                binding.tvDestCurrency.text = "KRW"
            }
            "MYR" -> {
                binding.destName.text = "Malaysian ringgit"
                binding.destCurrencySymbol.text = "RM"
                binding.imgDestCurrency.setImageResource(R.drawable.my_flag)
                binding.tvDestCurrency.text = "MYR"
            }
        }
    }

    private fun getExchangeRates() {

        networkChecker.performIfConnectedToInternet {
            remoteApi.getExchangeRates { result ->
                if (result is Success)
                onRatesReceived(result.data)
            }
        }
    }


    private fun onRatesReceived(exchangeResponse: GetExchangeResponse) {
        fillExchangeArr(exchangeResponse)


    }

    private fun fillExchangeArr(exchangeResponse: GetExchangeResponse) {
        exchangeArr.apply {
            add(exchangeResponse.uSD.toDouble())
            add(exchangeResponse.cAD)
            add(exchangeResponse.eUR)
            add(exchangeResponse.hKD)
            add(exchangeResponse.iSK)
            add(exchangeResponse.pHP)
            add(exchangeResponse.dKK)
            add(exchangeResponse.hUF)
            add(exchangeResponse.cZK)
            add(exchangeResponse.aUD)
            add(exchangeResponse.rON)
            add(exchangeResponse.sEK)
            add(exchangeResponse.iDR)
            add(exchangeResponse.iNR)
            add(exchangeResponse.bRL)
            add(exchangeResponse.rUB)
            add(exchangeResponse.hRK)
            add(exchangeResponse.jPY)
            add(exchangeResponse.tHB)
            add(exchangeResponse.cHF)
            add(exchangeResponse.sGD)
            add(exchangeResponse.pLN)
            add(exchangeResponse.bGN)
            add(exchangeResponse.cNY)
            add(exchangeResponse.nOK)
            add(exchangeResponse.nZD)
            add(exchangeResponse.zAR)
            add(exchangeResponse.mXN)
            add(exchangeResponse.gBP)
            add(exchangeResponse.kRW)
            add(exchangeResponse.mYR)
        }
    }


}
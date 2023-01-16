package com.mguibtech.currencyconverter

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonObject
import com.mguibtech.currencyconverter.api.Endpoint
import com.mguibtech.currencyconverter.util.NetworksUtils
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {

    private lateinit var spFrom : Spinner
    private lateinit var spTo : Spinner
    private lateinit var btConvert : Button
    private lateinit var tvResult : TextView
    private lateinit var etValueFrom : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spFrom = findViewById(R.id.spFrom)
        spTo = findViewById(R.id.spTo)
        btConvert = findViewById(R.id.btConvert)
        tvResult = findViewById(R.id.tvResult)
        etValueFrom = findViewById(R.id.etValueFrom)


        getCurrencies()

        btConvert.setOnClickListener {
            convertMoney()
        }
    }

    fun convertMoney(){

        if(etValueFrom.text.toString() == "" ){
//            return tvResult.setText("Valor não pode ser vazio")
            println(etValueFrom.text.toString())
            Toast.makeText(this, "O valor não pode ser vazio!", Toast.LENGTH_LONG).show()
        }else{
            val retrofitCLient = NetworksUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
            val endpoint = retrofitCLient.create(Endpoint::class.java)

            endpoint.getCurrencyRate(spFrom.selectedItem.toString(), spTo.selectedItem.toString()).enqueue(object :
                retrofit2.Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var data = response.body()?.entrySet()?.find { it.key == spTo.selectedItem.toString() }
                    val rate : Double = data?.value.toString().toDouble()
                    val convertion = etValueFrom.text.toString().toDouble() * rate

                    tvResult.setText(convertion.toString())

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    println("NÃO FOI")
                }
            })
        }
    }


    fun getCurrencies(){
        val retrofitCLient = NetworksUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitCLient.create(Endpoint::class.java)

        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                val posBRL = data.indexOf("brl")
                var posUSD = data.indexOf("usd")

                val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                spFrom.adapter = adapter
                spTo.adapter = adapter

                spFrom.setSelection(posBRL)
                spTo.setSelection(posUSD)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("NÃO FOI")
            }

        })
    }
}
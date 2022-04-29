package ru.music.radiostationvedaradio.view.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

import ru.music.radiostationvedaradio.R
import ru.music.radiostationvedaradio.retrofit.data_main_tcitata.EncodingInterceptor
import ru.music.radiostationvedaradio.retrofit.data_main_tcitata.HareKrishnaService

class MainFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_main, container, false)
        view0.apply {

        }
        return view0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread{
            view.tv_tcitata_dnya.loadNewTcitata()
        }.start()


    }

    private fun TextView.loadNewTcitata() {
        val stringResult: String = getString(R.string.default_text_bhagavadgita)
        val randomIntString = "${(1..657).random()}"
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(EncodingInterceptor()).build()
            )
            .baseUrl("http://hare108.ru")
            .build()
        val hareKrishnaService = retrofit.create(HareKrishnaService::class.java)
        val response: Call<String> =
            hareKrishnaService.getNewTcitata("http://hare108.ru/bhagavad-gita/$randomIntString.htm")
        response.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val byte = response.body()
                    val regexpLine =
                        "\".[^a-z]{50,1500}\"".trimMargin() // значения в <> исключая >/ со значением +    - один или более символов
                    val found = regexpLine.toRegex().find(byte.toString())
                    this@loadNewTcitata.text = found?.value ?: stringResult
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                this@loadNewTcitata.text = stringResult
            }
        })
    }


}

package ru.music.radiostationvedaradio.retrofit.data_main_tcitata

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.ResponseBody

//okHttp client : Interceptor с методом преобразующим header с правильным указанием header (почему - то сайт нечитаемые данные отдавал?)
class EncodingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val response: okhttp3.Response = chain.proceed(chain.request())
        val responseHeader: String = response.header("Content-Type") ?: "text/html; charset=windows-1251"
        val oldMediaType = MediaType.parse(responseHeader)
        val newResponseHeader = oldMediaType!!.type() + "/" + oldMediaType.subtype() + "; charset=windows-1251"
        val newMediaType = MediaType.parse(newResponseHeader)
        val newResponseBody: ResponseBody = ResponseBody.create(newMediaType, response.body()!!.bytes())
        return response.newBuilder()
            .removeHeader("Content-Type")
            .addHeader("Content-Type", newMediaType.toString())
            .body(newResponseBody)
            .build()
    }
}
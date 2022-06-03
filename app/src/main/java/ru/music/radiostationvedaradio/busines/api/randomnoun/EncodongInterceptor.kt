package ru.music.radiostationvedaradio.busines.api.randomnoun

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody

//okHttp client : Interceptor с методом преобразующим header с правильным указанием header
// (почему - то сайт нечитаемые данные отдавал?) В результате Charset по умолчанию был Utf 8 и кирилица не читалась
class EncodingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val response: okhttp3.Response = chain.proceed(chain.request())
        val responseHeader: String =
            response.header("Content-Type") ?: "text/html; charset=windows-1251"
        val oldMediaType = responseHeader.toMediaTypeOrNull()
        val newResponseHeader =
            oldMediaType?.type + "/" + oldMediaType?.subtype + "; charset=windows-1251"
        val newMediaType = newResponseHeader.toMediaTypeOrNull()
        val newResponseBody: ResponseBody =
            ResponseBody.create(newMediaType, response.body!!.bytes())
        return response.newBuilder()
            .removeHeader("Content-Type")
            .addHeader("Content-Type", newMediaType.toString())
            .body(newResponseBody)
            .build()
    }
}
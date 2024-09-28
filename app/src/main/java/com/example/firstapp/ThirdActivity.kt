package com.example.firstapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class ThirdActivity : AppCompatActivity() {

    private val SERVER_URL = "http://93.93.207.160:5000"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        fetchImagesFromServer()
    }

    private fun fetchImagesFromServer() {
        println("ПРОВЕРКА5")

        val client = OkHttpClient()
        println("ПРОВЕРКА2")

        val request = Request.Builder()
            .url(SERVER_URL)
            .get()
            .build()

        println("ПРОВЕРКА4")
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace() // Обработка ошибки
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                println("ПРОВЕРКА1")

                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    try {
                        val jsonArray = JSONArray(responseData)
                        println("ПРОВЕРКА")
                        // Обработка данных в главном потоке
                        runOnUiThread {
                            displayImages(jsonArray)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun displayImages(jsonArray: JSONArray) {
        val linearLayout = findViewById<LinearLayout>(R.id.gallery_layout)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val base64Image = jsonObject.getString("image")
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val imageView = ImageView(this)
            imageView.setImageBitmap(bitmap)
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            )

            linearLayout.addView(imageView)
        }
    }
}

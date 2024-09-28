package com.example.firstapp
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response


class SecondActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private val SERVER_URL = "http://93.93.207.160:5000"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        imageView = findViewById(R.id.imageView)
        val buttonChoose = findViewById<Button>(R.id.buttonChoose)
        val buttonUpload = findViewById<Button>(R.id.buttonUpload)
        val buttonOpenGallery = findViewById<Button>(R.id.button_open_gallery_activity)

        buttonChoose.setOnClickListener {
            openGallery()
        }

        buttonUpload.setOnClickListener {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            uploadImageAsBase64(bitmap)
        }

        buttonOpenGallery.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data
            try {
                // Получение изображения из галереи
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageAsBase64(bitmap: Bitmap) {
        val client = OkHttpClient()

        val bytesPerPixel = when (bitmap.config) {
            Bitmap.Config.ARGB_8888 -> 4 // 4 байта на пиксель
            Bitmap.Config.RGB_565 -> 2  // 2 байта на пиксель
            Bitmap.Config.ALPHA_8 -> 1  // 1 байт на пиксель
            Bitmap.Config.ARGB_4444 -> 2 // 2 байта на пиксель (deprecated)
            else -> 4 // По умолчанию 4 байта на пиксель
        }

        val bitmapSizeInBytes = bitmap.width * bitmap.height * bytesPerPixel

        Log.d("BitmapSize", "Размер массива Bitmap: $bitmapSizeInBytes байт")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
        println(encodedImage)

        // Создаем тело запроса с типом "application/x-www-form-urlencoded"
        val requestBody = FormBody.Builder()
            .add("aa", encodedImage)
            .add("bb", "test")
            .build()

        // Создаем сам запрос
        val request = Request.Builder()
            .url("http://93.93.207.160:5000")
            .post(requestBody)
            .build()

        // Выполняем запрос
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace() // Обработка ошибки
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Успешный ответ от сервера
                    val responseData = response.body?.string()
                    println("Response: $responseData")
                } else {
                    // Обработка неуспешного ответа
                    println("Request failed. Response code: ${response.code}")
                }
            }
        })

    }
}
package com.example.fetchcodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fetchcodingexercise.data.AmazonData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Type


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainView()
        }
    }

}

@Composable
fun MainView() {

    //Set the data to a mutable variable, so when the json data is retrieved, the string value can be changed on the UI
    //Without "remember", the state would be initialized to the default value every time the composable is recomposed
    var amazonData by remember { mutableStateOf<List<AmazonData>?>(null) }

    //Retrieve the string data using a coroutine when Composable is first launched
    LaunchedEffect(Unit) {
        //Fetch data using the IO thread, to avoid blocking the main thread
        CoroutineScope(Dispatchers.IO).launch {
            amazonData = retrieveAmazonJsonData("https://fetch-hiring.s3.amazonaws.com/hiring.json") //Get the amazon data from a custom json retrieval function
        }
    }

    AmazonListView(amazonData) //Assign the list of AmazonData into a view
}

@Composable
fun AmazonListView(amazonDataList: List<AmazonData>?) {
    amazonDataList?.let { dataList ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(dataList) { unit ->
                Text(text = "listId: ${unit.listId}, name: ${unit.name}, id: ${unit.id}")
            }
        }
    } ?: run {
        Text(text = "An error has occurred")
    }
}

fun retrieveAmazonJsonData(link: String) : List<AmazonData>? {
    val client = OkHttpClient() //Create client for HTTP requests
    val request = Request.Builder().url(link).build() //Build the request for the client

    try {
        val result = client.newCall(request).execute() //Use the OkHttpClient to request the Amazon URL, and put the result in the result variable
        val resultData = result.body?.string() //Take the body of the result as a string
        val type: Type = object : TypeToken<List<AmazonData?>?>() {}.type //Use TypeToken to specify that resultData will be a list of AmazonData
        return Gson().fromJson<List<AmazonData>>(resultData, type) //Use Gson to convert the string resultData into a json list of AmazonData
    } catch (e: Exception) {
        println("EXCEPTION $e")
        return null //If there is an error with the call to the URL, or deserialization, return null
    }
}
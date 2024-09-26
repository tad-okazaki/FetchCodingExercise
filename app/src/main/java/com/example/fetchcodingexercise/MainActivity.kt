package com.example.fetchcodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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

/*
MainView() represents the entire view
 */
@Composable
fun MainView() {

    //Set the data to a mutable variable, so when the json data is retrieved, the string value can be changed on the UI
    //Without "remember", the state would be initialized to the default value every time the composable is recomposed
    var amazonData by remember { mutableStateOf<List<AmazonData>?>(null) }

    //Retrieve the string data using a coroutine when Composable is first launched
    LaunchedEffect(Unit) {
        //Fetch data using the IO thread, to avoid blocking the main thread
        CoroutineScope(Dispatchers.IO).launch {
            val rawAmazonDataList = retrieveAmazonJsonData("https://fetch-hiring.s3.amazonaws.com/hiring.json") //Get the amazon data from a custom json retrieval function

            amazonData = sortList(rawAmazonDataList) //Sort the rawAmazonDataList and assign it to the amazonData for the view
        }
    }

    AmazonListView(amazonData) //Assign the list of AmazonData into a view
}

/*
AmazonListView represents the entire list
 */
@Composable
fun AmazonListView(amazonDataList: List<AmazonData>?) {
    amazonDataList?.let { dataList ->
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("listId", modifier = Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge) //listId header
                Text("name", modifier = Modifier.weight(2f), style = MaterialTheme.typography.headlineLarge) //name header
                Text("id", modifier = Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge) //id header
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(dataList) { unit -> //For each item in the List
                    AmazonItem(unit) //Add a row for the item
                }
            }
        }
    } ?: run {
        Text(text = "An error has occurred")
    }
}

/*
AmazonItem() creates one row for each AmazonData
 */
@Composable
fun AmazonItem(data: AmazonData) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("${data.listId}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge) //listId item text
        Text("${data.name}", modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyLarge) //name item text
        Text("${data.id}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge) //id item text
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

//sortList() will first remove all AmazonData entries that have null or blank names, then sort by listId, then sort by name
fun sortList(rawData: List<AmazonData>?) : List<AmazonData>? {
    //Remove all AmazonData names that are null or blank
    //Then sort by listId, then sort by name.
    //Name must have the prefix "Item " removed before int comparison
    return rawData?.filter { !it.name.isNullOrBlank() }?.sortedWith(compareBy({ it.listId }, { removeItemPrefix(it.name) }))
}

//removeItemPrefix() will remove the prefix "Item " so that the name's numbers can be compared as ints instead of strings
fun removeItemPrefix(name: String?) : Int {
    //Since all names we want to sort begin with "Item ", but need to be compared numerically, these names should be converted to ints for comparison
    //If the names aren't converted to ints, but instead compared as strings, then "Item 280" will come before "Item 29", which isn't correct.
    return name?.removePrefix("Item ")?.toIntOrNull() ?: 0
}



//groupByListId will group and sort by listId. Does not sort by name.
//Not currently used anymore, but is being kept for reference.
fun groupByListId(rawData: List<AmazonData>?) : List<AmazonData>? {

    return rawData?.groupBy{ it.listId }?.toList()?.sortedBy { (key, _) -> key }?.flatMap { (_, list) -> list }
    /*
    .groupBy{ it.listId }? will group the raw data by listId, and place into a map of format: Map<Int, List<AmazonData>>?
     The map's Int represents the group key, and the List<AmazonData> represents the data within the group.

     toList()? will group the data into a list of pairs to sort, with this format: List< Pair<Int, List<AmazonData>>>?

     .sortedBy { (key, _) -> key }? will sort the pairs into a list, using the group number as a key, with format:  List<Pair<Int, List<AmazonData>>>?

     .flatMap { (_, list) -> list } will remove the group key, and just leave the list of Amazon data: List<AmazonData>
     */
}
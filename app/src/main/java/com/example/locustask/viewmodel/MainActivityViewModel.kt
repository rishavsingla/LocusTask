package com.example.locustask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.locustask.models.DataMap
import com.example.locustask.models.LocusResponse
import com.example.locustask.models.Options
import com.google.gson.Gson
import com.google.gson.JsonArray

class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    private val response = MutableLiveData<List<LocusResponse>>()
    fun getResponseData(): LiveData<List<LocusResponse>> = response

    fun readJsonResponseFromFile() {
        val jsonString = getJsonFromAssets() ?: return
        val jsonArray = Gson().fromJson(jsonString, JsonArray::class.java) ?: JsonArray()
        val dataList = mutableListOf<LocusResponse>()
        for (jsonObject in jsonArray) {
            if (!jsonObject.isJsonObject) continue
            val type = jsonObject.asJsonObject.get("type").asString
            val id = jsonObject.asJsonObject.get("id").asString
            val title = jsonObject.asJsonObject.get("title").asString
            val dataMapJsonObject = jsonObject.asJsonObject.get("dataMap").asJsonObject
            val dataMap =
                Gson().fromJson(dataMapJsonObject, DataMap::class.java) ?: DataMap(emptyList())
            val optionsList = mutableListOf<Options>()
            for (option in dataMap.options) {
                optionsList.add(Options(option, false))
            }
            dataMap.list = optionsList
            dataList.add(LocusResponse(dataMap, id, type, title, ""))
        }
        response.value = dataList
    }

    private fun getJsonFromAssets(): String? {
        return try {
            val inputStream = app.assets.open("locus_response.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }

    fun setFilePath(position: Int, filePath: String) {
        val list = response.value?.toList() ?: emptyList()
        list[position].imagePath = filePath
        response.value = list.toList()
    }

    fun removePhoto(position: Int) {
        val list = response.value ?: emptyList()
        list[position].imagePath = ""
        response.value = list.toList()
    }

    fun setOptionsChoice(checkedId: Int, itemPosition: Int) {
        val list = response.value ?: emptyList()
        val optionsList = list[itemPosition].dataMap.list
        for (option in optionsList) {
            option.isChecked = false
        }
        optionsList[checkedId].isChecked = true
        response.value = list.toList()
    }
}
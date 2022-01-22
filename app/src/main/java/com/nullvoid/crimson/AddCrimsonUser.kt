package com.nullvoid.crimson

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nullvoid.crimson.adapters.ContactChooseAdapter
import com.nullvoid.crimson.databinding.ActivityAddCrimsonUserBinding

class AddCrimsonUser : AppCompatActivity() {

    private lateinit var binding: ActivityAddCrimsonUserBinding
    private lateinit var adapter: ContactChooseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCrimsonUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        init()
    }

    private fun init() {
        adapter = ContactChooseAdapter(this, arrayListOf())
        binding.listView.adapter = adapter
        binding.listView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            adapter.setData(getContactNumbers())
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 12)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            adapter.setData(getContactNumbers())
        }
    }

    private fun getContactNumbers(): ArrayList<Pair<String, String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = application.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            phoneCursor.close()
        }
        val list = arrayListOf<Pair<String, String>>()
        contactsNumberMap.entries.forEach {
            it.component2().forEach { x ->
                list.add(Pair(it.component1(), x))
            }
        }

        val ans = arrayListOf<Pair<String, String>>()
        for (x in list) {
            if (x.second[0] == '+') {
                ans.add(Pair(x.first, x.second.substring(3)))
            } else {
                ans.add(Pair(x.first, x.second))
            }
        }
        return ArrayList(ans.distinct())
    }
}
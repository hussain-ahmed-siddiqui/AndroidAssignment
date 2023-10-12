package com.ubit.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.ubit.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun getContacts(view: View) {

        var intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        resultLauncher.launch(intent)

    }




    @SuppressLint("Range")
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        grantUriPermission("com.ubit.myapplication",ContactsContract.Contacts.CONTENT_URI,
//            Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (result.resultCode == RESULT_OK) {
            val data = result.data

            val resolver = contentResolver
            val contentUri=data?.data!!
            val projection= arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            )

            var name=""
            var id=""
            val selectionArgs = arrayOf("1")
            try {
//
//                Toast.makeText(this,"Something2",Toast.LENGTH_SHORT).show()
                val cursor=resolver.query(contentUri,projection,"${ContactsContract.Contacts.HAS_PHONE_NUMBER} = ?",selectionArgs,ContactsContract.Contacts.DISPLAY_NAME)

                if (cursor != null) {
                    while(cursor.moveToNext()){
                        name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    }
                    cursor.close()
                    Log.e("QUERY_SUCCESS", "Name and id: $name")
                }
                val phoneProjection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE
                )

                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneProjection,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                phoneCursor?.use {
                    while (it.moveToNext()) {
                        val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val phoneType = it.getInt(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))

                        val phoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, phoneType, "Unknown").toString()
                        Toast.makeText(this, "Name: $name, Phone: $phoneNumber",Toast.LENGTH_SHORT).show()

                        Log.e("CONTACT_DETAILS: ", "$name $phoneNumber")
                    }
                }
            } catch (e:Exception ){

//                    Toast.makeText(this,""+e.printStackTrace(),Toast.LENGTH_LONG).show()
                Log.e("QUERY_ERROR", "Error querying content provider: ${e.message}", e)

                }
                    //do something


            }
        }
    }


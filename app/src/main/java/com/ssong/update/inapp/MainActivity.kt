package com.ssong.update.inapp

import android.content.Intent
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_flexible.setOnClickListener {
            val intent = Intent(this, FlexibleActivity::class.java)
            startActivity(intent)
        }

        btn_immediate.setOnClickListener {
            val intent = Intent(this, ImmediateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val info: PackageInfo = packageManager.getPackageInfo(this.packageName, 0)
        val version = info.versionName
        tv_version.text = "현재 버전 : $version"
    }
}

package com.ssong.update.inapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


class ImmediateActivity : AppCompatActivity() {

    private val reqUpdateCode = 100

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_immediate)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        // 강제업데이트 (업데이트 UI 종료하여도 계속 노출) 시에는 주석처리
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // 업데이트 요청.
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    AppUpdateType.IMMEDIATE,
                    this,
                    reqUpdateCode
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 업데이트 다운로드 완료 후 설치하지 않았을 경우 설치 유도
        // 강제업데이트 (업데이트 UI 종료하여도 계속 노출) 시에는 주석처리
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            // 업데이트 다운로드&설치 진행 중 onBackPressed 동작 시 다운로드&설치 페이지를 다시 띄워줌.
            // 업데이트 다운로드 완료 후 자동적(즉각적)으로 설치됨.
            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    AppUpdateType.IMMEDIATE,
                    this,
                    reqUpdateCode
                )
            }
        }

        // 강제업데이트 (업데이트 UI 종료하여도 계속 노출)
//        appUpdateManager.appUpdateInfo.addOnSuccessListener {
//            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                && it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                // 업데이트 요청.
//                appUpdateManager.startUpdateFlowForResult(
//                    it,
//                    AppUpdateType.IMMEDIATE,
//                    this,
//                    reqUpdateCode
//                )
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == reqUpdateCode) {
            if (resultCode != RESULT_OK) {
                // 업데이트를 취소 한 경우
                // 추후에 다시 업데이트가 가능함.
                Toast.makeText(this, "업데이트가 취소 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
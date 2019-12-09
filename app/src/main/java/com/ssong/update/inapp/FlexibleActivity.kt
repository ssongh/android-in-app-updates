package com.ssong.update.inapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability



class FlexibleActivity : AppCompatActivity(), InstallStateUpdatedListener {

    private val reqUpdateCode = 100

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_flexible)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(this)

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // 업데이트 요청.
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    AppUpdateType.FLEXIBLE,
                    this,
                    reqUpdateCode
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 업데이트 다운로드 완료 후 설치하지 않았을 경우 설치 유도
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
                // 업데이트 다운로드가 완료됨, 다운로드만 완료된 것으로 설치가 필요함.
                // 유저에게 다운로드가 완료됨을 알려준 후 입력을 받아 설치를 진행함.
                // 다운로드 후 설치는 즉각적 또는 추후에 선택하여 진행할 수 있다.
                if (it.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        appUpdateManager.unregisterListener(this)
    }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(findViewById(R.id.flexible_area), "새로운 앱 버전 다운로드 완료, 설치를 진행해주세요.", Snackbar.LENGTH_INDEFINITE)
            .setAction("설치") {
                // 다운로드 완료된 업데이트를 설치함.
                appUpdateManager.completeUpdate()
            }

        snackBar.show()
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

    override fun onStateUpdate(state: InstallState?) {
        if (state?.installStatus() == InstallStatus.DOWNLOADED) {
            // 업데이트 다운로드가 완료됨, 다운로드만 완료된 것으로 설치가 필요함.
            // 유저에게 다운로드를 알려준 후 입력을 받아 설치를 진행함.
            popupSnackBarForCompleteUpdate()
        }
    }
}
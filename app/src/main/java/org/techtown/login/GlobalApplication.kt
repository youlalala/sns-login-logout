package org.techtown.login

import android.app.Application
import android.app.NativeActivity
import com.kakao.sdk.common.KakaoSdk


class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "3419f8d30081a35920ea94392cbf9a81")
    }
}
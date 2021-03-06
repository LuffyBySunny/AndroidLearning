package com.sunny.tinkertest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Choreographer
import androidx.appcompat.app.AppCompatActivity
import com.sunny.tinkertest.activity.FragmentTestActivity
import com.sunny.tinkertest.util.CrashHandler
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainActivityContact.View {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CrashHandler.instance.init(this)
        goAnimator.setOnClickListener {
            Log.d("openNo", isOpen2(this).toString())
            openN(this)
        }

        val choreographer = Choreographer.getInstance()

        val method = Choreographer::class.java.getDeclaredMethod("postCallback",Int::class.java,Runnable::class.java,Object::class.java)
        method.isAccessible = true
        val runnable = Runnable {
            Log.d("syncScreen", "屏幕刷新了")
            //method.invoke(choreographer, 2,this,null)
        }
        method.invoke(choreographer, 2,runnable,null)



        fragmentTest.setOnClickListener {
            val intent = Intent(this, FragmentTestActivity::class.java)
            startActivity(intent)

        }




    }

    /**
     * 获取设备信息（目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo、三星都可以）
     *
     * @return
     */
    private fun getDeviceInfo(): String {
        val brand = Build.BRAND
        if (TextUtils.isEmpty(brand)) return "navigationbar_is_min"

        return if (brand.equals("HUAWEI", ignoreCase = true) || "HONOR" == brand) {
            "navigationbar_is_min"
        } else if (brand.equals("XIAOMI", ignoreCase = true)) {
            "force_fsg_nav_bar"
        } else if (brand.equals("VIVO", ignoreCase = true)) {
            "navigation_gesture_on"
        } else if (brand.equals("OPPO", ignoreCase = true)) {
            "navigation_gesture_on"
        } else if (brand.equals("samsung", ignoreCase = true)) {
            "navigationbar_hide_bar_enabled"
        } else {
            "navigationbar_is_min"
        }
    }

    /**
     * 判断设备是否存在NavigationBar
     *
     * @return true 存在, false 不存在
     */
    fun deviceHasNavigationBar(): Boolean {
        var haveNav = false
        try {
            //1.通过WindowManagerGlobal获取windowManagerService
            // 反射方法：IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
            val windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal")
            val getWmServiceMethod = windowManagerGlobalClass.getDeclaredMethod("getWindowManagerService")
            getWmServiceMethod.isAccessible = true
            //getWindowManagerService是静态方法，所以invoke null
            val iWindowManager = getWmServiceMethod.invoke(null)

            //2.获取windowMangerService的hasNavigationBar方法返回值
            // 反射方法：haveNav = windowManagerService.hasNavigationBar();
            val iWindowManagerClass = iWindowManager.javaClass
            val hasNavBarMethod = iWindowManagerClass.getDeclaredMethod("hasNavigationBar")
            hasNavBarMethod.isAccessible = true
            haveNav = hasNavBarMethod.invoke(iWindowManager) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return haveNav
    }

    private fun navigationGestureEnabled(context: Context): Boolean {
        val `val` = Settings.Global.getInt(context.getContentResolver(), getDeviceInfo(), 0)
        return `val` != 0
    }

    fun hasNavigationBar(context: Context): Boolean {
        //navigationGestureEnabled()从设置中取不到值的话，返回false，因此也不会影响在其他手机上的判断
        return deviceHasNavigationBar() && !navigationGestureEnabled(context)
    }

    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        if (hasNavigationBar(context)) {
            val res = context.resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    fun getForceNavigationBarHeight(context: Context):Int {
        val res = context.resources
        var result = 0
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getStatusBarHeight(context: Context?): Int {
        var statusBarHeight = 0
        if (context != null) {
            try {
                val c = Class.forName("com.android.internal.R\$dimen")
                val o = c.newInstance()
                val field = c.getField("status_bar_height")
                val x = field.get(o) as Int
                statusBarHeight = context.resources.getDimensionPixelSize(x)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return statusBarHeight
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d("open", "打开了")
        } else {
            Log.d("open", "没打卡")
        }
    }
}

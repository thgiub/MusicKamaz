package ru.kamaz.musickamaz.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import ru.kamaz.music.ui.PermissionUtils
import ru.kamaz.musickamaz.R
import ru.kamaz.musickamaz.adapters.DepthPageTransformer
import ru.kamaz.musickamaz.adapters.ScreenSlidePagerAdapter
import ru.kamaz.musickamaz.databinding.ActivityMainBinding
import ru.kamaz.music.ui.PermissionUtils.Companion as PermissionUtils1

class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager2
    private val CODE_PERMISSION = 1337

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        viewPager = binding.pager
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(DepthPageTransformer())
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }


    private fun checkPermission() {
        when(PermissionUtils.isPermissionsGranted(this)) {


            false -> requestPermissions()
        }
    }
       fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PermissionUtils.permissions, CODE_PERMISSION)
    }
}
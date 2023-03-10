package com.example.cryptohub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.cryptohub.databinding.ActivityMarketBinding
import com.example.cryptohub.fragments.ConvertFragment
import com.example.cryptohub.fragments.HomeFragment
import com.example.cryptohub.fragments.NewsFragment

class MarketActivity : AppCompatActivity() {
    lateinit var binding: ActivityMarketBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBtmNav()

    }


    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameMainContainer, fragment)
            commit()
        }


    private fun initBtmNav() {

        setCurrentFragment(HomeFragment())
        binding.btmNav.selectedItemId = R.id.miHome

        binding.btmNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.miNews -> setCurrentFragment(NewsFragment())
                R.id.miHome -> setCurrentFragment(HomeFragment())
                R.id.miConvert -> setCurrentFragment(ConvertFragment())
            }
            true
        }
        binding.btmNav.setOnItemReselectedListener {}
    }
}
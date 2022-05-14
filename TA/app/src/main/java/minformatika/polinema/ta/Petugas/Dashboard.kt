package minformatika.polinema.ta.Petugas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.dashboard_kader.*
import kotlinx.android.synthetic.main.dashboard_kader.bottomNavigationView
import kotlinx.android.synthetic.main.dashboard_kader.frameLayout
import kotlinx.android.synthetic.main.dashboard_petugas.*
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref

class Dashboard : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var session: SharedPref

    lateinit var ft : FragmentTransaction
    lateinit var fragPetugas : FragmentDataPetugas
    lateinit var fragGrafik : FragmentGrafik
    lateinit var fragDesa : FragmentDesa
    lateinit var fragPeringkat  : peringkat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_petugas)

        session = SharedPref(applicationContext)

        bottomNavigation.setOnNavigationItemSelectedListener(this)
        fragPetugas = FragmentDataPetugas()
        fragGrafik = FragmentGrafik()
        fragDesa = FragmentDesa()
        fragPeringkat = peringkat()

        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout,fragGrafik).commit()
        frameLayout.visibility = View.VISIBLE


    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.itemRumah ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragPetugas).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.ItemGrafik ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragGrafik).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.itemDesa ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragDesa).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.itemPeringkat ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragPeringkat).commit()
                frameLayout.visibility = View.VISIBLE
            }
        }
        return true
    }
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Tekan lagi untuk keluar", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 4000)
    }
}

package minformatika.polinema.ta.Kader

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.dashboard_kader.*
import minformatika.polinema.ta.R
import minformatika.polinema.ta.SharedPref
import org.json.JSONArray


class Dashboard : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var session: SharedPref

    lateinit var ft : FragmentTransaction
    lateinit var fragRumah : FragmentRumah
    lateinit var fragProfil : FragmentProfil
    lateinit var fragGrafik : FragmentGrafik

    var pet_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_kader)

        session = SharedPref(applicationContext)

        pet_id = SharedPref.getInstance(this).user.id.toString()

        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        fragRumah = FragmentRumah()
        fragProfil = FragmentProfil()
        fragGrafik = FragmentGrafik()


        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout,fragGrafik).commit()
        frameLayout.visibility = View.VISIBLE


    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.itemRumah ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragRumah).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.ItemGrafik ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragGrafik).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.itemProfil ->{
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout,fragProfil).commit()
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

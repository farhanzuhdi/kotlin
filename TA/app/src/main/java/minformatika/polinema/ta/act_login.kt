package minformatika.polinema.ta

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.mod_belum_vef.view.*
import minformatika.polinema.ta.Kader.Dashboard
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.system.exitProcess

class act_login : AppCompatActivity() {

    lateinit var etEmail: EditText
    internal lateinit var etPassword: EditText
    val urlLogin = "http://192.168.43.37/myapi/login_api.php?apicall=login"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        supportActionBar?.hide()


        if (SharedPref.getInstance(this).isLoggedIn) {
            if(SharedPref.getInstance(this).user.level == "2") {
                finish()
                startActivity(Intent(this, minformatika.polinema.ta.Petugas.Dashboard::class.java))
            }else if(SharedPref.getInstance(this).user.level == "3") {
                finish()
                startActivity(Intent(this, Dashboard::class.java))
            }else {
            }
        }


        etEmail = findViewById(R.id.email)
        etPassword = findViewById(R.id.password)


        //calling the method userLogin() for login the user
        btn_login.setOnClickListener(View.OnClickListener {
            userLogin()
        })

        //if user presses on textview it call the activity RegisterActivity
        txt_register.setOnClickListener(View.OnClickListener {
            finish()
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        })
    }

    private fun userLogin() {
        //first getting the values
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        //validating inputs
        if (TextUtils.isEmpty(email)) {
            etEmail.error = "Please enter your username"
            etEmail.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.error = "Please enter your password"
            etPassword.requestFocus()
            return
        }

        //if everything is fine
        val stringRequest = object : StringRequest(Request.Method.POST, urlLogin,
            Response.Listener { response ->


                try {
                    //converting response to json object
                    val obj = JSONObject(response)
                    val mes = obj.getString("message")

                    if (mes!=""){
                        Toast.makeText(applicationContext, mes, Toast.LENGTH_SHORT).show()
                    }else{
                    }
                    //if no error in response
                    if (!obj.getBoolean("error")) {


                        //getting the user from the response
                        val userJson = obj.getJSONObject("user")

                        //creating a new user object
                        val user = User(
                            userJson.getInt("pet_id"),
                            userJson.getString("username"),
                            userJson.getString("email"),
                            userJson.getString("level")
                        )
                        //storing the user in shared preferences
                        SharedPref.getInstance(applicationContext).userLogin(user)
                        if(user.level.toString() == "2"){
                            finish()
                            startActivity(Intent(applicationContext, minformatika.polinema.ta.Petugas.Dashboard::class.java))
                        }else if(user.level.toString()=="3"){
                            finish()
                            startActivity(Intent(applicationContext, Dashboard::class.java))
                        }else if(user.level.toString()=="4"){
                            val dialView = LayoutInflater.from(this).inflate(R.layout.mod_belum_vef,null);
                            val builder = AlertDialog.Builder(this).setView(dialView)
                            val aDialog = builder.show()
                            dialView.vefx.setOnClickListener{
                                aDialog.dismiss()
                            }
                            dialView.button5.setOnClickListener {
                                aDialog.dismiss()
                            }
                    }

                    } else {
                        Toast.makeText(
                            applicationContext,
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = email
                params["password"] = password
                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}
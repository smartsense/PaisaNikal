package aepsapp.easypay.com.aepsandroid
import aepsapp.easypay.com.aepsandroid.common.Utils
import aepsapp.easypay.com.aepsandroid.mantradevice.model.Opts
import aepsapp.easypay.com.aepsandroid.mantradevice.model.PidData
import aepsapp.easypay.com.aepsandroid.mantradevice.model.PidOptions
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_mantra_scan.*
import org.json.JSONException
import org.json.JSONObject
import org.json.XML
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.StringWriter
import java.util.*

class MantraScanActivity : AppCompatActivity() {

    private var fingerCount = 0
    private var pidData: PidData? = null
    private var serializer: Serializer? = null
    private var positions: ArrayList<String>? = null
    private var wadh: String? = null
    private var pidOpt: String? = null

    companion object {
        const val TAG = "MantraScanActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantra_scan)

        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (!TextUtils.isEmpty(sharedText)) {
            try {
                val objData = JSONObject(sharedText)
                wadh = objData.optString("wadh")
                pidOpt = objData.optString("pidOpt")
                edtxAdharNo.text = objData.get("aadhar").toString()
            } catch (e: JSONException) {
                Log.e(TAG, "JSONException", e)
            }

        }

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        positions = ArrayList()
        serializer = Persister()

        mantra_btnCapture.setOnClickListener {
            try {
                val pidOption = if (!TextUtils.isEmpty(pidOpt)) pidOpt else getPIDOptions()
                if (pidOption != null) {
                    Log.e("PidOptions", pidOption)
                    val intent2 = Intent()
                    intent2.action = "in.gov.uidai.rdservice.fp.CAPTURE"
                    intent2.putExtra("PID_OPTIONS", pidOption)
                    startActivityForResult(intent2, 2)
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }

        }
    }


    private fun displayError(message: String) {
        Utils.showAlert(this@MantraScanActivity, message)
    }


    private fun setText(message: String) {
        this.runOnUiThread(java.lang.Runnable {
            Log.e(TAG, "data=$message")
            // txtOutput.setText(message)
            try {
                val objJson = XML.toJSONObject(message)
                val pidJson = objJson.optJSONObject("PidData")
                if (pidJson != null) {
                    val respJson = pidJson.optJSONObject("Resp")
                    if (respJson != null) {
                        val code = respJson.optInt("errCode")
                        if (code > 0) {
                            displayError(respJson.get("errInfo").toString())
                            return@Runnable
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            //checkXML(message);

            val intent = Intent()
            intent.putExtra("pidData", message)
            setResult(Activity.RESULT_OK, intent)
            finish()
        })


    }

    /*   private fun checkXML(input: String): List<*>? {
           var sr: StringReader? = null
           try {
               sr = StringReader(input)
               val parser = Xml.newPullParser()
               parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
               //parser.setInput(sr, null);
               parser.setInput(sr)
               parser.nextTag()
               return readFeed(parser)
           } catch (e: XmlPullParserException) {
               Log.e(TAG, "checkXML", e)
           } catch (e: IOException) {
               Log.e(TAG, "checkXML", e)
           } finally {
               if (sr != null)
                   sr.close()
           }

           return null
       }

       @Throws(XmlPullParserException::class, IOException::class)
       private fun readFeed(parser: XmlPullParser): List<*> {
           val entries = ArrayList()

           //  parser.require(XmlPullParser.START_TAG, "", "Resp");
           while (parser.next() != XmlPullParser.END_TAG) {
               if (parser.eventType != XmlPullParser.START_TAG) {
                   continue
               }
               val name = parser.name
               // Starts by looking for the entry tag
               if (name == "Resp") {
                   readEntry(parser)
               } else {
                   // skip(parser);
               }
           }
           return entries
       }

       @Throws(XmlPullParserException::class, IOException::class)
       private fun readEntry(parser: XmlPullParser) {
           //  parser.require(XmlPullParser.START_TAG, "", "Resp");

           while (parser.next() != XmlPullParser.END_TAG) {
               if (parser.eventType != XmlPullParser.START_TAG) {
                   // Log.v(TAG,"COUNT "+parser.getAttributeCount());
                   continue
               }
               Log.v(TAG, "COUNT " + parser.attributeCount)
               val name = parser.name
               /*if (name.equals("title")) {
                   title = readTitle(parser);
               } else if (name.equals("summary")) {
                   summary = readSummary(parser);
               } else if (name.equals("link")) {
                   link = readLink(parser);
               } else {
                   skip(parser);
               }*/
           }
           //return new Entry(title, summary, link);
       }*/


    private fun getPIDOptions(): String? {
        try {
            var posh = "UNKNOWN"
            if (positions?.size ?: 0 > 0) {
                posh = positions.toString().replace("[", "").replace("]", "").replace("[\\s+]".toRegex(), "")
            }
            val opts = Opts()
            opts.fCount = "1"
            opts.fType = "1"
            opts.iCount = "0"
            opts.iType = "0"
            opts.pCount = "0"
            opts.pType = "0"
            opts.format = "1"
            opts.pidVer = "2.0"
            opts.timeout = "10000"

            // get aadhar XML data
            if (!TextUtils.isEmpty(wadh)) {
                opts.wadh = wadh
                opts.format = "0"
            } else
                opts.format = "1"// transaction protobuf format

            opts.posh = posh
            opts.env = "P"

            val pidOptions = PidOptions()
            pidOptions.ver = "1.0"
            pidOptions.Opts = opts

            val serializer = Persister()
            val writer = StringWriter()
            serializer.write(pidOptions, writer)
            return writer.toString()
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

            2 -> if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        val result = data.getStringExtra("PID_DATA")
                        if (result != null) {
                            pidData = serializer!!.read(PidData::class.java, result)
                            setText(result)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Error", "Error while deserialze pid data", e)
                }

            }
        }
    }

}

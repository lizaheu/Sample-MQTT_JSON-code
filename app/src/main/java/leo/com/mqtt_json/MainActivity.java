package leo.com.mqtt_json;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    EditText editTextName,editTextYear,editTextPassword;
    RadioButton radMale,radFemale;
    Spinner spinner;
    TextView txtViewEncodeResult;

    //======================================== Note =========================================
    //In this demo project, I only set the data into object class for further use and no perform any get method in this class.
    //Student can use get method when want to use it.
    Student studentDetails = new Student(); //Set data for futher use, in this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale = (RadioButton) findViewById(R.id.radFemale);
        spinner = (Spinner) findViewById(R.id.spinnerProgramme);
        editTextYear = (EditText) findViewById(R.id.editTextYear);
        editTextPassword = (EditText) findViewById(R.id.editTextPass);
        txtViewEncodeResult = (TextView) findViewById(R.id.txtViewEncodeResult);

    }

    public void btnDecode(View v) {
        //                 Prepare message publish to MQTT Server
        //===============================================================================
        // 303030303031 = Login Action
        // 303030303030303030303030303030303030303030303030 = Researve
        // 4c6565205761682050656e67 = 4c6565205761682050656e67 Username
        // 6c656f313233 = 6c656f313233 Password
        String sendSubscibeData = "{\"command\": \"303030303031\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                "\"username\": \"4c6565205761682050656e67\", \"password\": \"6c656f313233\"}";

        // Alert show what data publish to server
        AlertDialog.Builder publishBuilder = new AlertDialog.Builder(this);
        publishBuilder.setMessage("Data publish: " + sendSubscibeData)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog publishAlert = publishBuilder.create();
        publishAlert.show();

        //           Perform publish and receive publish from MQTT Server
        //================================================================================

        // Here to perform publish data to MQTT server and receive data publish
        // from MQTT server (Code by yourself)

        //=================================================================================


        //             Receive data publish from MQTT Server and split it
        //=================================================================================
        // 303030303035 = Respond from server
        // 303030303030303030303030303030303030303030303030 = Reserve
        // 4c6565205761682050656e67 = 4c6565205761682050656e67 (Username)
        //
        // 303032 = Female (Gender)
        //
        // 525344 = RSD (Programme Code)
        //
        // 303033 = 3 (Year of study)
        //
        // 6c656f313233 = 6c656f313233 (Password)

        String receiveJsonData = "{\"command\": \"30303030303035\", \"reserve\": \"303030303030303030303030303030303030303030303030\", " +
                "\"username\": \"4c6565205761682050656e67\", \"gender\": \"303032\", " +
                "\"programme_code\": \"525344\", \"year_of_study\": \"303033\", \"password\": \"6c656f313233\"}";
        try {
            JSONObject jsonObj = new JSONObject(receiveJsonData);
            if(jsonObj.getString("command").equals("30303030303035")){

                String name = Action.hexToAscii(jsonObj.getString("username"));
                editTextName.setText(name);
                studentDetails.setStudName(name);

                if(jsonObj.getString("gender").equals("303031")){
                    radMale.setChecked(true);
                    studentDetails.setStudGender("male");
                }else if(jsonObj.getString("gender").equals("303032")){
                    radFemale.setChecked(true);
                    studentDetails.setStudGender("female");
                }else{

                }

                String programmeCode = Action.hexToAscii(jsonObj.getString("programme_code")); //Decode from hex to ASCll String
                studentDetails.setStudProgramme(programmeCode.toString());
                if(programmeCode.equals("RSD")){
                    spinner.setSelection(0);
                }else if(programmeCode.equals("RIT")){
                    spinner.setSelection(1);
                }

                editTextYear.setText(jsonObj.getString("year_of_study").charAt(5)+"");

                String password = Action.hexToAscii(jsonObj.getString("password"));
                studentDetails.setStudPasword(password);
                editTextPassword.setText(password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnEncode(View v) {
        String encodeText = "{\"command\": \"30303030303036\", \"reserve\": \"303030303030303030303030303030303030303030303030\", "; // <-- Action code 000006, reserve code 000000000000000000000000.
        String name = editTextName.getText().toString();
        String password = editTextPassword.getText().toString();
        String gender = "";
        if(radMale.isChecked()){
            gender = "303031";
            studentDetails.setStudGender("male");
        }else if(radFemale.isChecked()){
            gender = "303032";
            studentDetails.setStudGender("female");
        }
        String programmeCode = spinner.getSelectedItem().toString();
        String numOfYear = "30303" + editTextYear.getText().toString();

        encodeText += "\"username\": " + Action.asciiToHex(name) + "\" ," +
                "\"gender\": " + gender + "\" ," +
                "\"programme_code\": " + Action.asciiToHex(programmeCode) + "\" ," +
                "\"year_of_study\": " + numOfYear + "\" ," +
                "\"password\": " + Action.asciiToHex(password) + "\"}";

        studentDetails.setStudName(name);
        studentDetails.setStudPasword(password);
        studentDetails.setStudProgramme(programmeCode);
        studentDetails.setStudYearofStudy(editTextYear.getText().toString());

        txtViewEncodeResult.setText("Encode Result: \n" + encodeText);

        }
}

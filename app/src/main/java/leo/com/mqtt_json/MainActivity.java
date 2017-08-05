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
        // 000001 = Login Action
        // 000000000000000000000000 = Researve
        // 124c6565205761682050656e67 = 12 Length of Username (First 2 ASCll character)
        //                            = 4c6565205761682050656e67 Username
        // 066c656f313233 = 06 Length of password (First 2 ASCll character)
        //                = 6c656f313233 Password
        String sendSubscibeData = "{\"command\": \"000005\", \"reserve\": \"000000000000000000000000\", " +
                "\"username\": \"124c6565205761682050656e67\", \"password\": \"066c656f313233\"}";

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
        // 000005 = Respond from server
        // 000000000000000000000000 = Reserve
        // 124c6565205761682050656e67 = 12 Length of Username (First 2 ASCll character)
        //                            = 4c6565205761682050656e67 Username
        //
        // 000002 = Female (Gender)
        //
        // 525344 = RSD (Programme Code)
        //
        // 000003 = 3 (Year of study)
        //
        // 066c656f313233 = 06 Length of password (First 2 ASCll character)
        //                = 6c656f313233 Password

        String receiveJsonData = "{\"command\": \"000005\", \"reserve\": \"000000000000000000000000\", \"size_of_name\": \"12\", " +
                "\"username\": \"4c6565205761682050656e67\", \"gender\": \"000002\", " +
                "\"programme_code\": \"525344\", \"year_of_study\": \"000003\", \"size_of_password\": \"6\", \"password\": \"6c656f313233\"}";
        try {
            JSONObject jsonObj = new JSONObject(receiveJsonData);
            if(jsonObj.getString("command").equals("000005")){

                String name = Action.hexToAscii(Action.splitString(Integer.parseInt(jsonObj.getString("size_of_name")),jsonObj.getString("username")));
                editTextName.setText(name);
                studentDetails.setStudName(name);

                if(jsonObj.getString("gender").equals("000001")){
                    radMale.setChecked(true);
                    studentDetails.setStudGender("male");
                }else if(jsonObj.getString("gender").equals("000002")){
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

                String password = Action.hexToAscii(Action.splitString(Integer.parseInt(jsonObj.getString("size_of_password")),jsonObj.getString("password")));
                studentDetails.setStudPasword(password);
                editTextPassword.setText(password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnEncode(View v) {
        String encodeText = "{\"command\": \"00006\", \"reserve\": \"000000000000000000000000\", "; // <-- Action code 000006, reserve code 000000000000000000000000.
        String name = editTextName.getText().toString();
        String password = editTextPassword.getText().toString();
        String gender = "";
        if(radMale.isChecked()){
            gender = "000001";
            studentDetails.setStudGender("male");
        }else if(radFemale.isChecked()){
            gender = "000002";
            studentDetails.setStudGender("female");
        }
        String programmeCode = spinner.getSelectedItem().toString();
        String numOfYear = "00000" + editTextYear.getText().toString();

        //Turn ASCll name, programme code and passsword to HEX.
        encodeText += "\"size_of_name\": " + name.length() + "\" , \"username\": " + Action.asciiToHex(name) + "\" ," +
                "\"gender\": " + gender + "\" ," +
                "\"programme_code\": " + Action.asciiToHex(programmeCode) + "\" ," +
                "\"year_of_study\": " + numOfYear + "\" ," +
                "\"size_of_password\": " + password.length() + "\" , \"password\": " + Action.asciiToHex(password) + "\"}";

        studentDetails.setStudName(name);
        studentDetails.setStudPasword(password);
        studentDetails.setStudProgramme(programmeCode);
        studentDetails.setStudYearofStudy(editTextYear.getText().toString());

        txtViewEncodeResult.setText("Encode Result: \n" + encodeText);

        }
}

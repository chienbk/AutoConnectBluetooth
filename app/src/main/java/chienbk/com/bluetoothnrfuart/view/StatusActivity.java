package chienbk.com.bluetoothnrfuart.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import chienbk.com.bluetoothnrfuart.R;

public class StatusActivity extends AppCompatActivity {

    private TextView txtVehicleSpeed, txtEngineRPM, txtEngineCoolant, txtFuelTank;
    private EditText editMessage;
    private ListView lvMessage;
    private Button btnSendMessage;

    private BluetoothDevice mDevice = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        //Bundle bundle = intent.getBundleExtra(BluetoothDevice.EXTRA_DEVICE);
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String deviceAddress = bundle.getString(BluetoothDevice.EXTRA_DEVICE);
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        }

        showMessage(mDevice.getName());

        initViews();
        initControls();

    }

    private void initControls() {
        //// TODO: 12/21/2017

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("Send message");
            }
        });
    }

    private void initViews() {
        txtVehicleSpeed = (TextView) findViewById(R.id.txt_vehicleSpeed);
        txtEngineRPM = (TextView) findViewById(R.id.txt_engineRPM);
        txtEngineCoolant = (TextView) findViewById(R.id.txt_engineCoolant);
        txtFuelTank = (TextView) findViewById(R.id.txt_fuelTank);

        editMessage = (EditText) findViewById(R.id.sendText);
        lvMessage = (ListView) findViewById(R.id.listMessage);

        btnSendMessage = (Button) findViewById(R.id.sendButton);
    }

    private void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

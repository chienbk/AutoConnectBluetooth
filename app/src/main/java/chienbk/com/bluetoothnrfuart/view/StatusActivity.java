package chienbk.com.bluetoothnrfuart.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import chienbk.com.bluetoothnrfuart.R;

public class StatusActivity extends AppCompatActivity {

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

    }

    private void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

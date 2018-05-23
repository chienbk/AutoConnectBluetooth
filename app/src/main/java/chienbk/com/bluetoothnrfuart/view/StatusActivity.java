package chienbk.com.bluetoothnrfuart.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

import chienbk.com.bluetoothnrfuart.R;
import chienbk.com.bluetoothnrfuart.service.BluetoothService;
import chienbk.com.bluetoothnrfuart.utils.Contans;
import chienbk.com.bluetoothnrfuart.utils.Utils;
import chienbk.com.bluetoothnrfuart.utils.WriteToFile;

public class StatusActivity extends AppCompatActivity {

    public static final String TAG = StatusActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;

    private TextView txtVehicleSpeed, txtEngineRPM, txtEngineCoolant, txtFuelTank;
    private EditText editMessage;
    private ListView lvMessage;
    private Button btnSendMessage;

    private int mState = UART_PROFILE_DISCONNECTED;
    private BluetoothService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private ArrayAdapter<String> listAdapter;
    private String deviceAddress = "";
    private LinearLayout layout_speed, layout_rpm, layout_coolant, layout_fuel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Check device is available
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            showMessage("Bluetooth is not available");
            finish();
            return;
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        initService();
        initViews();
        initControls();

        if (bundle != null) {
            deviceAddress = bundle.getString(BluetoothDevice.EXTRA_DEVICE);
            Log.d(TAG, "deviceAddress: " + deviceAddress);
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
            showMessage(mDevice.getName() + "connecting...");
        }
    }

    /**
     * The method used to init the service
     */
    private void initService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
            bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    /**
     * UART service connected/disconnected
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            enableButton();
            mService = ((BluetoothService.LocalBinder) iBinder).getService();
            layout_rpm.setClickable(true);
            layout_coolant.setClickable(true);
            layout_fuel.setClickable(true);
            layout_speed.setClickable(true);
            Log.d(TAG, "onServiceConnected mService= " + mService);
            mService.connect(deviceAddress);

            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            dissableButton();
            mService = null;
        }
    };

    private void dissableButton(){
        layout_coolant.setEnabled(false);
        layout_rpm.setEnabled(false);
        layout_fuel.setEnabled(false);
        layout_speed.setEnabled(false);
    }

    private void enableButton() {
        layout_coolant.setEnabled(true);
        layout_rpm.setEnabled(true);
        layout_fuel.setEnabled(true);
        layout_speed.setEnabled(true);
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(BluetoothService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        enableButton();
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        WriteToFile.writeLogCatToFile();
                        editMessage.setEnabled(true);
                        btnSendMessage.setEnabled(true);
                        listAdapter.add("["+currentDateTimeString+"] Connected to: "+ mDevice.getName());
                        lvMessage.smoothScrollToPosition(listAdapter.getCount() - 1);
                        mState = UART_PROFILE_CONNECTED;

                        while (true){

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    writeData(Contans.VERHICLE_SPEED);
                                }
                            }, 3000);
                        }
                    }
                });
            }

            //*********************//
            if (action.equals(BluetoothService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dissableButton();
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        editMessage.setEnabled(false);
                        btnSendMessage.setEnabled(false);
                        listAdapter.add("["+currentDateTimeString+"] Disconnected to: "+ mDevice.getName());
                        mState = UART_PROFILE_DISCONNECTED;
                        WriteToFile.writeLogCatToFile();
                        if (deviceAddress != null){
                            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                        }
                        //mService.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(BluetoothService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8").replaceAll("\\s+","");

                            Log.d(TAG, "dataReceive: " + txValue.toString());
                            showMessage("dataReceive: " + text);
                            Log.d(TAG, "data After format: " + text);
                            WriteToFile.writeLogCatToFile();

                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            listAdapter.add("["+currentDateTimeString+"] Receive: "+text);
                            lvMessage.smoothScrollToPosition(listAdapter.getCount() - 1);

                            if (text.contains(Contans.CONS_VERHICLE_SPEED)) {
                                text = Utils.convertDataReceiveToString(text);
                                txtVehicleSpeed.setText(Utils.convertIntegerToVehicleSpeed(text)+ " km/h");
                            }

                            if (text.contains(Contans.CONS_ENGINE_RPM)){
                                text = Utils.convertDataReceiveToString(text);
                                txtEngineRPM.setText(Utils.convertIntegerToEngineRPM(text) + " rpm");
                            }

                            if (text.contains(Contans.CONS_ENGINE_COOLANT_TEMPERATURE)) {
                                text = Utils.convertDataReceiveToString(text);
                                txtEngineCoolant.setText(Utils.convertIntegerToCoolantTemperature(text) + " °C");
                            }

                            if (text.contains(Contans.CONS_FUEL_TANK_LEVEL_INPUT)) {
                                text = Utils.convertDataReceiveToString(text);
                                txtFuelTank.setText(Utils.convertIntegerToFuelTank(text) + " %");
                            }

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(BluetoothService.DEVICE_DOES_NOT_SUPPORT_UART)){
                showMessage("Device doesn't support UART. Disconnecting");
                dissableButton();
                mService.disconnect();
            }


        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void writeData(String msg){
        showMessage("Data send: " + msg);
        Log.d(TAG, "send message: " + msg);
        WriteToFile.writeLogCatToFile();
        byte[] value;
        try {
            //send data to service
            value = msg.getBytes("UTF-8");
            mService.writeRXCharacteristic(value);
            //Update the log with time stamp
            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
            listAdapter.add("["+currentDateTimeString+"] Send: "+ msg);
            lvMessage.smoothScrollToPosition(listAdapter.getCount() - 1);
            editMessage.setText("");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void initControls() {
        //// TODO: 12/21/2017

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editMessage.getText().toString()+"\n";
                byte[] value;
                try {
                    //send data to service
                    value = message.getBytes("UTF-8");
                    mService.writeRXCharacteristic(value);
                    //Update the log with time stamp
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    listAdapter.add("["+currentDateTimeString+"] Send: "+ message);
                    lvMessage.smoothScrollToPosition(listAdapter.getCount() - 1);
                    editMessage.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        layout_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    String value = Contans.VERHICLE_SPEED;
                    try {
                        mService.writeRXCharacteristic(value.getBytes("UTF-8"));
                        showMessage(value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        layout_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    String value = Contans.FUEL_TANK_LEVEL_INPUT;
                    try {
                        mService.writeRXCharacteristic(value.getBytes("UTF-8"));
                        showMessage(value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        layout_coolant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    String value = Contans.ENGINE_COOLANT_TEMPERATURE;
                    try {
                        mService.writeRXCharacteristic(value.getBytes("UTF-8"));
                        showMessage(value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        layout_rpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    String value = Contans.ENGINE_RPM;
                    try {
                        mService.writeRXCharacteristic(value.getBytes("UTF-8"));
                        showMessage(value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initViews() {
        txtVehicleSpeed = (TextView) findViewById(R.id.txt_vehicleSpeed);
        txtEngineRPM = (TextView) findViewById(R.id.txt_engineRPM);
        txtEngineCoolant = (TextView) findViewById(R.id.txt_engineCoolant);
        txtFuelTank = (TextView) findViewById(R.id.txt_fuelTank);

        editMessage = (EditText) findViewById(R.id.sendText);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        lvMessage = (ListView) findViewById(R.id.listMessage);
        lvMessage.setAdapter(listAdapter);
        lvMessage.setDivider(null);

        btnSendMessage = (Button) findViewById(R.id.sendButton);

        layout_speed = (LinearLayout) findViewById(R.id.layout_speed);
        layout_rpm = (LinearLayout) findViewById(R.id.layout_rpm);
        layout_coolant = (LinearLayout) findViewById(R.id.layout_coolant);
        layout_fuel = (LinearLayout) findViewById(R.id.layout_fuel);

        dissableButton();

    }

    private void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mService.disconnect();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

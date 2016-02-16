package es.robertocerda.projects.discoverbluetoothdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ListView listDevices;
    ArrayAdapter<String> adapter;
    BluetoothAdapter defaultAdapter;
    final int REQUEST_ENABLE_BT = 1;
    Set<BluetoothDevice> devicesArray;
    IntentFilter filter;
    BroadcastReceiver receiver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init components
        init();
        logStatus();

        if(defaultAdapter==null){
            Toast.makeText(this,"No se ha detectado bluetooth",Toast.LENGTH_LONG).show();
        }else{
            //Enable bluetooth function
            enableBluetooth();
        }
    }



    private void init() {
        //Init graphics components
        listDevices = (ListView) findViewById(R.id.listdevices);
        //Init instance Arraylist for ListView
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        //Get default adapter for "BluetoothAdapter"
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        //Set adapter with List
        listDevices.setAdapter(adapter);
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    adapter.add(device.getName()+"-"+device.getAddress());
                }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){

                }
                else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                }
                else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(defaultAdapter.getState()==defaultAdapter.STATE_OFF){
                        enableBluetooth();
                    }
                }
            }
        };
        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver,filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver,filter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_CANCELED){
            Toast.makeText(this,"El bluetooth tiene que estar activado",Toast.LENGTH_LONG).show();
        }
    }

    public void enableBluetooth(){
        if (!defaultAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }



    public void logStatus(){

        if (defaultAdapter == null){
            Log.d("Error","Bluetooth NOT support");
        }else{
            if (defaultAdapter.isEnabled()){
                if(defaultAdapter.isDiscovering()){
                    Log.d("In progress","Bluetooth is currently in device discovery process.");
                }else{
                    defaultAdapter.startDiscovery();
                    Log.d("OK","Bluetooth is Enabled!");
                }
            }else{
                Log.d("Error","Bluetooth is NOT Enabled!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

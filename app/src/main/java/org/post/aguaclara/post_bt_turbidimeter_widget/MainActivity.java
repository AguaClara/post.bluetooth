package org.post.aguaclara.post_bt_turbidimeter_widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.Double;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.String;
import java.lang.Object;


public class MainActivity extends Activity {
   TextView myLabel;
   TextView turbidityLabel;
   EditText myTextbox;
   BluetoothAdapter mBluetoothAdapter;
   BluetoothSocket mmSocket;
   BluetoothDevice mmDevice;
   OutputStream mmOutputStream;
   InputStream mmInputStream;
   Thread workerThread;
   String allOutput = "";
   byte[] readBuffer;
   int readBufferPosition;
   int counter;
   volatile boolean stopWorker;
   Intent incomingIntent;
   /**
    * ATTENTION: This was auto-generated to implement the App Indexing API.
    * See https://g.co/AppIndexing/AndroidStudio for more information.
    */
   private GoogleApiClient client;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // Get the intent that started this activity
      Intent intent = getIntent();
      if (intent.getAction() == "org.post.aguaclara.post_bt_turbidimeter_widget.COLLECT") {
         incomingIntent = intent;
//         //       System.out.println("You got intent!");
//         Double mAnswer = makeJsonObject();
//         incomingIntent.putExtra("value", mAnswer);
//         setResult(RESULT_OK, incomingIntent);
//         //      System.out.println(mAnswer);
//         //     System.out.println(intent.getExtras().get("rawWaterTurbidity"));
//         finish();


      }


      final Button openButton = (Button) findViewById(R.id.open);
      final Button sendButton = (Button) findViewById(R.id.send);
      sendButton.setEnabled(false);
      //Button closeButton = (Button) findViewById(R.id.close);
      //Button testButton = (Button) findViewById(R.id.test);
      Button acceptButton = (Button) findViewById(R.id.accept);
      myLabel = (TextView) findViewById(R.id.label);
      turbidityLabel = (TextView) findViewById(R.id.turbidity);

      myTextbox = (EditText) findViewById(R.id.entry);

      //Error message for open button
      final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Go to 'Settings' -> 'Bluetooth' -> 'Paired Devices'. Make sure device HC-06 is paired. Then press 'Open' again.")
              .setTitle("Error: Device not paired.");
      builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
         }
      });

      final AlertDialog dialog = builder.create();


      //Error message for send button
      AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
      builder2.setMessage("You can't send now")
              .setTitle("Error!");
      builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
         }
      });
      final AlertDialog dialog2 = builder2.create();


      //Open Button
      openButton.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            try {
               findBT();
               openBT();
               openButton.setEnabled(false);
               sendButton.setEnabled(true);
            } catch (Exception ex) {

               //ERROR DIALOG NO IMAGES
               dialog.show();

               //TOAST
//               LayoutInflater inflater = getLayoutInflater();
//               View layout = inflater.inflate(R.layout.error,
//                       (ViewGroup) findViewById(R.id.custom_toast_container));
//
//               TextView text = (TextView) layout.findViewById(R.id.text);
//               text.setText("This is a custom toast");
//
//               Toast toast = new Toast(getApplicationContext());
//               toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//               toast.setDuration(Toast.LENGTH_LONG);
//               toast.setView(layout);
//               toast.show();
//               myLabel.setText("No bluetooth adapter available");

            }
         }
      });

      //Send Button
      sendButton.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            try {
               sendData();
            } catch (Exception ex) {
               dialog2.show();
            }
         }
      });

      //Test Button
//      testButton.setOnClickListener(new View.OnClickListener() {
//         public void onClick(View v) {
//            try {
//               test();
//            } catch (Exception ex) {
//               System.out.println(ex);
//            }
//         }
//      });

      //Accept (and close) Button
      acceptButton.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            try {
               closeBT();
            } catch (Exception ex) {
               System.out.println(ex);
            }
         }
      });

      //Close button
//      closeButton.setOnClickListener(new View.OnClickListener() {
//         public void onClick(View v) {
//            try {
//               closeBT();
//            } catch (Exception ex) {
//               finish();
//            }
//         }
//      });


      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
   }

   void test() {

      //Now get the double from the turbidimeterin stead of my method!
      //a.k.a. get the double by calling beginListenForData to refresh the shown value

      //Test stuff (don't use)
//      Double mAnswer = makeJsonObject();
//      String finalresult = new Double(mAnswer).toString();
//      turbidityLabel.setText(finalresult);

      //Actual stuff (change name of this button to "Refresh")
      // beginListenForData();

      //A handler allows user to repeatedly hit read on the turbidimeter and the value will update
//      final Handler h = new Handler();
//      final int delay = 1000; //milliseconds
//
//      h.postDelayed(new Runnable(){
//         public void run(){
//            getDoubleFromString(allOutput);
//            h.postDelayed(this, delay);
//         }
//      }, delay);

      final Handler h = new Handler();
      final int delay = 1000; //milliseconds

      h.postDelayed(new Runnable() {
         public void run() {
            getDoubleFromString(allOutput);
            h.postDelayed(this, delay);
         }
      }, delay);


   }

   void findBT() {
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if (mBluetoothAdapter == null) {
         myLabel.setText("No bluetooth adapter available");
      }

      if (!mBluetoothAdapter.isEnabled()) {
         Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(enableBluetooth, 0);
      }

      Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
      if (pairedDevices.size() > 0) {
         for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("HC-06")) {
               mmDevice = device;
               break;
            }
         }
      }
      //myLabel.setText(getResources().getString(R.string.bluetooth_connected));
   }

   void openBT() throws IOException {
      UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
      mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
      mmSocket.connect();
      mmOutputStream = mmSocket.getOutputStream();
      mmInputStream = mmSocket.getInputStream();

      beginListenForData();
      try {
         test();
      } catch (Exception ex) {
         System.out.println(ex);
      }



      myLabel.setText(getResources().getString(R.string.bluetooth_connected));
      turbidityLabel.setText("");


//      String stringValue = turbidityLabel.getText().toString();
//      Double result = Double.parseDouble(stringValue);
//      incomingIntent.putExtra("value", result);
//      setResult(RESULT_OK, incomingIntent);
//      closeBT();
//      finish();
   }

   void beginListenForData() {
      final Handler handler = new Handler();
      final byte delimiter = 10; //This is the ASCII code for a newline character

      stopWorker = false;
      readBufferPosition = 0;
      readBuffer = new byte[2048];
      workerThread = new Thread(new Runnable() {
         public void run() {
            while (!Thread.currentThread().isInterrupted() && !stopWorker) {
               try {
                  int bytesAvailable = mmInputStream.available();
                  if (bytesAvailable > 0) {
                     byte[] packetBytes = new byte[bytesAvailable];
                     mmInputStream.read(packetBytes);
                     for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        if (b == delimiter) {
                           byte[] encodedBytes = new byte[readBufferPosition];
                           System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                           final String data = new String(encodedBytes, "US-ASCII");
                           allOutput = data + " " + allOutput;
                           //CHANGE: Sends the allOutput String to my method

                           getDoubleFromString(allOutput);


                           readBufferPosition = 0;


                        } else {
                           readBuffer[readBufferPosition++] = b;
                        }
                     }
                     handler.post(new Runnable() {
                        public void run() {

                           if (allOutput.contains("NTU")) {
                              int ntuIndex = allOutput.indexOf("NTU") - 4;
                              myLabel.setText(String.valueOf(parseNtu(allOutput)));
                              Bundle result = new Bundle();
                              result.putFloat("textFieldInGroup", parseNtu(allOutput));
                              sendToODK(result);
                           }
                        }
                     });
                  }
               } catch (IOException ex) {
                  stopWorker = true;
               }
            }
         }
      });

      workerThread.start();
   }

   void sendData() throws IOException {

      String stringValue = turbidityLabel.getText().toString();
      Double result = Double.parseDouble(stringValue);
      incomingIntent.putExtra("value", result);
      setResult(RESULT_OK, incomingIntent);

      //Only including this closeBT() will allow end user to refresh without having to reopen BT
      //but greater chance of error if the user closes the app without closing BT.
      closeBT();
      finish();


//      String msg = myTextbox.getText().toString();
//      msg += "\n";
//      mmOutputStream.write(msg.getBytes());
//      myLabel.setText("Data Sent");
   }

   // puts the values asked for by ODK into the returning intent and passes it off to ODK and shuts
// down the app
   void sendToODK(Bundle extras) {
      Intent outgoingintent = incomingIntent;
      for (String key : incomingIntent.getExtras().keySet()) {
         if (extras.containsKey(key)) {
            outgoingintent.putExtra(key, (String) extras.get(key));
         }
      }
      setResult(RESULT_OK, outgoingintent);

   }

   void closeBT() throws IOException {

      stopWorker = true;
      mmOutputStream.close();
      mmInputStream.close();
      mmSocket.close();
      // myLabel.setText("Press OPEN to get another reading.\n\nPress SEND if finished.");
      myLabel.setText("Complete!");

   }

   public static float parseNtu(String string) {
      String first_match = parseWithRegex(string, "(\\d+)(.)(\\d+)(\\s*)(NTU)");
      String second_match = parseWithRegex(string, "([)(\\d)(.)(\\d)(])");
      return Float.parseFloat(second_match);
   }

   //  Takes a string and a Java Regex pattern string and returns the first match. Returns empty string
// if no match exists
   public static String parseWithRegex(String string, String pattern) {
      // Create a Pattern object
      Pattern r = Pattern.compile(pattern);

      // Now create matcher object.
      Matcher m = r.matcher(string);
      if (m.find()) {
         return m.group(0);
      } else return "";
   }

   public double makeJsonObject() {
      System.out.println("You got here!");

      try {
         JSONObject obj = new JSONObject();


         JSONObject rd = new JSONObject();

         JSONObject tur = new JSONObject();
         JSONObject u = new JSONObject();
         JSONArray t = new JSONArray();
         JSONArray d = new JSONArray();

         JSONArray e = new JSONArray();
         JSONArray ge = new JSONArray();

         rd.put("tur", tur);
         tur.put("u", "ntu");
         tur.put("t", t);
         tur.put("d", d);
         tur.put("e", e);


         t.put(0);
         d.put(1.40);


         obj.put("dt", "hhT");
         obj.put("id", "01010101");
         obj.put("rt", 0);
         obj.put("ts", 0);
         obj.put("rd", rd);
         obj.put("ge", ge);
         obj.put("al", "{}");
         System.out.println(obj);

         System.out.println(obj.getJSONObject("rd").getJSONObject("tur").getJSONArray("d").getDouble(0));
         return obj.getJSONObject("rd").getJSONObject("tur").getJSONArray("d").getDouble(0);
      } catch (JSONException e) {
         System.out.println(e);
         finish();
      }
      //fix this later
      return 0.0;
   }

   //This method will take the output (String) from the Turbidimeter, turn it into a JSON Object,
   //and send the double
   public void getDoubleFromString(String output) {
      try {
         JSONObject obj = new JSONObject(output);

         Double mAnswer = obj.getJSONObject("rd").getJSONObject("tur").getJSONArray("d").getDouble(0);
         String finalresult = new Double(mAnswer).toString();
         turbidityLabel.setText(finalresult);
         //this closeBT() closes worker each time. More work, but less chance of error.
         //closeBT();


         // incomingIntent.putExtra("value", mAnswer);
         // setResult(RESULT_OK, incomingIntent);
         // closeBT();
         // finish();
      } catch (Exception e) {
         System.out.println(e);
         //   finish();
      }
   }

   //This code closes the thread every time the app is in background and restarts when reopened!te
@Override
   protected void onPause() {
   super.onPause();
      try {
         closeBT();
      } catch (Exception ex) {
         System.out.println(ex);
      }
   }

   protected void onResume() {
      super.onResume();
      try {
         openBT();
      } catch (Exception ex) {
         System.out.println(ex);
      }
   }
   @Override




   public void onStart() {
      super.onStart();

      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      client.connect();
      Action viewAction = Action.newAction(
              Action.TYPE_VIEW, // TODO: choose an action type.
              "Main Page", // TODO: Define a title for the content shown.
              // TODO: If you have web page content that matches this app activity's content,
              // make sure this auto-generated web page URL is correct.
              // Otherwise, set the URL to null.
              Uri.parse("http://host/path"),
              // TODO: Make sure this auto-generated app URL is correct.
              Uri.parse("android-app://org.post.aguaclara.post_bt_turbidimeter_widget/http/host/path")
      );
      AppIndex.AppIndexApi.start(client, viewAction);
   }

   @Override
   public void onStop() {
      super.onStop();

      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      Action viewAction = Action.newAction(
              Action.TYPE_VIEW, // TODO: choose an action type.
              "Main Page", // TODO: Define a title for the content shown.
              // TODO: If you have web page content that matches this app activity's content,
              // make sure this auto-generated web page URL is correct.
              // Otherwise, set the URL to null.
              Uri.parse("http://host/path"),
              // TODO: Make sure this auto-generated app URL is correct.
              Uri.parse("android-app://org.post.aguaclara.post_bt_turbidimeter_widget/http/host/path")
      );
      AppIndex.AppIndexApi.end(client, viewAction);
      client.disconnect();
   }
}

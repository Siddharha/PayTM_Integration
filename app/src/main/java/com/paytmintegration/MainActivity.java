package com.paytmintegration;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGActivity;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.PaytmWebView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button btnPTM;
    private int randomInt = 0;
    private PaytmPGService Service = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        clickCall();

    }

    private void clickCall() {

        btnPTM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(MainActivity.this, PaytmPGActivity.class));


                Random randomGenerator = new Random();
                randomInt = randomGenerator.nextInt(100000);
                //for testing environment
                Service = PaytmPGService.getStagingService();
                //for production environment
    /*Service = PaytmPGService.getProductionService();*/

    /*PaytmMerchant constructor takes two parameters
    1) Checksum generation url
    2) Checksum verification url
    Merchant should replace the below values with his values*/


                PaytmMerchant Merchant = new PaytmMerchant("https://pguat.paytm.com/merchant-chksum/ChecksumGenerator","https://pguat.paytm.com/merchant-chksum/ValidateChksum");
                //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values

                Map<String, String> paramMap = new HashMap<String, String>();

                //these are mandatory parameters
                paramMap.put("REQUEST_TYPE", "DEFAULT");
                paramMap.put("ORDER_ID", String.valueOf(randomInt));
                //MID provided by paytm
                paramMap.put("MID", "id provided by paytm");
                paramMap.put("CUST_ID", "CUST123");
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                paramMap.put("WEBSITE", "paytm");
                paramMap.put("TXN_AMOUNT", "1");
                paramMap.put("THEME", "merchant");
                PaytmOrder Order = new PaytmOrder(paramMap);


                Service.initialize(Order, Merchant,null);
                Service.startPaymentTransaction(MainActivity.this, false, false, new PaytmPaymentTransactionCallback() {
                    @Override
                    public void onTransactionSuccess(Bundle bundle) {
                       Log.e("STATUS","Transaction Success :" + bundle);
                    }

                    @Override
                    public void onTransactionFailure(String s, Bundle bundle) {
                        Log.e("STATUS","Transaction Failure :" + s + "\n" + bundle);
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.e("STATUS","network unavailable :");
                    }

                    @Override
                    public void clientAuthenticationFailed(String s) {
                        Log.e("STATUS","clientAuthenticationFailed :" + s);
                    }

                    @Override
                    public void someUIErrorOccurred(String s) {
                        Log.e("STATUS","someUIErrorOccurred :" + s);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int i, String s, String s2) {
                        Log.e("STATUS","errorLoadingWebPage :" + i + "\n" + s + "\n" + s2);
                    }
                });
            }
        });


    }

    private void initialize() {
        btnPTM = (Button)findViewById(R.id.btnPTM);

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

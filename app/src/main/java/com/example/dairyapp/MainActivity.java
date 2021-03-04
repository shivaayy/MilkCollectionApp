package com.example.dairyapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    String bill="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        String bill="test bill";

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED);

        Button makebill=(Button)findViewById(R.id.makebill);

        Button sendSMS=(Button)findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send_sms();
            }
        });

        Button savingbill=(Button)findViewById(R.id.savebill);
        savingbill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_bill();
            }
        });


        Button showing_bill_history=(Button)findViewById(R.id.billhistory);
        showing_bill_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bill_history();
            }
        });


        Button searching_phone_no=(Button)findViewById(R.id.searchno);
        searching_phone_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_phone_no();
            }
        });

        makebill.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                TextView bill_field=(TextView)findViewById(R.id.bill);
                TextView avgfat=(TextView)findViewById(R.id.avgfat);
                float avgfat_value=Float.parseFloat(avgfat.getText().toString());

                TextView avgSNF=(TextView)findViewById(R.id.avgSNF);
                float avgSNF_value=Float.parseFloat(avgSNF.getText().toString());

                TextView milkprice=(TextView)findViewById(R.id.milkprice);
                float milkprice_value=Float.parseFloat(milkprice.getText().toString());

                TextView milkSNF=(TextView)findViewById(R.id.milkSNF);
                float milkSNF_value=Float.parseFloat(milkSNF.getText().toString());

                TextView milkfat=(TextView)findViewById(R.id.milkfat);
                float milkfat_value=Float.parseFloat(milkfat.getText().toString());

                TextView milkquantity=(TextView)findViewById(R.id.milkquantity);
//                float milkquantity_value=Float.parseFloat(milkquantity.getText().toString());
                float milkquantity_value=sum_of_string(milkquantity.getText().toString());


                Switch includefat=(Switch)findViewById(R.id.includefat);
                boolean includefat_value=includefat.isChecked();
                Switch includeSNF=(Switch)findViewById(R.id.includeSNF);
                boolean includeSNF_value=includeSNF.isChecked();

                float calculated_per_kg_fat_price=0.0f;
                float calculated_milk_price=0.0f;
                float total_price=0.0f;
                String fat_checked="No";
                String SNF_checked="No";


                if ( includeSNF_value && includefat_value){

                    calculated_per_kg_fat_price=(milkprice_value*300)/((3*avgfat_value)+(2*avgSNF_value));

//                    calculated_milk_price=((milkfat_value*calculated_per_kg_fat_price)/100)+
//                            ((2*milkSNF_value*calculated_per_kg_fat_price)/300);

                    calculated_milk_price=((3*milkfat_value*milkprice_value)+(2*milkSNF_value*milkprice_value))/((3*avgfat_value)+(2*avgSNF_value));
                    calculated_milk_price= (float) (Math.round(calculated_milk_price*100.0)/100.0);
//                    total_price=calculated_milk_price*milkquantity_value;
                    fat_checked="Yes";
                    SNF_checked="Yes";

                }
                else if (includefat_value){
//                    calculated_per_kg_fat_price=(milkprice_value*100)/(avgfat_value);

                    calculated_milk_price=((3*milkfat_value*milkprice_value)+(2*avgSNF_value*milkprice_value))/((3*avgfat_value)+(2*avgSNF_value));
                    calculated_milk_price= (float) (Math.round(calculated_milk_price*100.0)/100.0);

//                    calculated_milk_price=((milkfat_value*calculated_per_kg_fat_price)/100);
//                    total_price=calculated_milk_price*milkquantity_value;
                    fat_checked="Yes";
                    SNF_checked="No";

                }
                else if (includeSNF_value){
//                    calculated_per_kg_fat_price=(milkprice_value*100)/(avgSNF_value);

                    calculated_milk_price=((3*avgfat_value*milkprice_value)+(2*milkSNF_value*milkprice_value))/((3*avgfat_value)+(2*avgSNF_value));
                    calculated_milk_price= (float) (Math.round(calculated_milk_price*100.0)/100.0);

//                    calculated_milk_price=((milkSNF_value*calculated_per_kg_fat_price)/100);
                    fat_checked="No";
                    SNF_checked="Yes";


                }



                button_vibrate();
                Calendar calendar = Calendar.getInstance();
                String currentDate=new SimpleDateFormat("dd-MMM-yyyy").format(calendar.getTime())+
                        "   "+new SimpleDateFormat("hh:mm:ss").format(calendar.getTime());
                Switch includetear=(Switch)findViewById(R.id.includetear);
                TextView tearweight=(TextView)findViewById(R.id.tearWeight);

                if(includetear.isChecked()){
                    float tearweight_value=sum_of_string(tearweight.getText().toString());
                    float total_milk_quantity=(float) milkquantity_value-(float) tearweight_value;
                    total_milk_quantity= (float) (Math.round(total_milk_quantity*1000.0)/1000.0);

                    total_price=calculated_milk_price*total_milk_quantity;
                    total_price= (float) (Math.round(total_price*100.0)/100.0);

                    bill="********************* Bill **********************\n"
                            +"\nDate : "+currentDate
                            +"\n\nAvg Fat: "+avgfat_value+"    Avg SNF : "+avgSNF_value
                            +"\nInclude Fat : "+fat_checked+"    Include SNF : "+SNF_checked
                            +"\nMilk Price : "+milkprice_value
                            +"\n\nMilk Fat : "+milkfat_value+"    Milk SNF : "+milkSNF_value
                            +"\nCalculated Price : " +calculated_milk_price+"\nGross Weight : "+milkquantity.getText().toString()
                            +" = "+milkquantity_value+"\nTare Weight : "+tearweight.getText().toString()+" = "+tearweight_value
                            +"\nTotal Milk Quantity : "+total_milk_quantity
                            +"\nTotal : "+total_price;

                }
                else{
                    total_price=calculated_milk_price*milkquantity_value;
                    total_price= (float) (Math.round(total_price*100.0)/100.0);
                    bill="********************* Bill **********************\n"
                            +"\nDate : "+currentDate
                            +"\n\nAvg Fat: "+avgfat_value+"    Avg SNF : "+avgSNF_value
                            +"\nInclude Fat : "+fat_checked+"    Include SNF : "+SNF_checked
                            +"\nMilk Price : "+milkprice_value
                            +"\n\nMilk Fat : "+milkfat_value+"    Milk SNF : "+milkSNF_value
                            +"\nCalculated price : " +calculated_milk_price+"\nMilk Quantity : "+milkquantity.getText().toString()+" = "+milkquantity_value
                            +"\nTotal : "+total_price;

                }


                bill_field.setText(bill);

                    bill_field.setBackgroundColor(Color.rgb(0,179,0));







            }
        });


    }

    void Send_sms(){


        button_vibrate();
        TextView ph = (TextView)findViewById(R.id.phno);
        String str=bill;
        String number=ph.getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(str);
        smsManager.sendMultipartTextMessage(number, null, parts, null, null);
        Toast.makeText(getApplicationContext(), "bill sms sent successfully", Toast.LENGTH_LONG).show();


    }

    float sum_of_string(String str){
        String [] temp=str.split(",");
        float value=0.0f;
        for (String s:temp){
            value=value+Float.parseFloat(s);
        }
        return value;
    }
    void button_vibrate(){
        Vibrator vib=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        vib.vibrate(50);

    }
    void save_bill(){
        button_vibrate();

    }
    void bill_history(){
        button_vibrate();


    }
    void search_phone_no(){
        button_vibrate();

    }
}
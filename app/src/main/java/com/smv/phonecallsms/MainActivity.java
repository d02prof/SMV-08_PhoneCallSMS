package com.smv.phonecallsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_SMS = 2;

    private EditText editTextStevilka;
    private ImageView imageViewPoklici;
    private EditText editTextSMS;
    private ImageView imageViewPosljiSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextStevilka = findViewById(R.id.editTextStevilka);
        imageViewPoklici = findViewById(R.id.imageViewTelefon);
        editTextSMS = findViewById(R.id.editTextSMS);
        imageViewPosljiSMS = findViewById(R.id.imageViewSMS);

        imageViewPoklici.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Telefoniraj();
            }
        });

        imageViewPosljiSMS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PosljiSMS();
            }
        });
    }

    public void Telefoniraj()
    {
        String stevilka = editTextStevilka.getText().toString();

        //če je vpisana številka
        if (stevilka.trim().length() > 0)
        {
            //preverimo če imamo pravice za klicanje
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                //če jih še nimamo
                //lahko bi zahtevali več dovoljenj, zato polje stringov, v tem primeru rabim ole eno
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }
            else
            {
                //če jih že imamo
                String poklici = "tel:" + stevilka;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(poklici)));
            }
        }
        else Toast.makeText(this, "Vpiši številko", Toast.LENGTH_SHORT).show();
    }

    public void PosljiSMS()
    {
        String stevilka = editTextStevilka.getText().toString();
        String besedilo = editTextSMS.getText().toString();

        //če ni vpisana številka
        if (stevilka.trim().length() == 0)
        {
            Toast.makeText(this, "Vpiši številko", Toast.LENGTH_SHORT).show();
            return;
        }
        //če ni vpisano besedilo
        if (besedilo.trim().length() == 0)
        {
            Toast.makeText(this, "Vpiši besedilo sporočila", Toast.LENGTH_SHORT).show();
            return;
        }

        //preverimo če imamo pravice za klicanje
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS);
        }
        else
        {
            SmsManager smsManager = SmsManager.getDefault();
            //do izjeme pride ob prazni številki ali besedilu v metodi endTextMessage,
            //tako da try/catch v bistvu ne rabimo,saj smo za oboje že poskrbeli zgoraj
            try
            {
                smsManager.sendTextMessage(stevilka, null, besedilo, null, null);
                Toast.makeText(getApplicationContext(), "Pošiljam SMS ...\n\n" + "za " + stevilka + ": \n" + besedilo, Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "NAPAKA: \n\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //metoda, ki obdela rezultat zahteve za dovoljenja (tokrat imamo dve: za klic in za sms)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //če smo kaj dobili nazaj, v switch case pogledamo, kaj to je
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            switch (requestCode)
            {
                case REQUEST_CALL:
                    Telefoniraj();
                    break;
                case REQUEST_SMS:
                    PosljiSMS();
                    break;
            }
        }
        else
        {
            Toast.makeText(this, "Dovoljenje ZAVRNJENO", Toast.LENGTH_SHORT).show();
        }
    }
}
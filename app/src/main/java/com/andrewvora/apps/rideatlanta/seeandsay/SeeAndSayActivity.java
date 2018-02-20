package com.andrewvora.apps.rideatlanta.seeandsay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.andrewvora.apps.rideatlanta.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Gives information about how to report to See & Say.
 *
 * Created by faytx on 11/16/2016.
 * @author Andrew Vorakrajangthiti
 */
public class SeeAndSayActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_and_say);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_see_and_say);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.see_say_text_police)
    void onSendTextToPoliceClicked() {
        Intent openNewTextToNumberIntent = new Intent(Intent.ACTION_SENDTO);
        String smsNumberUri = "sms:" + getString(R.string.sms_number_see_and_say);
        openNewTextToNumberIntent.setData(Uri.parse(smsNumberUri));
        configureExternalIntent(openNewTextToNumberIntent);

        if(openNewTextToNumberIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openNewTextToNumberIntent);
        }
    }

    @OnClick(R.id.see_say_call_police)
    void onMakeCallToPoliceClicked() {
        Intent openDialerWithNumberIntent = new Intent(Intent.ACTION_DIAL);
        String phoneNumberUri = "tel:" + getString(R.string.phone_number_see_and_say);
        openDialerWithNumberIntent.setData(Uri.parse(phoneNumberUri));
        configureExternalIntent(openDialerWithNumberIntent);

        if(openDialerWithNumberIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openDialerWithNumberIntent);
        }
    }

    private void configureExternalIntent(@NonNull Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
    }
}

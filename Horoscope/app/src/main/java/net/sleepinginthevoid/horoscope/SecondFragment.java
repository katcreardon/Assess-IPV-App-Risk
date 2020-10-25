/* Facade for app listing tool
*  Display sign buttons */

package net.sleepinginthevoid.horoscope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String str = SecondFragmentArgs.fromBundle(getArguments()).getMyArg();
        TextView horoscopeView = view.getRootView().findViewById(R.id.textviewHoroscope);

        switch (str) {
            case "Aries" :
                horoscopeView.setText(getString(R.string.ariesText));
                break;
            case "Taurus" :
                horoscopeView.setText(getString(R.string.taurusText));
                break;
            case "Gemini" :
                horoscopeView.setText(getString(R.string.geminiText));
                break;
            case "Cancer" :
                horoscopeView.setText(getString(R.string.cancerText));
                break;
            case "Leo" :
                horoscopeView.setText(getString(R.string.leoText));
                break;
            case "Virgo" :
                horoscopeView.setText(getString(R.string.virgoText));
                break;
            case "Libra" :
                horoscopeView.setText(getString(R.string.libraText));
                break;
            case "Scorpio" :
                horoscopeView.setText(getString(R.string.scorpioText));
                break;
            case "Sagittarius" :
                horoscopeView.setText(getString(R.string.sagittariusText));
                break;
            case "Capricorn" :
                horoscopeView.setText(getString(R.string.capricornText));
                break;
            case "Aquarius" :
                horoscopeView.setText(getString(R.string.aquariusText));
                break;
            case "Pisces" :
                horoscopeView.setText(getString(R.string.piscesText));
                break;
        }

        view.findViewById(R.id.buttonPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}
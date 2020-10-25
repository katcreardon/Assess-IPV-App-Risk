/* Facade for app listing tool
*  Display horoscope text */

package net.sleepinginthevoid.horoscope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.buttonAries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Aries";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonTaurus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Taurus";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonGemini).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Gemini";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonCancer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Cancer";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonLeo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Leo";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonVirgo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Virgo";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonLibra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Libra";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonScorpio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Scorpio";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonSagittarius).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Sagittarius";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonCapricorn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Capricorn";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonAquarius).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Aquarius";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });

        view.findViewById(R.id.buttonPisces).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "Pisces";
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(str);
                NavHostFragment.findNavController(FirstFragment.this).navigate(action);
            }
        });
    }
}
package com.jcyu.eventfinder.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jcyu.eventfinder.R;
import com.jcyu.eventfinder.network.ApiService;
import com.jcyu.eventfinder.ui.search.EventsResult.EventsResultsFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private AutoCompleteTextView keywordInput;

    private ArrayList<String> hints;

    private ArrayList<JSONObject> eventResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_search, container, false);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        View toastCustomLayout = inflater.inflate(R.layout.customized_toast, null);
        Spinner categorySpinner = root.findViewById(R.id.spinner_cate);

        // Create a list of categories
        List<String> Allcategories = new ArrayList<>();
        Allcategories.add("All");
        Allcategories.add("Music");
        Allcategories.add("Sports");
        Allcategories.add("Arts & Theatre");
        Allcategories.add("Film");
        Allcategories.add("Miscellaneous");

        // Create an ArrayAdapter using the categories list and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.color_spinner_layout, Allcategories);
//        android.R.layout.simple_spinner_item
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Spinner
        categorySpinner.setAdapter(adapter);

        //deal with auto-detect logic
        LinearLayout GroupLocation = root.findViewById(R.id.locationGroup);
        Switch autoDetectSwitch = root.findViewById(R.id.switch_autodetect);
        //add a listener to the switch
        autoDetectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If the switch is checked, hide the location EditText
                GroupLocation.setVisibility(View.GONE);
            } else {
                // If the switch is not checked, show the location EditText
                GroupLocation.setVisibility(View.VISIBLE);
            }
        });
        //begin to collect all data
        // search button
        Button searchButton = root.findViewById(R.id.button_search);
        // location field
        EditText locationField = root.findViewById(R.id.location);
        //input keyword
        AutoCompleteTextView keywordInput = root.findViewById(R.id.input_keyword);
        //input distance
        EditText inputDistance = root.findViewById(R.id.distance);

        eventResult = new ArrayList<>();
        searchButton.setOnClickListener(v -> {
            //location
            String locationText = locationField.getText().toString().trim();
            // I define autoDetectSwitch above autoDetectSwitch
            boolean autoDetect = autoDetectSwitch.isChecked();
            String keywordText = keywordInput.getText().toString().trim();
            //selected spinner element: categorySpinner
            Object selectedItem = categorySpinner.getSelectedItem();
            String categoryText = selectedItem.toString().trim();
            String distanceText = inputDistance.getText().toString().trim();
            // Check if keyword and location are empty or filled with spaces
            if (keywordText.isEmpty() || (!autoDetect && locationText.isEmpty())) {
                Log.d("SearchButton", String.valueOf(autoDetect));
                Log.d("SearchButton", keywordText);
                Log.d("SearchButton", locationText);
                TextView TextOfToast = toastCustomLayout.findViewById(R.id.textOfToast);
                TextOfToast.setText("Please fill all fields");
                Toast toast = new Toast(getActivity());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(toastCustomLayout);
                toast.show();
//                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Perform your search action or any other operation here
                Log.d("SearchButton", "No empty fields, we can do search");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                EventsResultsFragment eventsResultsFragment = new EventsResultsFragment();
                fragmentTransaction.replace(R.id.replace_container, eventsResultsFragment, "EventsResultsFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // Add the visibility code here
                View cardSearch = getActivity().findViewById(R.id.card_search);
                cardSearch.setVisibility(View.GONE);

                View replacedContainer = getActivity().findViewById(R.id.replace_container);
                replacedContainer.setVisibility(View.VISIBLE);


                ApiService.generateEventResult(keywordText, locationText, autoDetect, categoryText, distanceText, getActivity(), new ApiService.CallbackEventResult() {
                    @Override
                    public void onResponse(ArrayList<JSONObject> events) {
                          Log.d("eventSearch", "override onResponse in SearchFragment" );
                          Log.d("HttpResponse", "event: " + events.toString());
                          eventResult.clear();
                          eventResult.addAll(events);

                        Log.d("HttpResponse", "eventResult: " + eventResult.toString());
                        EventsResultsFragment.passSearchResults(eventResult,getActivity(), getParentFragmentManager());
//                          Log.d("SearchButton", eventResult);
//                        hints.clear();
//                        hints.addAll(newSuggestions);
//                        ArrayAdapter<String> adapterAuto = new ArrayAdapter<>(getActivity(), R.layout.autocomplete_dropdown, hints);
//                        keywordInput.setAdapter(adapterAuto);
//                        adapterAuto.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String error) {
                        // Handle error, e.g., show a toast with an error message
                        Log.d("eventSearch", "SearchFragment No eventSearch result");
                    }
                });
            }
        });

        //clear button
        Button ButtonClear = root.findViewById(R.id.button_clear);
        ButtonClear.setOnClickListener(v -> {
            // Reset the auto-detect switch
            autoDetectSwitch.setChecked(false);
            // Reset the category spinner
            categorySpinner.setSelection(0);
            // Reset the location field
            locationField.setText("");
            // Reset the keyword input field
            keywordInput.setText("");
            inputDistance.setText("10");
        });

        hints = new ArrayList<>();

        //autocomplete for keywordInput
        keywordInput.addTextChangedListener(new TextWatcher() {
            //the structure if this methods is copied from given tutorial
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    ApiService.generateAutoComplete(s.toString(), getActivity(), new ApiService.CallbackAutoComplete() {
                        @Override
                        public void onResponse(ArrayList<String> newSuggestions) {
                            hints.clear();
                            hints.addAll(newSuggestions);
                            ArrayAdapter<String> adapterAuto = new ArrayAdapter<>(getActivity(), R.layout.autocomplete_dropdown, hints);
                            keywordInput.setAdapter(adapterAuto);
                            adapterAuto.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String error) {
                            // Handle error, e.g., show a toast with an error message
                            Log.d("auto", "No autocomplete result");
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return root;
    }


}